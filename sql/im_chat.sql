-- ----------------------------
-- IM 即时通讯模块表结构（v2 主流重构）
--
-- 设计要点（对标主流 IM）：
--   1. 会话表：一条会话一条记录，全局唯一 conversation_id。
--      单聊用 min_user_id + max_user_id 保证唯一性（小 ID 在前，大 ID 在后），
--      避免 A→B / B→A 产生两条会话。type 字段预留群聊扩展。
--   2. 未读数：会话表上用两个独立字段 unread_count_a / unread_count_b，
--      分别对应 min_user_id / max_user_id 的未读数，无需引入 member 表。
--      （群聊扩展时再引入 im_conversation_member 表。）
--   3. 消息表：conversation_id 指向唯一会话，查询历史只需按 conversation_id 单字段过滤，
--      走索引 idx_conv_time(conversation_id, send_time)，简洁高效。
--   4. last_msg_*：会话级别的最后消息摘要，发消息时更新一次。
-- ----------------------------

-- ----------------------------
-- 会话表（一条会话一条记录）
-- ----------------------------
DROP TABLE IF EXISTS im_conversation;
CREATE TABLE im_conversation (
    conversation_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '会话ID（全局唯一）',
    type              TINYINT       NOT NULL DEFAULT 1       COMMENT '会话类型（1单聊 2群聊-预留）',
    min_user_id       BIGINT        NOT NULL                COMMENT '参与者A（userId 较小者）',
    max_user_id       BIGINT        NOT NULL                COMMENT '参与者B（userId 较大者）',
    unread_count_a    INT(11)       NOT NULL DEFAULT 0      COMMENT '参与者A的未读消息数',
    unread_count_b    INT(11)       NOT NULL DEFAULT 0      COMMENT '参与者B的未读消息数',
    last_msg_id       BIGINT        DEFAULT NULL            COMMENT '最后一条消息ID',
    last_msg_content  VARCHAR(1000) DEFAULT ''              COMMENT '最后一条消息内容摘要',
    last_msg_time     DATETIME      DEFAULT NULL            COMMENT '最后一条消息时间',
    last_msg_sender   BIGINT        DEFAULT NULL            COMMENT '最后一条消息发送者ID',
    status            CHAR(1)       DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    del_flag          CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
    create_by         VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time       DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by         VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time       DATETIME      DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (conversation_id),
    UNIQUE KEY uk_min_max (min_user_id, max_user_id),
    KEY idx_min_user (min_user_id),
    KEY idx_max_user (max_user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='IM会话表（一条会话一条记录）';

-- ----------------------------
-- 消息表（conversation_id 指向唯一会话）
-- ----------------------------
DROP TABLE IF EXISTS im_message;
CREATE TABLE im_message (
    msg_id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '消息ID（全局唯一，可用作游标排序）',
    conversation_id BIGINT        NOT NULL                COMMENT '会话ID（指向唯一会话，不分方向）',
    sender_id       BIGINT        NOT NULL                COMMENT '发送者用户ID',
    receiver_id     BIGINT        NOT NULL                COMMENT '接收者用户ID',
    msg_type        TINYINT       NOT NULL DEFAULT 1       COMMENT '消息类型（1文本 2图片）',
    content         TEXT          NOT NULL                COMMENT '消息内容（文本内容 / 图片URL）',
    send_time       DATETIME      NOT NULL                COMMENT '发送时间',
    status          TINYINT       NOT NULL DEFAULT 1       COMMENT '消息状态（1已发送 2已送达 3已读 4已撤回）',
    del_flag        CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
    create_by       VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by       VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME      DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (msg_id),
    KEY idx_conv_time (conversation_id, send_time),
    KEY idx_sender_id (sender_id),
    KEY idx_receiver_id (receiver_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='IM消息表';
