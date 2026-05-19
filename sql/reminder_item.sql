-- ----------------------------
-- 提醒事项表
-- ----------------------------
DROP TABLE IF EXISTS reminder_item;
CREATE TABLE reminder_item (
    reminder_id        BIGINT         NOT NULL AUTO_INCREMENT COMMENT '提醒事项ID',
    user_id            BIGINT         NOT NULL                COMMENT '所属用户ID',
    title              VARCHAR(200)   NOT NULL                COMMENT '事项标题',
    description        VARCHAR(1000)  DEFAULT ''              COMMENT '事项描述',
    category           VARCHAR(50)    DEFAULT 'other'         COMMENT '分类(work/life/finance/health/subscription/license/other)',
    due_date           DATE           NOT NULL                COMMENT '到期日期',
    remind_before_days INT            DEFAULT 7               COMMENT '提前几天开始提醒(默认7天)',
    remind_time        TIME           DEFAULT '09:00:00'      COMMENT '每天提醒时间(默认09:00)',
    overdue_frequency  INT            DEFAULT 1               COMMENT '到期后提醒频率(天,默认每天)',
    status             CHAR(1)        DEFAULT '0'             COMMENT '状态(0=待提醒 1=提醒中 2=已到期 3=已完成 4=已关闭)',
    recipient_email    VARCHAR(200)   DEFAULT ''              COMMENT '收件人邮箱(为空则用系统配置)',
    renewal_count      INT            DEFAULT 0               COMMENT '续期次数',
    last_renewal_date  DATETIME       DEFAULT NULL            COMMENT '最后续期时间',
    original_due_date  DATE           DEFAULT NULL            COMMENT '原始到期日期',
    last_remind_time   DATETIME       DEFAULT NULL            COMMENT '最后提醒发送时间',
    next_remind_time   DATETIME       DEFAULT NULL            COMMENT '下次提醒时间(冗余,用于高效查询)',
    del_flag           CHAR(1)        DEFAULT '0'             COMMENT '删除标志(0=存在 2=删除)',
    create_by          VARCHAR(64)    DEFAULT ''              COMMENT '创建者',
    create_time        DATETIME       DEFAULT NULL            COMMENT '创建时间',
    update_by          VARCHAR(64)    DEFAULT ''              COMMENT '更新者',
    update_time        DATETIME       DEFAULT NULL            COMMENT '更新时间',
    remark             VARCHAR(500)   DEFAULT ''              COMMENT '备注',
    PRIMARY KEY (reminder_id),
    KEY idx_user_id (user_id),
    KEY idx_due_date (due_date),
    KEY idx_next_remind_time (next_remind_time),
    KEY idx_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='提醒事项表';
