-- ----------------------------
-- IM 即时通讯模块表结构
-- 说明：单聊场景。会话按"用户视角"建表——同一对用户 A/B 会产生两条 im_conversation
--       记录（user_id=A,target_id=B 与 user_id=B,target_id=A），各自维护自己的未读数，
--       这样会话列表、未读统计都只按 user_id 单字段过滤，查询简单高效。
--       type 字段预留群聊扩展位（当前固定 1=单聊）。
-- ----------------------------

-- ----------------------------
-- 会话表
-- ----------------------------
DROP TABLE IF EXISTS im_conversation;
CREATE TABLE im_conversation (
    conversation_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    type              TINYINT       NOT NULL DEFAULT 1       COMMENT '会话类型（1单聊 2群聊-预留）',
    user_id           BIGINT        NOT NULL                COMMENT '会话归属用户ID（当前视角）',
    target_id         BIGINT        NOT NULL                COMMENT '对方用户ID',
    last_msg_id       BIGINT        DEFAULT NULL            COMMENT '最后一条消息ID',
    last_msg_content  VARCHAR(1000) DEFAULT ''              COMMENT '最后一条消息内容摘要',
    last_msg_time     DATETIME      DEFAULT NULL            COMMENT '最后一条消息时间',
    unread_count      INT(11)       DEFAULT 0               COMMENT '未读消息数',
    status            CHAR(1)       DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    del_flag          CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
    create_by         VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time       DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by         VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time       DATETIME      DEFAULT NULL            COMMENT '更新时间',
    remark            VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    PRIMARY KEY (conversation_id),
    UNIQUE KEY uk_user_target (user_id, target_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='IM会话表';

-- ----------------------------
-- 消息表
-- ----------------------------
DROP TABLE IF EXISTS im_message;
CREATE TABLE im_message (
    msg_id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    conversation_id BIGINT        NOT NULL                COMMENT '会话ID（发送方视角的会话）',
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
    KEY idx_receiver_sender (receiver_id, sender_id),
    KEY idx_sender_id (sender_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='IM消息表';
