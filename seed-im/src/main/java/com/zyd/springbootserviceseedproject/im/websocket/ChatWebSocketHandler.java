package com.zyd.springbootserviceseedproject.im.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zyd.springbootserviceseedproject.im.config.WebSocketAuthInterceptor;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import com.zyd.springbootserviceseedproject.im.service.IImChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * IM 聊天 WebSocket Handler
 *
 * 职责：
 *   1. 维护 userId → 在线 session 集合（同一用户可多端同时在线）
 *   2. 接收客户端 chat 帧 → 调 IImChatService 落库 → 推送给接收方在线 session
 *   3. 处理 ping/pong 心跳
 *   4. 回复 ack 投递确认
 *
 * 协议帧见类顶部注释。消息内容长度上限 64KB（由容器 TextMessage 限制）。
 *
 * @author zhaoyudong
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private IImChatService imChatService;

    /** userId → 该用户的全部在线 session（多端登录时会有多个） */
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    private final SimpleDateFormat timeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            closeQuietly(session);
            return;
        }
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("WS 连接建立：userId={}, sessionId={}, 在线端数={}", userId, session.getId(), userSessions.get(userId).size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Long senderId = getUserId(session);
        if (senderId == null) {
            closeQuietly(session);
            return;
        }
        String payload = message.getPayload();
        JSONObject frame;
        try {
            frame = JSON.parseObject(payload);
        } catch (Exception e) {
            log.warn("WS 消息解析失败：userId={}, payload={}", senderId, payload);
            return;
        }
        String type = frame.getString("type");
        if (type == null) {
            return;
        }
        switch (type) {
            case "chat" -> handleChat(session, senderId, frame);
            case "ping" -> sendJson(session, pongFrame());
            default -> log.debug("WS 未知帧类型：{} from userId={}", type, senderId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserId(session);
        if (userId == null) {
            return;
        }
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        log.info("WS 连接关闭：userId={}, sessionId={}, code={}, reason={}",
                userId, session.getId(), status.getCode(), status.getReason());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = getUserId(session);
        log.error("WS 传输异常：userId={}, sessionId={}", userId, session.getId(), exception);
        closeQuietly(session);
    }

    // ---- 业务处理 ----

    /**
     * 处理客户端发来的 chat 帧：落库 → 回 ack 给发送方 → 推送给接收方。
     */
    private void handleChat(WebSocketSession session, Long senderId, JSONObject frame) {
        String clientMsgId = frame.getString("clientMsgId");
        Long receiverId = frame.getLong("receiverId");
        Integer msgType = frame.getInteger("msgType");
        String content = frame.getString("content");

        if (receiverId == null || content == null || content.isEmpty()) {
            sendJson(session, ackFrame(clientMsgId, null, false, "receiverId/content 不能为空"));
            return;
        }

        ImMessage message;
        try {
            message = imChatService.saveMessage(senderId, receiverId, msgType, content);
        } catch (Exception e) {
            log.error("WS 消息落库失败：senderId={}, receiverId={}", senderId, receiverId, e);
            sendJson(session, ackFrame(clientMsgId, null, false, e.getMessage()));
            return;
        }

        // 1. 回 ack 给发送方
        sendJson(session, ackFrame(clientMsgId, message.getMsgId(), true, null));

        // 2. 推送给接收方所有在线 session
        JSONObject push = chatPushFrame(message, clientMsgId);
        sendToUser(receiverId, push);

        // 3. 推送给发送方其它端（多端同步）
        sendToUser(senderId, push);
    }

    // ---- 推送工具 ----

    /**
     * 给指定用户的所有在线 session 发消息
     */
    private void sendToUser(Long userId, JSONObject json) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String text = json.toJSONString();
        for (WebSocketSession s : sessions) {
            sendRaw(s, text);
        }
    }

    private void sendJson(WebSocketSession session, JSONObject json) {
        sendRaw(session, json.toJSONString());
    }

    private void sendRaw(WebSocketSession session, String text) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(text));
        } catch (IOException e) {
            log.warn("WS 发送失败：sessionId={}, msg={}", session.getId(), e.getMessage());
        }
    }

    // ---- 协议帧构造 ----

    private JSONObject ackFrame(String clientMsgId, Long msgId, boolean success, String errMsg) {
        JSONObject o = new JSONObject();
        o.put("type", "ack");
        o.put("clientMsgId", clientMsgId);
        o.put("msgId", msgId);
        o.put("success", success);
        if (errMsg != null) {
            o.put("errMsg", errMsg);
        }
        return o;
    }

    private JSONObject chatPushFrame(ImMessage message, String clientMsgId) {
        JSONObject o = new JSONObject();
        o.put("type", "chat");
        o.put("msgId", message.getMsgId());
        o.put("conversationId", message.getConversationId());
        o.put("senderId", message.getSenderId());
        o.put("receiverId", message.getReceiverId());
        o.put("msgType", message.getMsgType());
        o.put("content", message.getContent());
        o.put("sendTime", timeFmt.format(message.getSendTime()));
        if (clientMsgId != null) {
            o.put("clientMsgId", clientMsgId);
        }
        return o;
    }

    private JSONObject pongFrame() {
        JSONObject o = new JSONObject();
        o.put("type", "pong");
        return o;
    }

    // ---- 工具 ----

    private Long getUserId(WebSocketSession session) {
        Object val = session.getAttributes().get(WebSocketAuthInterceptor.ATTR_USER_ID);
        return val instanceof Long ? (Long) val : null;
    }

    private void closeQuietly(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.POLICY_VIOLATION);
            } catch (IOException ignored) {
            }
        }
    }
}
