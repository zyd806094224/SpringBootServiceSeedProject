package com.zyd.springbootserviceseedproject.im.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 会话列表项 VO（从当前用户视角，带对方用户信息）
 *
 * 同一条会话记录对不同用户展示不同的 targetId/未读数。
 *
 * @author zhaoyudong
 */
@Data
public class ConversationVO {

    /** 会话ID */
    private Long conversationId;

    /** 对方用户ID */
    private Long targetId;

    /** 对方昵称 */
    private String targetName;

    /** 对方头像URL */
    private String targetAvatar;

    /** 最后一条消息内容摘要 */
    private String lastMsgContent;

    /** 最后一条消息时间 */
    private Date lastMsgTime;

    /** 当前用户的未读消息数 */
    private Integer unreadCount;
}
