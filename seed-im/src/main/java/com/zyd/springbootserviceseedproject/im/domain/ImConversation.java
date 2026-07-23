package com.zyd.springbootserviceseedproject.im.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * IM 会话实体（一条会话一条记录，全局唯一）
 *
 * 单聊用 min_user_id + max_user_id 保证唯一性（小 ID 在前，大 ID 在后），
 * 避免 A→B / B→A 产生两条会话。未读数用 unread_count_a / unread_count_b
 * 分别维护两个参与者的未读数。
 *
 * 注意：不继承 BaseEntity（其 params 字段是 Map 查询参数容器，非数据库列，
 * 与 MyBatis-Plus BaseMapper.insert() 自动映射冲突）。审计字段在此直接声明。
 *
 * @author zhaoyudong
 */
@Data
@TableName("im_conversation")
public class ImConversation implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话ID（全局唯一） */
    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;

    /** 会话类型（1单聊 2群聊-预留） */
    @TableField("type")
    private Integer type;

    /** 参与者A（userId 较小者） */
    @TableField("min_user_id")
    private Long minUserId;

    /** 参与者B（userId 较大者） */
    @TableField("max_user_id")
    private Long maxUserId;

    /** 参与者A的未读消息数 */
    @TableField("unread_count_a")
    private Integer unreadCountA;

    /** 参与者B的未读消息数 */
    @TableField("unread_count_b")
    private Integer unreadCountB;

    /** 最后一条消息ID */
    @TableField("last_msg_id")
    private Long lastMsgId;

    /** 最后一条消息内容摘要 */
    @TableField("last_msg_content")
    private String lastMsgContent;

    /** 最后一条消息时间 */
    @TableField("last_msg_time")
    private Date lastMsgTime;

    /** 最后一条消息发送者ID */
    @TableField("last_msg_sender")
    private Long lastMsgSender;

    /** 状态（0正常 1停用） */
    @TableField("status")
    private String status;

    /** 删除标志（0存在 2删除） */
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    /** 创建者 */
    @TableField("create_by")
    private String createBy;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;

    /** 更新者 */
    @TableField("update_by")
    private String updateBy;

    /** 更新时间 */
    @TableField("update_time")
    private Date updateTime;
}
