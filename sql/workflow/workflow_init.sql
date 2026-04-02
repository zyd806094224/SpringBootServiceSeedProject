-- =====================================================
-- 工作流模块建表SQL
-- 基于 Flowable 7.0.1，包含5张业务表
-- Flowable系统表会在应用启动时自动创建
-- =====================================================

-- 1. 流程实例组信息表
CREATE TABLE IF NOT EXISTS `wf_instance_group_info` (
    `id`                    BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `biz_id`                BIGINT NOT NULL DEFAULT 0 COMMENT '业务主键ID',
    `biz_key`               VARCHAR(128) NOT NULL DEFAULT '' COMMENT '业务编码',
    `biz_type`              INT NOT NULL DEFAULT 0 COMMENT '业务类型',
    `process_key`           VARCHAR(64) NOT NULL DEFAULT '' COMMENT '流程定义Key',
    `dept_id`               BIGINT NOT NULL DEFAULT 0 COMMENT '申请人部门ID',
    `user_id`               BIGINT NOT NULL DEFAULT 0 COMMENT '申请用户ID',
    `biz_type_name`         VARCHAR(128) NOT NULL DEFAULT '' COMMENT '业务类型名称',
    `biz_item_id`           INT NOT NULL DEFAULT 0 COMMENT '业务事项ID',
    `right_user_id`         BIGINT NOT NULL DEFAULT 0 COMMENT '数据权益人ID',
    `right_dept_id`         BIGINT NOT NULL DEFAULT 0 COMMENT '数据权益人部门ID',
    `status`                TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-审批中 2-审批通过 3-已驳回 4-撤回',
    `node_id`               VARCHAR(64) NOT NULL DEFAULT '' COMMENT '当前审批节点ID',
    `node_name`             VARCHAR(128) NOT NULL DEFAULT '' COMMENT '当前节点名称',
    `node_type`             INT NOT NULL DEFAULT 0 COMMENT '节点类型: 0-无 1-单人审批 2-顺序会签 3-并行会签 4-或签',
    `status_instance_id`    VARCHAR(64) NOT NULL DEFAULT '' COMMENT '导致状态更新的流程实例ID',
    `create_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_status`         TINYINT NOT NULL DEFAULT 0 COMMENT '删除状态: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_biz_id` (`biz_id`),
    KEY `idx_process_key` (`process_key`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例组信息表';

-- 2. 流程实例信息表
CREATE TABLE IF NOT EXISTS `wf_instance_info` (
    `id`                    BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `record_id`             BIGINT NOT NULL DEFAULT 0 COMMENT '业务申请记录ID',
    `biz_type`              INT NOT NULL DEFAULT 0 COMMENT '业务类型',
    `process_key`           VARCHAR(64) NOT NULL DEFAULT '' COMMENT '流程定义Key',
    `instance_group_id`     BIGINT NOT NULL DEFAULT 0 COMMENT '流程实例组ID',
    `process_instance_id`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'Flowable流程实例ID',
    `version`               INT NOT NULL DEFAULT 0 COMMENT '版本号（同组内终态递增）',
    `status`                TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-审批中 2-审批通过 3-已驳回 4-撤回',
    `create_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_status`         TINYINT NOT NULL DEFAULT 0 COMMENT '删除状态: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_instance_group_id` (`instance_group_id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例信息表';

-- 3. 审批进度信息表
CREATE TABLE IF NOT EXISTS `wf_approval_progress_info` (
    `id`                    BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `instance_group_id`     BIGINT NOT NULL DEFAULT 0 COMMENT '流程实例组ID',
    `process_instance_id`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '流程实例ID',
    `task_id`               VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'Flowable任务ID',
    `node_id`               VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'BPMN节点定义ID',
    `node_name`             VARCHAR(128) NOT NULL DEFAULT '' COMMENT '节点名称',
    `node_type`             INT NOT NULL DEFAULT 0 COMMENT '节点类型',
    `node_type_desc`        VARCHAR(32) NOT NULL DEFAULT '' COMMENT '节点类型描述',
    `opt_type`              INT NOT NULL DEFAULT 0 COMMENT '操作类型: 1-发起申请 2-通过 3-驳回 4-驳回至 5-撤回',
    `opt_type_desc`         VARCHAR(32) NOT NULL DEFAULT '' COMMENT '操作类型描述',
    `status`                INT NOT NULL DEFAULT 0 COMMENT '节点状态',
    `status_desc`           VARCHAR(32) NOT NULL DEFAULT '' COMMENT '状态描述',
    `comment`               VARCHAR(500) NOT NULL DEFAULT '' COMMENT '审批意见',
    `user_id`               BIGINT NOT NULL DEFAULT 0 COMMENT '审批人/发起人ID',
    `user_name`             VARCHAR(64) NOT NULL DEFAULT '' COMMENT '审批人/发起人姓名',
    `dept_id`               BIGINT NOT NULL DEFAULT 0 COMMENT '部门ID',
    `dept_name`             VARCHAR(64) NOT NULL DEFAULT '' COMMENT '部门名称',
    `node_time`             DATETIME DEFAULT NULL COMMENT '节点时间',
    `create_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_instance_group_id` (`instance_group_id`),
    KEY `idx_process_instance_id` (`process_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批进度信息表';

-- 4. 用户审批任务信息表
CREATE TABLE IF NOT EXISTS `wf_user_approval_task_info` (
    `id`                    BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id`               VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'Flowable任务ID',
    `record_id`             BIGINT NOT NULL DEFAULT 0 COMMENT '业务申请记录ID',
    `process_key`           VARCHAR(64) NOT NULL DEFAULT '' COMMENT '流程Key',
    `process_instance_id`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'Flowable流程实例ID',
    `node_id`               VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'BPMN节点定义ID',
    `node_name`             VARCHAR(128) NOT NULL DEFAULT '' COMMENT '节点名称',
    `node_type`             INT NOT NULL DEFAULT 0 COMMENT '节点类型',
    `dept_id`               BIGINT NOT NULL DEFAULT 0 COMMENT '审批人部门ID',
    `user_id`               BIGINT NOT NULL DEFAULT 0 COMMENT '审批人ID',
    `approval_apply_time`   DATETIME DEFAULT NULL COMMENT '审批申请时间',
    `approval_assigned_time` DATETIME DEFAULT NULL COMMENT '审批指派时间',
    `approval_deal_time`    DATETIME DEFAULT NULL COMMENT '审批处理时间',
    `status`                TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-审批中 2-通过 3-已驳回 4-撤回 5-自动通过 6-自动驳回',
    `create_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_status`         TINYINT NOT NULL DEFAULT 0 COMMENT '删除状态: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户审批任务信息表';

-- 5. 审批数据快照表
CREATE TABLE IF NOT EXISTS `wf_record_snapshot` (
    `id`                    BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `record_id`             BIGINT NOT NULL DEFAULT 0 COMMENT '业务申请记录ID',
    `biz_id`                BIGINT NOT NULL DEFAULT 0 COMMENT '业务主键ID',
    `biz_type`              INT NOT NULL DEFAULT 0 COMMENT '业务类型',
    `process_instance_id`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'Flowable流程实例ID',
    `form_schema`           LONGTEXT COMMENT '表单数据快照（JSON）',
    `create_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批数据快照表';
