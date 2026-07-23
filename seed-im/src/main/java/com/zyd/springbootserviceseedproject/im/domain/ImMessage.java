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
 * IM 消息实体
 *
 * conversation_id 指向唯一会话（不分方向），查历史只需按 conversation_id 过滤。
 *
 * 注意：不继承 BaseEntity（其 params 字段是 Map 查询参数容器，非数据库列，
 * 与 MyBatis-Plus BaseMapper.insert() 自动映射冲突）。审计字段在此直接声明。
 *
 * @author zhaoyudong
 */
@Data
@TableName("im_message")
public class ImMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息ID（全局唯一，可用作游标排序） */
    @TableId(value = "msg_id", type = IdType.AUTO)
    private Long msgId;

    /** 会话ID（指向唯一会话，不分方向） */
    @TableField("conversation_id")
    private Long conversationId;

    /** 发送者用户ID */
    @TableField("sender_id")
    private Long senderId;

    /** 接收者用户ID */
    @TableField("receiver_id")
    private Long receiverId;

    /** 消息类型（1文本 2图片） */
    @TableField("msg_type")
    private Integer msgType;

    /** 消息内容（文本内容 / 图片URL） */
    @TableField("content")
    private String content;

    /** 发送时间 */
    @TableField("send_time")
    private Date sendTime;

    /** 消息状态（1已发送 2已送达 3已读 4已撤回） */
    @TableField("status")
    private Integer status;

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
