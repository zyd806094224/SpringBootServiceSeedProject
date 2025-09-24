/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : master

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 24/09/2025 10:02:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `no` varchar(20) DEFAULT NULL COMMENT '账号',
  `name` varchar(100) NOT NULL COMMENT '名字',
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `age` int DEFAULT NULL,
  `sex` int DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `role_id` int DEFAULT NULL COMMENT '角色 0超级管理员，1管理员，2普通账号',
  `is_valid` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'Y' COMMENT '是否有效，Y有效，其他无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`, `is_valid`) VALUES (1, 'admin', '超管', '$2a$10$N7XIb6kj2/MXOeZl61O8fecYnLSud4.oUJd90XwOjtb.hsjyQUfHa', 25, 0, '15812345678', 0, 'Y');
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`, `is_valid`) VALUES (2, 'lisi', '李四', '$2a$10$N7XIb6kj2/MXOeZl61O8fecYnLSud4.oUJd90XwOjtb.hsjyQUfHa', 22, 1, '13300000000', 1, 'Y');
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`, `is_valid`) VALUES (3, 'zhangsan', '张三', '123456', 23, 2, '14588888888', 2, 'Y');
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`, `is_valid`) VALUES (4, 'zzzz', '测试账号', '123456', 13, 1, '13533333333', 1, 'Y');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
