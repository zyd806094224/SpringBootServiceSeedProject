-- ----------------------------
-- 备忘录分类表
-- ----------------------------
DROP TABLE IF EXISTS memo_category;
CREATE TABLE memo_category (
    category_id     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    user_id         BIGINT        NOT NULL                COMMENT '所属用户ID',
    category_name   VARCHAR(100)  NOT NULL                COMMENT '分类名称',
    category_icon   VARCHAR(100)  DEFAULT ''              COMMENT '分类图标',
    order_num       INT(4)        DEFAULT 0               COMMENT '显示排序',
    status          CHAR(1)       DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    del_flag        CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
    create_by       VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by       VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME      DEFAULT NULL            COMMENT '更新时间',
    remark          VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    PRIMARY KEY (category_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='备忘录分类表';

-- ----------------------------
-- 备忘录字段定义表
-- ----------------------------
DROP TABLE IF EXISTS memo_field_def;
CREATE TABLE memo_field_def (
    field_id        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '字段定义ID',
    category_id     BIGINT        NOT NULL                COMMENT '所属分类ID',
    field_name      VARCHAR(100)  NOT NULL                COMMENT '字段名称（如：身高、体重）',
    field_code      VARCHAR(100)  DEFAULT ''              COMMENT '字段编码',
    field_type      VARCHAR(20)   NOT NULL DEFAULT 'text' COMMENT '字段类型（text/textarea/number/date/select/radio）',
    field_options   VARCHAR(1000) DEFAULT NULL            COMMENT '选项值（select/radio的JSON数组）',
    default_value   VARCHAR(500)  DEFAULT NULL            COMMENT '默认值',
    placeholder     VARCHAR(200)  DEFAULT ''              COMMENT '输入提示',
    is_required     CHAR(1)       DEFAULT '0'             COMMENT '是否必填（0否 1是）',
    sort_order      INT(4)        DEFAULT 0               COMMENT '排序号',
    del_flag        CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
    create_by       VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by       VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME      DEFAULT NULL            COMMENT '更新时间',
    remark          VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    PRIMARY KEY (field_id),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='备忘录字段定义表';

-- ----------------------------
-- 备忘录信息表
-- ----------------------------
DROP TABLE IF EXISTS memo_info;
CREATE TABLE memo_info (
    memo_id         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '备忘录ID',
    user_id         BIGINT        NOT NULL                COMMENT '所属用户ID',
    category_id     BIGINT        NOT NULL                COMMENT '所属分类ID',
    memo_name       VARCHAR(200)  NOT NULL                COMMENT '备忘录名称（如：父亲、我的房子）',
    memo_desc       VARCHAR(500)  DEFAULT ''              COMMENT '简要描述',
    status          CHAR(1)       DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    del_flag        CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
    create_by       VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by       VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME      DEFAULT NULL            COMMENT '更新时间',
    remark          VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    PRIMARY KEY (memo_id),
    KEY idx_user_category (user_id, category_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='备忘录信息表';

-- ----------------------------
-- 备忘录字段值表
-- ----------------------------
DROP TABLE IF EXISTS memo_field_value;
CREATE TABLE memo_field_value (
    value_id        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '值ID',
    memo_id         BIGINT        NOT NULL                COMMENT '备忘录ID',
    field_id        BIGINT        NOT NULL                COMMENT '字段定义ID',
    field_value     TEXT          DEFAULT NULL            COMMENT '字段值',
    create_by       VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME      DEFAULT NULL            COMMENT '创建时间',
    update_by       VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME      DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (value_id),
    UNIQUE KEY uk_memo_field (memo_id, field_id),
    KEY idx_memo_id (memo_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='备忘录字段值表';

-- ----------------------------
-- 测试数据（user_id=1 为admin用户，请根据实际情况修改）
-- ----------------------------

-- 分类
INSERT INTO memo_category (category_id, user_id, category_name, category_icon, order_num, status, create_by, create_time) VALUES
(1, 1, '家人信息', 'peoples', 1, '0', 'admin', sysdate()),
(2, 1, '房产信息', 'house',    2, '0', 'admin', sysdate()),
(3, 1, '车辆信息', 'car',      3, '0', 'admin', sysdate());

-- 字段定义 - 家人信息
INSERT INTO memo_field_def (field_id, category_id, field_name, field_code, field_type, field_options, is_required, sort_order, create_by, create_time) VALUES
(1,  1, '身高',     'height',        'number',  NULL,                            '0', 0,  'admin', sysdate()),
(2,  1, '体重',     'weight',        'number',  NULL,                            '0', 1,  'admin', sysdate()),
(3,  1, '衣服尺码', 'clothing_size', 'select',  '["S","M","L","XL","XXL","XXXL"]', '0', 2,  'admin', sysdate()),
(4,  1, '鞋码',     'shoe_size',     'number',  NULL,                            '0', 3,  'admin', sysdate()),
(5,  1, '血型',     'blood_type',    'select',  '["A型","B型","AB型","O型"]',     '0', 4,  'admin', sysdate()),
(6,  1, '出生日期', 'birthday',      'date',    NULL,                            '0', 5,  'admin', sysdate()),
(7,  1, '身体状态', 'health_status', 'textarea',NULL,                            '0', 6,  'admin', sysdate()),
(8,  1, '疾病信息', 'diseases',      'textarea',NULL,                            '0', 7,  'admin', sysdate()),
(9,  1, '过敏史',   'allergies',     'textarea',NULL,                            '0', 8,  'admin', sysdate()),
(10, 1, '手机号',   'phone',         'text',    NULL,                            '0', 9,  'admin', sysdate());

-- 字段定义 - 房产信息
INSERT INTO memo_field_def (field_id, category_id, field_name, field_code, field_type, field_options, is_required, sort_order, create_by, create_time) VALUES
(11, 2, '小区名称',   'community',      'text',     NULL,                                '1', 0,  'admin', sysdate()),
(12, 2, '详细地址',   'address',        'text',     NULL,                                '1', 1,  'admin', sysdate()),
(13, 2, '面积(㎡)',   'area',           'number',   NULL,                                '0', 2,  'admin', sysdate()),
(14, 2, '户型',       'house_type',     'text',     NULL,                                '0', 3,  'admin', sysdate()),
(15, 2, '购买日期',   'purchase_date',  'date',     NULL,                                '0', 4,  'admin', sysdate()),
(16, 2, '房产证号',   'certificate_no', 'text',     NULL,                                '0', 5,  'admin', sysdate()),
(17, 2, '贷款状态',   'loan_status',    'select',   '["全款","商贷","公积金贷","组合贷"]', '0', 6,  'admin', sysdate()),
(18, 2, '备注',       'remark',         'textarea', NULL,                                '0', 7,  'admin', sysdate());

-- 字段定义 - 车辆信息
INSERT INTO memo_field_def (field_id, category_id, field_name, field_code, field_type, field_options, is_required, sort_order, create_by, create_time) VALUES
(19, 3, '品牌型号',   'brand_model',   'text',     NULL,                          '1', 0,  'admin', sysdate()),
(20, 3, '车牌号',     'plate_no',      'text',     NULL,                          '1', 1,  'admin', sysdate()),
(21, 3, '购买日期',   'purchase_date', 'date',     NULL,                          '0', 2,  'admin', sysdate()),
(22, 3, '年检到期',   'inspection_expire', 'date', NULL,                          '0', 3,  'admin', sysdate()),
(23, 3, '保险到期',   'insurance_expire', 'date', NULL,                          '0', 4,  'admin', sysdate()),
(24, 3, '车架号',     'vin',           'text',     NULL,                          '0', 5,  'admin', sysdate()),
(25, 3, '发动机号',   'engine_no',     'text',     NULL,                          '0', 6,  'admin', sysdate());

-- 备忘录条目 - 家人
INSERT INTO memo_info (memo_id, user_id, category_id, memo_name, memo_desc, status, create_by, create_time) VALUES
(1, 1, 1, '父亲', '父亲基本信息', '0', 'admin', sysdate()),
(2, 1, 1, '母亲', '母亲基本信息', '0', 'admin', sysdate()),
(3, 1, 1, '媳妇', '媳妇基本信息', '0', 'admin', sysdate()),
(4, 1, 1, '儿子', '儿子基本信息', '0', 'admin', sysdate()),
(5, 1, 1, '姥姥', '姥姥基本信息', '0', 'admin', sysdate());

-- 备忘录条目 - 房产
INSERT INTO memo_info (memo_id, user_id, category_id, memo_name, memo_desc, status, create_by, create_time) VALUES
(6, 1, 2, '北京的房子', '朝阳区住宅', '0', 'admin', sysdate()),
(7, 1, 2, '老家的房子', '老家自建房', '0', 'admin', sysdate());

-- 备忘录条目 - 车辆
INSERT INTO memo_info (memo_id, user_id, category_id, memo_name, memo_desc, status, create_by, create_time) VALUES
(8, 1, 3, '家里的车', '家用SUV', '0', 'admin', sysdate());

-- 字段值 - 父亲
INSERT INTO memo_field_value (memo_id, field_id, field_value, create_by, create_time) VALUES
(1, 1,  '172',                  'admin', sysdate()),
(1, 2,  '75',                   'admin', sysdate()),
(1, 3,  'XL',                   'admin', sysdate()),
(1, 4,  '43',                   'admin', sysdate()),
(1, 5,  'A型',                  'admin', sysdate()),
(1, 6,  '1965-03-15',           'admin', sysdate()),
(1, 7,  '身体状况良好，每年定期体检', 'admin', sysdate()),
(1, 8,  '轻度高血压，每日服药控制',   'admin', sysdate()),
(1, 9,  '青霉素过敏',             'admin', sysdate()),
(1, 10, '13800138001',           'admin', sysdate());

-- 字段值 - 母亲
INSERT INTO memo_field_value (memo_id, field_id, field_value, create_by, create_time) VALUES
(2, 1,  '160',                  'admin', sysdate()),
(2, 2,  '58',                   'admin', sysdate()),
(2, 3,  'M',                    'admin', sysdate()),
(2, 5,  'O型',                  'admin', sysdate()),
(2, 6,  '1968-07-22',           'admin', sysdate()),
(2, 7,  '身体健康',             'admin', sysdate()),
(2, 8,  '无',                   'admin', sysdate()),
(2, 10, '13800138002',           'admin', sysdate());

-- 字段值 - 北京的房子
INSERT INTO memo_field_value (memo_id, field_id, field_value, create_by, create_time) VALUES
(6, 11, '阳光花园小区',          'admin', sysdate()),
(6, 12, '北京市朝阳区XX路XX号',  'admin', sysdate()),
(6, 13, '89',                   'admin', sysdate()),
(6, 14, '两室一厅',             'admin', sysdate()),
(6, 15, '2020-06-15',           'admin', sysdate()),
(6, 17, '组合贷',               'admin', sysdate());

-- 字段值 - 家里的车
INSERT INTO memo_field_value (memo_id, field_id, field_value, create_by, create_time) VALUES
(8, 19, '比亚迪宋PLUS DM-i',    'admin', sysdate()),
(8, 20, '京A12345',             'admin', sysdate()),
(8, 21, '2023-03-20',           'admin', sysdate()),
(8, 22, '2029-03-20',           'admin', sysdate()),
(8, 23, '2025-03-20',           'admin', sysdate()),
(8, 24, 'LGXXXXXXXXXXXXXX',     'admin', sysdate());
