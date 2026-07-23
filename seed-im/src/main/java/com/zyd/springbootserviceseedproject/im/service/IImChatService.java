package com.zyd.springbootserviceseedproject.im.service;

import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
import com.zyd.springbootserviceseedproject.im.domain.vo.SimpleUserVO;

import java.util.List;

/**
 * IM 聊天业务接口（v2 主流设计）
 *
 * 会话表一条会话一条记录，消息按 conversation_id 关联。
 *
 * @author zhaoyudong
 */
public interface IImChatService {

    /**
     * 获取或创建会话（唯一会话，min_user_id + max_user_id）
     *
     * @param userId   当前用户ID
     * @param targetId 对方用户ID
     * @return 会话
     */
    ImConversation getOrCreateConversation(Long userId, Long targetId);

    /**
     * 会话列表（当前用户视角，含对方昵称/头像、各自未读数）
     *
     * @param userId 当前用户ID
     */
    List<ConversationVO> getConversationList(Long userId);

    /**
     * 保存消息：落库 + 更新会话摘要 + 累加接收方未读数。
     * 由 WebSocket 接收到消息时调用。
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param msgType    消息类型（1文本 2图片）
     * @param content    消息内容
     * @return 落库后的消息（含 msgId / sendTime / conversationId）
     */
    ImMessage saveMessage(Long senderId, Long receiverId, Integer msgType, String content);

    /**
     * 历史消息分页（按 conversation_id 查，游标向前翻页）。
     *
     * @param conversationId 会话ID
     * @param lastMsgId      游标（上一页最后一条 msgId），为空则查最新一页
     * @param size           每页条数
     */
    List<ImMessage> getHistoryMessages(Long conversationId, Long lastMsgId, int size);

    /**
     * 标记会话已读（清零当前用户的未读数）
     *
     * @param conversationId 会话ID
     * @param userId         当前用户ID
     */
    void markRead(Long conversationId, Long userId);

    /**
     * 当前用户未读消息总数
     *
     * @param userId 当前用户ID
     */
    int getUnreadTotal(Long userId);

    /**
     * IM 用户列表（排除自己）
     *
     * @param userId 当前用户ID
     */
    List<SimpleUserVO> getChatUserList(Long userId);
}
