package com.zyd.springbootserviceseedproject.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
import com.zyd.springbootserviceseedproject.im.domain.vo.SimpleUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IM 会话 Mapper（v2 主流设计）
 *
 * 会话表一条会话一条记录（min_user_id + max_user_id 唯一），
 * 未读数用 unread_count_a / unread_count_b 分别维护。
 *
 * @author zhaoyudong
 */
@Mapper
public interface ImConversationMapper extends BaseMapper<ImConversation> {

    /**
     * 按 (minUserId, maxUserId) 查询唯一会话
     */
    ImConversation selectByUsers(@Param("minUserId") Long minUserId, @Param("maxUserId") Long maxUserId);

    /**
     * 会话列表（当前用户视角）：
     * 从一条会话记录中根据当前用户是 A 还是 B，取出对方的 userId/昵称/头像和自己的未读数。
     * 按最后消息时间倒序。
     */
    List<ConversationVO> selectConversationList(@Param("userId") Long userId);

    /**
     * 累加未读数（给 receiverId 对应的一方加未读）
     *
     * @param conversationId 会话ID
     * @param receiverId     接收者ID（确定加 unread_count_a 还是 unread_count_b）
     */
    int incrUnreadCount(@Param("conversationId") Long conversationId, @Param("receiverId") Long receiverId);

    /**
     * 清零未读数（给 userId 对应的一方清零）
     *
     * @param conversationId 会话ID
     * @param userId         当前用户ID（确定清 unread_count_a 还是 unread_count_b）
     */
    int clearUnreadCount(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * 更新最后一条消息摘要
     */
    int updateLastMsg(@Param("conversationId") Long conversationId,
                      @Param("lastMsgId") Long lastMsgId,
                      @Param("lastMsgContent") String lastMsgContent,
                      @Param("lastMsgTime") String lastMsgTime,
                      @Param("lastMsgSender") Long lastMsgSender);

    /**
     * 查询用户未读总数（根据用户是 A 还是 B 取对应字段求和）
     */
    int selectUnreadTotal(@Param("userId") Long userId);

    /**
     * 查询 IM 可聊天的用户列表（排除当前用户自己，只返回启用的用户）
     *
     * @param userId 当前用户ID（排除自己）
     */
    List<SimpleUserVO> selectChatUserList(@Param("userId") Long userId);
}
