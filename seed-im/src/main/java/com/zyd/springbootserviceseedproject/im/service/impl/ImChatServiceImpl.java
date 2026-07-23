package com.zyd.springbootserviceseedproject.im.service.impl;

import com.zyd.springbootserviceseedproject.common.exception.ServiceException;
import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
import com.zyd.springbootserviceseedproject.im.domain.vo.SimpleUserVO;
import com.zyd.springbootserviceseedproject.im.enums.ImMsgType;
import com.zyd.springbootserviceseedproject.im.mapper.ImConversationMapper;
import com.zyd.springbootserviceseedproject.im.mapper.ImMessageMapper;
import com.zyd.springbootserviceseedproject.im.service.IImChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * IM 聊天业务实现（v2 主流设计）
 *
 * 会话表一条会话一条记录（min_user_id + max_user_id 唯一），
 * 消息表 conversation_id 指向唯一会话，历史查询按 conversation_id 单字段过滤。
 *
 * @author zhaoyudong
 */
@Service
public class ImChatServiceImpl implements IImChatService {

    /** 历史消息默认每页条数 */
    private static final int DEFAULT_PAGE_SIZE = 20;

    /** 消息摘要最大长度（图片统一显示为 [图片]） */
    private static final int SUMMARY_MAX_LEN = 100;

    @Autowired
    private ImConversationMapper conversationMapper;

    @Autowired
    private ImMessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImConversation getOrCreateConversation(Long userId, Long targetId) {
        if (userId == null || targetId == null) {
            throw new ServiceException("用户ID不能为空");
        }
        if (userId.equals(targetId)) {
            throw new ServiceException("不能和自己发起会话");
        }
        // min/max 保证唯一性（小 ID 在前，大 ID 在后）
        long minUserId = Math.min(userId, targetId);
        long maxUserId = Math.max(userId, targetId);

        ImConversation conversation = conversationMapper.selectByUsers(minUserId, maxUserId);
        if (conversation != null) {
            return conversation;
        }
        // 创建唯一会话
        Date now = new Date();
        ImConversation conv = new ImConversation();
        conv.setType(1);
        conv.setMinUserId(minUserId);
        conv.setMaxUserId(maxUserId);
        conv.setUnreadCountA(0);
        conv.setUnreadCountB(0);
        conv.setStatus("0");
        conv.setDelFlag("0");
        conv.setCreateBy(String.valueOf(userId));
        conv.setCreateTime(now);
        conversationMapper.insert(conv);
        return conv;
    }

    @Override
    public List<ConversationVO> getConversationList(Long userId) {
        return conversationMapper.selectConversationList(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImMessage saveMessage(Long senderId, Long receiverId, Integer msgType, String content) {
        if (senderId == null || receiverId == null) {
            throw new ServiceException("发送者/接收者不能为空");
        }
        if (content == null || content.isEmpty()) {
            throw new ServiceException("消息内容不能为空");
        }
        int type = msgType == null ? ImMsgType.TEXT.getValue() : msgType;

        // 1. 获取或创建唯一会话（一条记录，不分方向）
        ImConversation conversation = getOrCreateConversation(senderId, receiverId);

        // 2. 落库消息（conversation_id 指向唯一会话）
        Date now = new Date();
        ImMessage message = new ImMessage();
        message.setConversationId(conversation.getConversationId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMsgType(type);
        message.setContent(content);
        message.setSendTime(now);
        message.setStatus(1);
        message.setDelFlag("0");
        message.setCreateBy(String.valueOf(senderId));
        message.setCreateTime(now);
        messageMapper.insert(message);

        // 3. 消息摘要（图片显示 [图片]）
        String summary = ImMsgType.of(type) == ImMsgType.IMAGE ? "[图片]" : truncate(content, SUMMARY_MAX_LEN);
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        // 4. 更新会话最后消息摘要
        conversationMapper.updateLastMsg(
                conversation.getConversationId(),
                message.getMsgId(),
                summary,
                timeStr,
                senderId
        );

        // 5. 累加接收方未读数（只更新一条会话记录的对应字段）
        conversationMapper.incrUnreadCount(conversation.getConversationId(), receiverId);

        return message;
    }

    @Override
    public List<ImMessage> getHistoryMessages(Long conversationId, Long lastMsgId, int size) {
        if (conversationId == null) {
            throw new ServiceException("会话ID不能为空");
        }
        int pageSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
        return messageMapper.selectHistoryPage(conversationId, lastMsgId, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new ServiceException("参数不能为空");
        }
        conversationMapper.clearUnreadCount(conversationId, userId);
    }

    @Override
    public int getUnreadTotal(Long userId) {
        if (userId == null) {
            return 0;
        }
        return conversationMapper.selectUnreadTotal(userId);
    }

    @Override
    public List<SimpleUserVO> getChatUserList(Long userId) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        return conversationMapper.selectChatUserList(userId);
    }

    // ---- private ----

    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen);
    }
}
