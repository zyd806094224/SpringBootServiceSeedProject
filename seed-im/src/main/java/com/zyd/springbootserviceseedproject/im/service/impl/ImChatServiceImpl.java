package com.zyd.springbootserviceseedproject.im.service.impl;

import com.zyd.springbootserviceseedproject.common.exception.ServiceException;
import com.zyd.springbootserviceseedproject.common.utils.SecurityUtils;
import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
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
 * IM 聊天业务实现
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
        ImConversation conversation = conversationMapper.selectByUserTarget(userId, targetId);
        if (conversation != null) {
            return conversation;
        }
        // 创建当前用户视角的会话
        Date now = new Date();
        String username = SecurityUtils.getUsername();
        ImConversation mine = new ImConversation();
        mine.setType(1);
        mine.setUserId(userId);
        mine.setTargetId(targetId);
        mine.setUnreadCount(0);
        mine.setStatus("0");
        mine.setDelFlag("0");
        mine.setCreateBy(username);
        mine.setCreateTime(now);
        conversationMapper.insert(mine);
        return mine;
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

        // 1. 确保发送方视角的会话存在（消息挂在发送方会话下）
        ImConversation senderConv = getOrCreateConversation(senderId, receiverId);

        // 2. 落库消息
        Date now = new Date();
        String username = String.valueOf(senderId);
        ImMessage message = new ImMessage();
        message.setConversationId(senderConv.getConversationId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMsgType(type);
        message.setContent(content);
        message.setSendTime(now);
        message.setStatus(1);
        message.setDelFlag("0");
        message.setCreateBy(username);
        message.setCreateTime(now);
        messageMapper.insert(message);

        // 3. 消息摘要（图片显示 [图片]）
        String summary = ImMsgType.of(type) == ImMsgType.IMAGE ? "[图片]" : truncate(content, SUMMARY_MAX_LEN);
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        // 4. 更新发送方会话摘要（不加未读）
        conversationMapper.updateLastMsg(senderConv.getConversationId(), message.getMsgId(), summary, timeStr);

        // 5. 确保接收方会话存在 + 更新摘要 + 累加未读
        ImConversation receiverConv = conversationMapper.selectByUserTarget(receiverId, senderId);
        if (receiverConv == null) {
            receiverConv = buildReceiverConversation(receiverId, senderId, now);
            conversationMapper.insert(receiverConv);
        }
        conversationMapper.updateLastMsg(receiverConv.getConversationId(), message.getMsgId(), summary, timeStr);
        conversationMapper.incrUnreadCount(receiverConv.getConversationId(), 1);

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
        conversationMapper.clearUnreadCount(conversationId);
    }

    @Override
    public int getUnreadTotal(Long userId) {
        if (userId == null) {
            return 0;
        }
        return conversationMapper.selectUnreadTotal(userId);
    }

    // ---- private ----

    private ImConversation buildReceiverConversation(Long receiverId, Long senderId, Date now) {
        ImConversation conv = new ImConversation();
        conv.setType(1);
        conv.setUserId(receiverId);
        conv.setTargetId(senderId);
        conv.setUnreadCount(0);
        conv.setStatus("0");
        conv.setDelFlag("0");
        conv.setCreateBy(String.valueOf(receiverId));
        conv.setCreateTime(now);
        return conv;
    }

    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen);
    }
}
