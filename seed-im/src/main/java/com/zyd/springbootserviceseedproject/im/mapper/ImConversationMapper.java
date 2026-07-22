package com.zyd.springbootserviceseedproject.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IM 会话 Mapper
 *
 * @author zhaoyudong
 */
@Mapper
public interface ImConversationMapper extends BaseMapper<ImConversation> {

    /**
     * 按 (userId, targetId) 查询单条会话
     */
    ImConversation selectByUserTarget(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /**
     * 会话列表（联查 sys_user 取对方昵称/头像），按最后消息时间倒序
     */
    List<ConversationVO> selectConversationList(@Param("userId") Long userId);

    /**
     * 累加未读数
     */
    int incrUnreadCount(@Param("conversationId") Long conversationId, @Param("count") int count);

    /**
     * 清零未读数
     */
    int clearUnreadCount(@Param("conversationId") Long conversationId);

    /**
     * 更新最后一条消息摘要
     */
    int updateLastMsg(@Param("conversationId") Long conversationId,
                      @Param("lastMsgId") Long lastMsgId,
                      @Param("lastMsgContent") String lastMsgContent,
                      @Param("lastMsgTime") String lastMsgTime);

    /**
     * 查询用户未读总数
     */
    int selectUnreadTotal(@Param("userId") Long userId);
}
