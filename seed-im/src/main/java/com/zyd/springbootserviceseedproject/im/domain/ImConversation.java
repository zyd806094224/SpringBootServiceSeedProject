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
 * IM 会话实体（用户视角）
 *
 * 同一对用户 A/B 产生两条记录：user_id=A,target_id=B 与 user_id=B,target_id=A，
 * 各自维护自己的未读数与会话展示。
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

    /** 会话ID */
    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;

    /** 会话类型（1单聊 2群聊-预留） */
    @TableField("type")
    private Integer type;

    /** 会话归属用户ID（当前视角） */
    @TableField("user_id")
    private Long userId;

    /** 对方用户ID */
    @TableField("target_id")
    private Long targetId;

    /** 最后一条消息ID */
    @TableField("last_msg_id")
    private Long lastMsgId;

    /** 最后一条消息内容摘要 */
    @TableField("last_msg_content")
    private String lastMsgContent;

    /** 最后一条消息时间 */
    @TableField("last_msg_time")
    private Date lastMsgTime;

    /** 未读消息数 */
    @TableField("unread_count")
    private Integer unreadCount;

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
