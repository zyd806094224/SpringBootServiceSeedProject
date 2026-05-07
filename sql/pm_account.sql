-- ----------------------------
-- 密码管理账号表
-- ----------------------------
DROP TABLE IF EXISTS pm_account;
CREATE TABLE pm_account (
    account_id    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '账号ID',
    user_id       BIGINT        NOT NULL                COMMENT '所属用户ID',
    title         VARCHAR(100)  NOT NULL                COMMENT '账号标题(如：微信、GitHub)',
    category      VARCHAR(20)   DEFAULT 'other'         COMMENT '分类(social=社交,work=工作,finance=金融,other=其他)',
    username      VARCHAR(200)  NOT NULL                COMMENT '用户名/邮箱/手机号',
    password      VARCHAR(500)  NOT NULL                COMMENT '密码(AES加密存储)',
    url           VARCHAR(500)  DEFAULT ''              COMMENT '网站URL',
    remark        VARCHAR(500)  DEFAULT ''              COMMENT '备注',
    create_by     VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time   DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by     VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time   DATETIME      DEFAULT NULL            COMMENT '更新时间',
    del_flag      CHAR(1)       DEFAULT '0'             COMMENT '删除标志(0=存在 2=删除)',
    PRIMARY KEY (account_id),
    KEY idx_user_id (user_id),
    KEY idx_category (category)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='密码管理账号表';
