/*
 Navicat Premium Data Transfer

 Source Server         : qyzj_cs
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 127.0.0.1:3306
 Source Schema         : memorial_db

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 20/03/2026 16:15:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for family
-- ----------------------------
DROP TABLE IF EXISTS `family`;
CREATE TABLE `family`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `pid` bigint NOT NULL DEFAULT 0 COMMENT '上级家族ID，0表示顶级',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '家族名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '家族简介',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在地区',
  `founder_id` bigint NULL DEFAULT NULL COMMENT '创建人用户ID',
  `chief_id` bigint NULL DEFAULT NULL COMMENT '族长用户ID（家庭/支族时 designated）',
  `root_id` bigint NULL DEFAULT NULL COMMENT '根家族ID（type=家族时等于id，家庭/支族时指向顶层家族）',
  `founder_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `member_count` int NULL DEFAULT 0 COMMENT '成员数',
  `tomb_count` int NULL DEFAULT 0 COMMENT '墓碑数',
  `invite_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邀请码',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_family_pid`(`pid`) USING BTREE,
  INDEX `idx_family_root_id`(`root_id`) USING BTREE,
  INDEX `idx_family_create_by`(`create_by`) USING BTREE,
  INDEX `idx_family_invite_code`(`invite_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家族表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of family
-- ----------------------------
INSERT INTO `family` VALUES (1, 0, '张氏宗族', '张三丰家族', '', '', 16, NULL, 1, 'zhangsan', 0, 0, 'FCB6EA1D', 1, '2026-03-20 11:40:17', 16, NULL, NULL, 0);
INSERT INTO `family` VALUES (2, 0, '张三丰', '', '', '', 16, NULL, 2, 'zhangsan', 0, 0, '0442E0A6', 1, '2026-03-20 11:41:01', 16, NULL, NULL, 1);
INSERT INTO `family` VALUES (3, 1, '张三丰', '', '', '', 16, NULL, 1, 'zhangsan', 0, 0, '73267DC5', 1, '2026-03-20 11:41:20', 16, NULL, NULL, 0);
INSERT INTO `family` VALUES (4, 3, '张三丰长子', '', '', '', 16, NULL, 1, 'zhangsan', 1, 0, 'E6F7BF55', 1, '2026-03-20 11:41:54', 16, NULL, NULL, 0);
INSERT INTO `family` VALUES (5, 3, '张三丰次子', '', '', '', 16, NULL, 1, 'zhangsan', 2, 0, '354AA69E', 1, '2026-03-20 11:42:15', 16, NULL, NULL, 0);
INSERT INTO `family` VALUES (6, 0, '张氏旁支', '', '', '', 16, NULL, 6, 'zhangsan', 1, 0, 'B8AECFDE', 1, '2026-03-20 11:42:57', 16, NULL, NULL, 0);
INSERT INTO `family` VALUES (7, 0, '李四家', '', '', '', 17, NULL, 7, 'lisi', 4, 0, 'A3C45671', 1, '2026-03-20 14:36:18', 17, NULL, NULL, 0);

-- ----------------------------
-- Table structure for family_admin
-- ----------------------------
DROP TABLE IF EXISTS `family_admin`;
CREATE TABLE `family_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `family_id` bigint NOT NULL COMMENT '家族ID（type=家族的根节点）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：super_admin 超级管理员/admin 管理员',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_family_user`(`family_id`, `user_id`) USING BTREE,
  INDEX `idx_family_id`(`family_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_fa_user_family`(`user_id`, `family_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家族管理员表（超级管理员=创建人，可指定管理员）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of family_admin
-- ----------------------------
INSERT INTO `family_admin` VALUES (1, 1, 16, 'super_admin', '2026-03-20 11:40:17', 16);
INSERT INTO `family_admin` VALUES (2, 2, 16, 'super_admin', '2026-03-20 11:41:01', 16);
INSERT INTO `family_admin` VALUES (3, 6, 16, 'super_admin', '2026-03-20 11:42:57', 16);
INSERT INTO `family_admin` VALUES (4, 1, 19, 'admin', '2026-03-20 14:00:51', 19);
INSERT INTO `family_admin` VALUES (5, 7, 17, 'super_admin', '2026-03-20 14:36:18', 17);

-- ----------------------------
-- Table structure for family_member
-- ----------------------------
DROP TABLE IF EXISTS `family_member`;
CREATE TABLE `family_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `family_id` bigint NOT NULL COMMENT '家族ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成员姓名',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID',
  `bind_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '扫码绑定码',
  `relation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '与族长关系',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '成员' COMMENT '家族角色：族长/管理员/成员',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bind_code`(`bind_code`) USING BTREE,
  INDEX `idx_family_id`(`family_id`) USING BTREE,
  INDEX `idx_fm_user_family`(`user_id`, `family_id`, `del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家族成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of family_member
-- ----------------------------
INSERT INTO `family_member` VALUES (1, 4, '张书平', 18, '6D3E06F9', '父亲', '13200000001', '成员', NULL, '2026-03-20 13:45:44', 16, '2026-03-20 13:57:29', 18, 0);
INSERT INTO `family_member` VALUES (2, 5, '张无忌', 19, 'D542D765', '父亲', '13200000002', '管理员', NULL, '2026-03-20 13:46:57', 16, '2026-03-20 14:00:51', 19, 0);
INSERT INTO `family_member` VALUES (3, 5, '赵敏', 20, 'A0D6CF1B', '母亲', '13200000003', '成员', NULL, '2026-03-20 14:01:55', 16, '2026-03-20 14:03:30', 20, 0);
INSERT INTO `family_member` VALUES (4, 6, '张小凡', 21, NULL, '儿子', NULL, '成员', NULL, '2026-03-20 14:13:31', 16, NULL, NULL, 0);
INSERT INTO `family_member` VALUES (5, 7, '李柳', 22, NULL, '女儿', NULL, '成员', NULL, '2026-03-20 14:46:26', 22, NULL, NULL, 0);
INSERT INTO `family_member` VALUES (6, 7, '李凡', 23, 'E99CD6F7', '儿子', '13300000002', '成员', NULL, '2026-03-20 14:47:54', 17, '2026-03-20 14:49:37', 23, 0);
INSERT INTO `family_member` VALUES (7, 7, '李白', 24, NULL, '兄弟', NULL, '成员', NULL, '2026-03-20 14:53:19', 24, NULL, NULL, 0);
INSERT INTO `family_member` VALUES (8, 7, '李沫', 25, NULL, '姐妹', NULL, '成员', NULL, '2026-03-20 16:13:13', 25, NULL, NULL, 0);

-- ----------------------------
-- Table structure for family_member_apply
-- ----------------------------
DROP TABLE IF EXISTS `family_member_apply`;
CREATE TABLE `family_member_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `family_id` bigint NOT NULL COMMENT '家族ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `relation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '与族长关系',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请理由',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending待审核 approved已通过 rejected已拒绝',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝理由',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_family_id`(`family_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家族成员申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of family_member_apply
-- ----------------------------
INSERT INTO `family_member_apply` VALUES (1, 6, 21, '儿子', '这是一个测试', 'approved', NULL, '2026-03-20 14:12:59', '2026-03-20 14:13:31', 16);

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名称',
  `type` tinyint NULL DEFAULT NULL COMMENT '文件类型 1.图片 2.文档 3.视频 4.音频 5.其它',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前缀',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路径',
  `suffix` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件后缀',
  `source` tinyint NULL DEFAULT NULL COMMENT '来源：1用户 2商家 3管理',
  `size` int NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code`) USING BTREE,
  INDEX `idx_create_by`(`create_by`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES (1, 'kagppUpROoQoK661TYPd3kjNzD7Xrpjz', '03c708ed-b0ef-4a29-8e40-c4a03fa76d06', 1, 'https://ny.px-apricot.cn/api/', '20260320', '.png', NULL, 414559, '2026-03-20 11:20:11', 16, NULL, NULL, 0);
INSERT INTO `file` VALUES (2, '0Tf8kHwi3DR8HI4MY7QHLVerV0JzBEKC', 'D44D6BB06E6A53B8AAF0F6CC7D91E295', 1, 'https://ny.px-apricot.cn/api/', '20260320', '.jpg', NULL, 24836, '2026-03-20 14:08:37', 16, NULL, NULL, 0);
INSERT INTO `file` VALUES (3, 'Ke5MBSc9rHpSq3ER04AdKWYaMWPmFQdb', '6b2e257c4b33d9348ca9f16956f215f7', 1, 'https://ny.px-apricot.cn/api/', '20260320', '.jpg', NULL, 43147, '2026-03-20 14:14:34', 16, NULL, NULL, 0);
INSERT INTO `file` VALUES (4, 'taLrxkmlIIUyrugr91S7GRkRme8aHwgP', 'FCDFB42271E2155E526261BE83E887B2', 1, 'https://ny.px-apricot.cn/api/', '20260320', '.jpg', NULL, 40042, '2026-03-20 14:38:09', 17, NULL, NULL, 0);
INSERT INTO `file` VALUES (5, '2voBY6TUA9RiCyMpdJVCSeCCR38N2eC2', '8E47A8A4C02C15DB73D8C20312D637D1', 1, 'https://ny.px-apricot.cn/api/', '20260320', '.jpg', NULL, 47545, '2026-03-20 14:54:54', 17, NULL, NULL, 0);
INSERT INTO `file` VALUES (6, '6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', '148a493245d38878a9076175f0421092', 1, 'https://ny.px-apricot.cn/api/', '20260320', '.jpg', NULL, 2348496, '2026-03-20 14:55:59', 17, NULL, NULL, 0);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pid` bigint NULL DEFAULT NULL COMMENT '父ID',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单标题',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单类型',
  `permission` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限code',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端组件地址',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端菜单icon',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端路由名',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路径',
  `redirect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重定向路径',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `hidden` tinyint NULL DEFAULT 0 COMMENT '是否被隐藏',
  `affix` tinyint NULL DEFAULT 0 COMMENT '是否被固定在tab',
  `keep_alive` tinyint NULL DEFAULT 0 COMMENT '是否缓存页面',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, 0, '首页概览', 'menu', '', NULL, 'DataAnalysis', NULL, '/dashboard', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (2, 0, '墓碑管理', 'dir', '', NULL, 'Place', NULL, '', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (3, 0, '家族管理', 'dir', '', NULL, 'Connection', NULL, '', NULL, 3, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (4, 0, '用户管理', 'dir', '', NULL, 'User', NULL, '', NULL, 4, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (5, 0, '系统管理', 'dir', '', NULL, 'Setting', NULL, '', NULL, 5, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (6, 2, '墓碑列表', 'menu', 'tomb:list', NULL, '', NULL, '/tomb', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (7, 2, '留言管理', 'menu', 'tomb:message', NULL, '', NULL, '/tomb/message', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (8, 2, '打卡记录', 'menu', 'tomb:checkin', NULL, '', NULL, '/tomb/checkin', NULL, 3, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (9, 3, '家族列表', 'menu', 'family:list', NULL, '', NULL, '/family', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (10, 3, '成员管理', 'menu', 'family:member', NULL, '', NULL, '/family/member', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (11, 4, '用户列表', 'menu', 'sys:user:list', NULL, '', NULL, '/user', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (12, 5, '角色管理', 'menu', 'sys:role:list', NULL, '', NULL, '/system/role', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (13, 5, '菜单管理', 'menu', 'sys:menu:list', NULL, '', NULL, '/system/menu', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (14, 5, '操作日志', 'menu', 'sys:log:list', NULL, '', NULL, '/system/log', NULL, 3, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (15, 11, '新增用户', 'btn', 'sys:user:add', NULL, '', NULL, '', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (16, 11, '编辑用户', 'btn', 'sys:user:edit', NULL, '', NULL, '', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (17, 11, '删除用户', 'btn', 'sys:user:delete', NULL, '', NULL, '', NULL, 3, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (18, 11, '重置密码', 'btn', 'sys:user:resetPwd', NULL, '', NULL, '', NULL, 4, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (19, 11, '禁用用户', 'btn', 'sys:user:disable', NULL, '', NULL, '', NULL, 5, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (20, 11, '恢复用户', 'btn', 'sys:user:restore', NULL, '', NULL, '', NULL, 6, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (21, 11, '分配角色', 'btn', 'sys:user:role', NULL, '', NULL, '', NULL, 7, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (22, 12, '新增角色', 'btn', 'sys:role:add', NULL, '', NULL, '', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (23, 12, '编辑角色', 'btn', 'sys:role:edit', NULL, '', NULL, '', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (24, 12, '删除角色', 'btn', 'sys:role:delete', NULL, '', NULL, '', NULL, 3, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (25, 12, '权限设置', 'btn', 'sys:role:perm', NULL, '', NULL, '', NULL, 4, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (26, 13, '新增菜单', 'btn', 'sys:menu:add', NULL, '', NULL, '', NULL, 1, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (27, 13, '编辑菜单', 'btn', 'sys:menu:edit', NULL, '', NULL, '', NULL, 2, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (28, 13, '删除菜单', 'btn', 'sys:menu:delete', NULL, '', NULL, '', NULL, 3, 0, 0, 0, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (29, 5, '文件管理', 'menu', 'sys:file:list', NULL, '', NULL, '/system/file', NULL, 4, 0, 0, 0, '2026-03-16 14:28:16', NULL, NULL, NULL, 0);
INSERT INTO `menu` VALUES (30, 29, '删除文件', 'btn', 'sys:file:delete', NULL, NULL, NULL, '/system/file/delete/{id}', NULL, 1, 0, 0, 0, '2026-03-16 14:34:47', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` int NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标识：1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', 'admin', '系统最高权限，可管理所有功能', 1, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);
INSERT INTO `role` VALUES (2, '普通用户', 'user', '普通注册用户，只能管理自己的数据', 1, '2026-03-16 14:02:19', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES (47, 2, 1);
INSERT INTO `role_menu` VALUES (48, 2, 2);
INSERT INTO `role_menu` VALUES (49, 2, 6);
INSERT INTO `role_menu` VALUES (50, 2, 7);
INSERT INTO `role_menu` VALUES (51, 2, 8);
INSERT INTO `role_menu` VALUES (52, 2, 3);
INSERT INTO `role_menu` VALUES (53, 2, 9);
INSERT INTO `role_menu` VALUES (54, 2, 10);
INSERT INTO `role_menu` VALUES (84, 1, 1);
INSERT INTO `role_menu` VALUES (85, 1, 2);
INSERT INTO `role_menu` VALUES (86, 1, 6);
INSERT INTO `role_menu` VALUES (87, 1, 7);
INSERT INTO `role_menu` VALUES (88, 1, 8);
INSERT INTO `role_menu` VALUES (89, 1, 3);
INSERT INTO `role_menu` VALUES (90, 1, 9);
INSERT INTO `role_menu` VALUES (91, 1, 10);
INSERT INTO `role_menu` VALUES (92, 1, 4);
INSERT INTO `role_menu` VALUES (93, 1, 11);
INSERT INTO `role_menu` VALUES (94, 1, 15);
INSERT INTO `role_menu` VALUES (95, 1, 16);
INSERT INTO `role_menu` VALUES (96, 1, 17);
INSERT INTO `role_menu` VALUES (97, 1, 18);
INSERT INTO `role_menu` VALUES (98, 1, 19);
INSERT INTO `role_menu` VALUES (99, 1, 20);
INSERT INTO `role_menu` VALUES (100, 1, 21);
INSERT INTO `role_menu` VALUES (101, 1, 5);
INSERT INTO `role_menu` VALUES (102, 1, 12);
INSERT INTO `role_menu` VALUES (103, 1, 22);
INSERT INTO `role_menu` VALUES (104, 1, 23);
INSERT INTO `role_menu` VALUES (105, 1, 24);
INSERT INTO `role_menu` VALUES (106, 1, 25);
INSERT INTO `role_menu` VALUES (107, 1, 13);
INSERT INTO `role_menu` VALUES (108, 1, 26);
INSERT INTO `role_menu` VALUES (109, 1, 27);
INSERT INTO `role_menu` VALUES (110, 1, 28);
INSERT INTO `role_menu` VALUES (111, 1, 14);
INSERT INTO `role_menu` VALUES (112, 1, 29);
INSERT INTO `role_menu` VALUES (113, 1, 30);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名称',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日志名称',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP',
  `area` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域',
  `operator` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运营商',
  `path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '全路径',
  `module` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块名称',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类名',
  `method_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方法名称',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方式',
  `content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容类型',
  `request_body` tinyint NULL DEFAULT NULL COMMENT '是否是JSON请求映射参数',
  `param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'tokenMd5值',
  `type` tinyint NULL DEFAULT NULL COMMENT '类型',
  `success` tinyint NULL DEFAULT NULL COMMENT '0:失败,1:成功',
  `code` int NULL DEFAULT NULL COMMENT '响应结果状态码',
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '响应结果消息',
  `exception_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异常类名称',
  `exception_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '异常信息',
  `browser_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器名称',
  `browser_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器版本',
  `engine_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器引擎名称',
  `engine_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器引擎版本',
  `os_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '系统名称',
  `platform_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '平台名称',
  `mobile` tinyint NULL DEFAULT NULL COMMENT '是否是手机',
  `device_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '移动端设备名称',
  `device_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '移动端设备型号',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 179 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO `sys_operation_log` VALUES (1, '0b25bfba41de494ca3c0d5fad9e0808f', '1', 'admin', '新增用户（管理端）', '192.168.31.73', NULL, NULL, '/user/add', 'memorial', 'com.memorial.system.controller.UserController', 'addUser', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"mobile\":\"13200000000\",\"nickname\":\"张三\",\"sex\":1,\"password\":\"ftsfU4AZaD/Bw+4irIATNrGdCe7esw6qJ2JQ/1XmrUOhg0/5+wrluIb1RH8WnA10+hoTd18c4O+W84vgns0IUe+KccEcVgQ3KJYJ/R4pclDQYuklpa111cMZVOssWlardn08n6Hd5Y+bggpjPA1LS0BNambK8Gu2X5BRlLqycQw=\"}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:40:24', NULL);
INSERT INTO `sys_operation_log` VALUES (2, '159b3e0034bc45aabb898ea011665df0', '1', 'admin', '新增用户（管理端）', '192.168.31.73', NULL, NULL, '/user/add', 'memorial', 'com.memorial.system.controller.UserController', 'addUser', 'POST', 'application/json', 1, '{\"username\":\"lisi\",\"mobile\":\"13300000000\",\"nickname\":\"李四\",\"sex\":1,\"password\":\"F4OGsSiFIoWb0i+xP8i/+EByFG1smA8pnfPJgwej1CFhCY9bP85pLVMON0CQrqwThklexa2H5JhMaksEzAD3uyD6YKiLeHj+F8xZqdZOldboeyko59pVctHsqfhoYWE2v+M4ekx/imoTg4BELYPFfl5MtqrsT2UI/dmsZ8xp0BU=\"}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:40:51', NULL);
INSERT INTO `sys_operation_log` VALUES (3, 'bfdacbfab0074ad3a4b39302c781659a', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000000\",\"password\":\"YEPlhbAH7Vj6jvsOLSsbFj0TWJMdrqCIVeczyWdkuuuet9VfnWHDgR7jvrfaap7ugpyYjRwLKGTX0sK/aQA3BhIzrbhT7gUagDAYFnnrpoXENypOjZHE+U0VCZDYaBxtL7a6jlD5mOQ2MNFXECoMAwFOkH3huFS8eY2NqBm0PcA=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:44:04', NULL);
INSERT INTO `sys_operation_log` VALUES (4, '091a1474b34f47989925aef1579821b5', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"password\":\"HGQjv8loh+HYbePylqQ6j94PWBCInAgMdhljDND9HmkTbAJXn8lLI6t5oPnJWc1M6U42KTtqo1KzHmVuGaXi5lRDt6QZ8A/WHSka67e0szUwYV1G/2ARCzWzC16iMyL49PFYo1GpydsSUcF+0wrJKhC1Bw4AGHNHmEQiHTEMJ6U=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:44:16', NULL);
INSERT INTO `sys_operation_log` VALUES (5, '07f29a35f08e498ead2cca666d7a21e2', '1', 'admin', '新增用户（管理端）', '192.168.31.73', NULL, NULL, '/user/add', 'memorial', 'com.memorial.system.controller.UserController', 'addUser', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"mobile\":\"13200000000\",\"nickname\":\"张三\",\"sex\":1,\"password\":\"LW21jTD89tsfTiPMPixcL4+8wDfHT9IOV61fWdDtkHbgXjIp7qsMKsu+icaQwojjgRiFwQ77X2tTm0XZSEt2+bU9DnR1Uvcige9amGOS1SRZ96yKjLymZeZDM7QFGRBj7yN4LIGznx23u5aTwvIC82nXCO1Xq1hNLaS796/qpIM=\"}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:50:27', NULL);
INSERT INTO `sys_operation_log` VALUES (6, 'ab3d5d5403d743e1810634969b83a6e3', '1', 'admin', '新增用户（管理端）', '192.168.31.73', NULL, NULL, '/user/add', 'memorial', 'com.memorial.system.controller.UserController', 'addUser', 'POST', 'application/json', 1, '{\"username\":\"lisi\",\"mobile\":\"13300000000\",\"nickname\":\"李四\",\"sex\":1,\"password\":\"UY6e/0oewAd3RBaPYgONOEmxinuH1EZ+uf4OPgR2Xw60yTVNwuQEKspeR3oHWeUSlaWnCXjT3z/NYJmn9OUFt+HsUDrCsF3KRm1lhbmAo3Ora6vhEqvranavX68q9WsDj0RcDpc6fHtlQpPoFplve8SN8zrbHHxvLBMQzCH11KY=\"}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:51:09', NULL);
INSERT INTO `sys_operation_log` VALUES (7, '82317c58b66c4ccd896526d1856d0fb0', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"password\":\"b9Eo2Wsp5PrroPyMvK3ysU9Pbo7dISzE6dFDuTQKdnUnSfOAc9HeWUxf1ud7NBsptDRbKgX52+CFkkUJWKCpsWGQ9N1ZTc/vQv+qp1IO38D4ilSmI3GOVT7TnoL610BGlh4oOfrJdsjOux5VBGvSRTCPklfki21D+LaLg1cHP1c=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:51:29', NULL);
INSERT INTO `sys_operation_log` VALUES (8, 'cf8253e078c644abb8589effbc385e07', '1', 'admin', '新增用户（管理端）', '192.168.31.73', NULL, NULL, '/user/add', 'memorial', 'com.memorial.system.controller.UserController', 'addUser', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"mobile\":\"13200000000\",\"nickname\":\"张三\",\"sex\":1,\"password\":\"X766wwX8pP971KPian/ryETMseH6vOmKWjd6k1LyURxbwViHU5/LwXclEWQOQRfwUZq7FzoYGLnYAuraUDhj7rv4FRgMi+2xnxZED904eLSiCcEhZK69Br+yf9O8JsrphdgJXfITMgGmLLJZ1qRr1hcMF0mqrcVxlTidM0LhC7U=\",\"roleIds\":[2]}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:54:19', NULL);
INSERT INTO `sys_operation_log` VALUES (9, '8ff79ab06da744f3abcca2e107fb2863', '1', 'admin', '新增用户（管理端）', '192.168.31.73', NULL, NULL, '/user/add', 'memorial', 'com.memorial.system.controller.UserController', 'addUser', 'POST', 'application/json', 1, '{\"username\":\"lisi\",\"mobile\":\"13300000000\",\"nickname\":\"李四\",\"sex\":1,\"password\":\"ajMOwJneb2OfcoGkSG1ygs/qxVzvNAVdhMPs081m4K/97ncdnRpE+SIq4Ov4c/BKfuxrmp+J6RvqaIpuJkSP8vCX465EMTs5hr8NhzzA7MDCgP+5qy5twmps0fYv2uHREVM3aUh7+lRYOLeKaLbQ96XJeJ/nbS5bVRu5GRLD3w0=\",\"roleIds\":[2]}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:55:05', NULL);
INSERT INTO `sys_operation_log` VALUES (10, '90da120a891347489bf380b52e6eb54f', '1', 'admin', '修改用户（管理端）', '192.168.31.73', NULL, NULL, '/user/updateUser', 'memorial', 'com.memorial.system.controller.UserController', 'updateUser', 'POST', 'application/json', 1, '{\"nickname\":\"李四\",\"sex\":1,\"id\":17,\"username\":\"lisi\",\"mobile\":\"13300000000\"}', '0fdca9bd07da4a5a8a38d7c47651246f', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 10:55:44', NULL);
INSERT INTO `sys_operation_log` VALUES (11, 'd3301e1b22eb44dd80b6ff3215d1b12c', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"password\":\"dgUpKh/U/u/xXrf5gxPGr4zyL3c0HAkxK7Mymy7pFPmqLmTcyfVQ4e8Y6x9S+QQz93DgOJssfIn4CwBzq4BCKA347eqgFjE+S0uHVvgWzFBjT4CR/VGRcMQJYWAvzcJ68oVWwXc1vM99xt/3HArtxJwps0Ed+ZZGA3Dbej5jKw8=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:10:42', NULL);
INSERT INTO `sys_operation_log` VALUES (12, '3b62af674dc3465582af7d729c64e20e', NULL, NULL, '退出登录（当前用户）', '192.168.31.73', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, '0fdca9bd07da4a5a8a38d7c47651246f', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:13:55', NULL);
INSERT INTO `sys_operation_log` VALUES (13, '1b7de63a01464d2ea78f21ddd604c93d', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"admin\",\"password\":\"epkY8f8xISZEOtPCLH/yoPSYcdNMBl85hJnKJxBmptWyiXjQ3zoq5Uu1yTPd7FKCrN2fBe/um5hSzSx2PebgbxWIRcQdr0Qf5cVRtQoWrtRSfHdAObyy6VoHIUMknNMDy+jeUhYfF/XkSbjN+xw2G5fRrMJ57vV+o//Hr6WruBQ=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:14:05', NULL);
INSERT INTO `sys_operation_log` VALUES (14, '1be637ca83cf407ca2864e420e900395', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"admin\",\"password\":\"C+E6f9r/EQ7tdjQsnAONg8bwXy5iPP6yY7/Tjopv/YB12A9Nj6KMwWOIhxmRQjaQd24WIFgVxSMRruZcJb6q1HHMQhmBwXLszS8XbAAu7k0fkPH8mJQ/vuDVqqbxoDZQ2kg9juNA/JnvE+mZuCkql18T+pvjZZEJ5qoYdvm602k=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:14:11', NULL);
INSERT INTO `sys_operation_log` VALUES (15, '883b8568fc3342a19b148501a2aae35f', '16', 'zhangsan', '新增墓碑', '192.168.31.73', NULL, NULL, '/tomb/add', 'memorial', 'com.memorial.system.controller.TombController', 'add', 'POST', 'application/json', 1, '{\"name\":\"张三丰\",\"photo\":\"http://192.168.31.73:2168/file/kagppUpROoQoK661TYPd3kjNzD7Xrpjz\",\"birthday\":\"2020-01-01\",\"deathday\":\"2026-03-01\",\"biography\":\"<p>\\t张三丰（生卒年不详）<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[17]&nbsp;[19-21]</span>，是辽东<a href=\\\"https://baike.baidu.com/item/%E6%87%BF%E5%B7%9E/10344882?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">懿州</a>人<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[30]</span>。名全一，一名君宝，三丰其号也。以其不修边幅，又号张邋遢<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[30]</span>。自称<a href=\\\"https://baike.baidu.com/item/%E5%BC%A0%E9%81%93%E9%99%B5/1707760?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">张道陵</a>后裔，又名彭俊、全一、思廉、玄素、玄化、三仹、三峰、君实<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[29]</span>，字铉一、蹋仙、居宝、昆阳、剌闼、元元、玄玄、符元，号三侔、三丰子、玄玄子<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[2-8]&nbsp;[11]&nbsp;[13-15]&nbsp;[18]</span>。</p><p>\\t元世祖<a href=\\\"https://baike.baidu.com/item/%E4%B8%AD%E7%BB%9F/19140493?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">中统</a>元年，官至中山博陵令。大耳圆目、须髯如戟，居宝鸡<a href=\\\"https://baike.baidu.com/item/%E9%87%91%E5%8F%B0%E8%A7%82/7970580?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">金台观</a>时曾“阳神出游”、时隐时现。皇帝封其“犹龙六祖隐仙寓化虚微普度天尊”、“通微显化真人”、“韬光尚志真仙”、“清虚元妙真君”、“飞龙显化宏仁济世真君”等<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[15]&nbsp;[19]</span>。</p><p>\\t张三丰影响后世最深的是在前代理论及技艺的基础上，开创了中国武术中的内家功夫——武当派。张三丰深受阴阳平衡、动静相生理念的影响，于是根据道家“道法自然”“处柔守雌”理论，创造了以养生为首、防身为要，以柔克刚、以静制动、借力打力、后发制人的独特功理与拳法，为中华武术的发展作出了突出的贡献。</p>\",\"story\":\"\",\"epitaph\":\"明可明，非常明。道可道，非常道\",\"visitorActionOpen\":true,\"address\":\"武当山\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:25:24', NULL);
INSERT INTO `sys_operation_log` VALUES (16, 'd00d95ca7141490f8c830fb3ffc2d62d', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":0,\"type\":\"家族\",\"name\":\"张氏宗族\",\"description\":\"张三丰直系家族\",\"phone\":\"\",\"address\":\"湖北武当山\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:26:06', NULL);
INSERT INTO `sys_operation_log` VALUES (17, '4226b4232ffc4c8f943643edb0fc2fd1', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":1,\"type\":\"家庭\",\"name\":\"张三丰\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:27:35', NULL);
INSERT INTO `sys_operation_log` VALUES (18, '4d5e24aefcd14d19a3a058be212db8f2', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":2,\"type\":\"支族\",\"name\":\"张三丰宗族\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:28:53', NULL);
INSERT INTO `sys_operation_log` VALUES (19, 'cbfefa84b39740258da451e8fd8cb30f', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":2,\"type\":\"支族\",\"name\":\"张三丰支族\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:29:15', NULL);
INSERT INTO `sys_operation_log` VALUES (20, 'f4e7e98686284f4293c675965e4c20f5', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":0,\"name\":\"张氏宗族\",\"description\":\"张三丰家族\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:40:17', NULL);
INSERT INTO `sys_operation_log` VALUES (21, '399ad3cb682b425d9bb22659986cb274', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":0,\"name\":\"张三丰\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:41:01', NULL);
INSERT INTO `sys_operation_log` VALUES (22, '1d04e10be1d24b008c01a370c4839426', '16', 'zhangsan', '删除家族', '192.168.31.73', NULL, NULL, '/family/delete/2', 'memorial', 'com.memorial.system.controller.FamilyController', 'delete', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 3, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:41:06', NULL);
INSERT INTO `sys_operation_log` VALUES (23, 'fff6caf5f0e045f6bec00a6250bf2bac', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":1,\"name\":\"张三丰\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:41:20', NULL);
INSERT INTO `sys_operation_log` VALUES (24, '28b52ffc2c934cb4821131f4dce4dda6', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":3,\"name\":\"张三丰长子\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:41:54', NULL);
INSERT INTO `sys_operation_log` VALUES (25, '5a70c905edef4e41bec9f577d0f28f24', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":3,\"name\":\"张三丰次子\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:42:15', NULL);
INSERT INTO `sys_operation_log` VALUES (26, 'b40fc9a9977d4df08a77ad60af9fdb91', '16', 'zhangsan', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":0,\"name\":\"张氏旁支\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:42:57', NULL);
INSERT INTO `sys_operation_log` VALUES (27, '10747d28666541eb99b709a01622bfcd', '16', 'zhangsan', '修改墓碑', '192.168.31.73', NULL, NULL, '/tomb/update', 'memorial', 'com.memorial.system.controller.TombController', 'update', 'POST', 'application/json', 1, '{\"id\":1,\"name\":\"张三丰\",\"photo\":\"http://192.168.31.73:2168/file/kagppUpROoQoK661TYPd3kjNzD7Xrpjz\",\"birthday\":\"2020-01-01\",\"deathday\":\"2026-03-01\",\"biography\":\"<p>\\t张三丰（生卒年不详）<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[17]&nbsp;[19-21]</span>，是辽东<a href=\\\"https://baike.baidu.com/item/%E6%87%BF%E5%B7%9E/10344882?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">懿州</a>人<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[30]</span>。名全一，一名君宝，三丰其号也。以其不修边幅，又号张邋遢<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[30]</span>。自称<a href=\\\"https://baike.baidu.com/item/%E5%BC%A0%E9%81%93%E9%99%B5/1707760?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">张道陵</a>后裔，又名彭俊、全一、思廉、玄素、玄化、三仹、三峰、君实<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[29]</span>，字铉一、蹋仙、居宝、昆阳、剌闼、元元、玄玄、符元，号三侔、三丰子、玄玄子<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[2-8]&nbsp;[11]&nbsp;[13-15]&nbsp;[18]</span>。</p><p>\\t元世祖<a href=\\\"https://baike.baidu.com/item/%E4%B8%AD%E7%BB%9F/19140493?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">中统</a>元年，官至中山博陵令。大耳圆目、须髯如戟，居宝鸡<a href=\\\"https://baike.baidu.com/item/%E9%87%91%E5%8F%B0%E8%A7%82/7970580?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">金台观</a>时曾“阳神出游”、时隐时现。皇帝封其“犹龙六祖隐仙寓化虚微普度天尊”、“通微显化真人”、“韬光尚志真仙”、“清虚元妙真君”、“飞龙显化宏仁济世真君”等<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[15]&nbsp;[19]</span>。</p><p>\\t张三丰影响后世最深的是在前代理论及技艺的基础上，开创了中国武术中的内家功夫——武当派。张三丰深受阴阳平衡、动静相生理念的影响，于是根据道家“道法自然”“处柔守雌”理论，创造了以养生为首、防身为要，以柔克刚、以静制动、借力打力、后发制人的独特功理与拳法，为中华武术的发展作出了突出的贡献。</p>\",\"story\":\"\",\"epitaph\":\"明可明，非常明。道可道，非常道\",\"visitorActionOpen\":true,\"familyId\":3,\"address\":\"武当山\"}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:43:42', NULL);
INSERT INTO `sys_operation_log` VALUES (28, 'c9a663cd0f8842be8b174fd4d24bc51b', '16', 'zhangsan', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":1,\"title\":\"早年经历\",\"content\":\"<p>\\t<a href=\\\"https://baike.baidu.com/item/%E5%AE%8B%E6%9C%AB/35286?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">宋末</a>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">元初，张三丰出生。生有异质，龟形鹤骨，大耳圆目。身长七尺余，修髯如戟，顶作一髻，常戴偃月冠。一笠一衲，寒暑御之。不饰</span>\\t<a href=\\\"https://baike.baidu.com/item/%E8%BE%B9%E5%B9%85/10947312?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">边幅</a>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">，人皆目为张邋遢</span>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">。</span></p><p>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">张三丰五岁时，目染异疾，积久渐昏。当时有道人名张云庵，是方外异人，住持碧落宫，自号白云禅老。见而奇之曰：此子仙风道骨，自非凡器，但目遭</span>\\t<a href=\\\"https://baike.baidu.com/item/%E9%AD%94%E9%9A%9C/7519630?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">魔障</a>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">，须拜贫道为弟子，了脱尘翳，慧珠再朗，即送还。林氏许之，遂投云庵为徒。静居半载而目渐明，教习道经过目便晓，有暇兼读儒、释两家之书，随手披阅，会通其大意即止</span>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">。</span></p><p>\\t张三丰十三岁时，其母林氏想念他，张云庵也不留，于是张三丰拜辞归家，专究儒业。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[26]</span></p><p>\\t<a href=\\\"https://baike.baidu.com/item/%E5%85%83/2634049?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">元</a>世祖<a href=\\\"https://baike.baidu.com/item/%E4%B8%AD%E7%BB%9F/19140493?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">中统</a>元年（1260年），张三丰举茂才异等。</p><p>\\t元中统二年（1261年），张三丰文学才识，列名上闻，以备擢用，然而做官不是其向来的志向。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[26]</span></p><p>\\t元<a href=\\\"https://baike.baidu.com/item/%E8%87%B3%E5%85%83/386003?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">至元</a>元年（1264年）秋，游燕京。当时元朝刚刚在燕京定都，诏令旧列文学才识者待用。张三丰声望渐起，始与<a href=\\\"https://baike.baidu.com/item/%E5%B9%B3%E7%AB%A0%E6%94%BF%E4%BA%8B/9533026?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">平章政事</a><a href=\\\"https://baike.baidu.com/item/%E5%BB%89%E5%B8%8C%E5%AE%AA/1408671?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">廉希宪</a>相交。廉希宪惊异他的才能，奏补为中山博陵令。</p>\",\"sort\":0}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:46:52', NULL);
INSERT INTO `sys_operation_log` VALUES (29, '7eb56c347b4c447fa0bdd841565a2c6b', '16', 'zhangsan', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":1,\"title\":\"弃官归隐\",\"content\":\"<p>\\t元至元二年（1265年），张三丰父母病逝，张三丰弃官<a href=\\\"https://baike.baidu.com/item/%E4%B8%81%E5%BF%A7/998469?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">丁忧</a>，不久断绝仕进的想法，奉讳归辽阳，终日哀毁。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[26]</span>闲居数年，张三丰束装出游，将田产托付给族人，嘱代扫墓，带领二行童相随。游历各地名山古刹，吟咏闲观，且行且住。几乎三十年，均无所遇。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[26-27]</span></p><p>\\t元延祐年间，张三丰六十七岁，入终南山修炼，拜<a href=\\\"https://baike.baidu.com/item/%E7%81%AB%E9%BE%99%E7%9C%9F%E4%BA%BA/19911111?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">火龙真人</a>为师，得金丹之旨。山居四载，功效寂然。闻近斯道者，必须<a href=\\\"https://baike.baidu.com/item/%E6%B3%95%E8%B4%A2/1970359?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">法财</a>两用，平生游访，兼颇好善，囊箧殆空，不觉泪下，火龙怪之，进告以故，乃传<a href=\\\"https://baike.baidu.com/item/%E4%B8%B9%E7%A0%82/3405377?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">丹砂</a>点化之诀，命出山修炼。立辞恩师，和光混俗者数年。</p>\",\"sort\":0}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:47:08', NULL);
INSERT INTO `sys_operation_log` VALUES (30, 'bdaee503a7df497eb5584ef1061edcee', '16', 'zhangsan', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":1,\"title\":\"得道成真\",\"content\":\"<p>\\t元泰定元年（1324年）春，南至<a href=\\\"https://baike.baidu.com/item/%E6%AD%A6%E5%BD%93%E5%B1%B1/84549?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">武当山</a>，调神九载而道成。隐显遨游又十余年。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[27]</span></p><p>\\t元至正初年，由楚还辽阳，省墓讫，复之燕市，公卿故交，死亡已尽矣。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[27]</span></p><p>\\t元至正十九年（1359年），秦淮渔户沈万山，又名万三，好善乐施，张三丰传道于沈万三。沈万三自号三山道士。临别，先生预知万三有徙边之祸，嘱曰：东南王气正盛，当晤子于西南也。不久返回陕西，居宝鸡金台观。</p>\",\"sort\":0}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:47:23', NULL);
INSERT INTO `sys_operation_log` VALUES (31, 'c31b53b76e574c2a8312b63f93a57799', '16', 'zhangsan', '修改墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/update', 'memorial', 'com.memorial.system.controller.TombStoryController', 'update', 'POST', 'application/json', 1, '{\"id\":3,\"tombId\":1,\"title\":\"得道成真\",\"content\":\"<p>\\t元泰定元年（1324年）春，南至<a href=\\\"https://baike.baidu.com/item/%E6%AD%A6%E5%BD%93%E5%B1%B1/84549?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">武当山</a>，调神九载而道成。隐显遨游又十余年。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[27]</span></p><p>\\t元至正初年，由楚还辽阳，省墓讫，复之燕市，公卿故交，死亡已尽矣。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[27]</span></p><p>\\t元至正十九年（1359年），秦淮渔户沈万山，又名万三，好善乐施，张三丰传道于沈万三。沈万三自号三山道士。临别，先生预知万三有徙边之祸，嘱曰：东南王气正盛，当晤子于西南也。不久返回陕西，居宝鸡金台观。</p><p>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">元至正二十六年九月二十日（1366年10月30日</span>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">）</span>\\t<span style=\\\"color: rgb(51, 51, 51);\\\">，自言辞世，留颂而逝。士民杨轨山置棺殓讫，临窆复生。</span></p>\",\"sort\":0}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:47:35', NULL);
INSERT INTO `sys_operation_log` VALUES (32, 'df6f26236ad44617a7163cd9dd16c888', '16', 'zhangsan', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":1,\"title\":\"踪迹莫测\",\"content\":\"<p>\\t明洪武十七年（1384年），明太祖<a href=\\\"https://baike.baidu.com/item/%E6%9C%B1%E5%85%83%E7%92%8B/25626?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">朱元璋</a>以华夷宾服，诏求张三丰，不赴。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[27]</span></p><p>\\t明洪武十八年（1385年），<a href=\\\"https://baike.baidu.com/item/%E6%98%8E%E5%A4%AA%E7%A5%96/2329247?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">明太祖</a>强迫<a href=\\\"https://baike.baidu.com/item/%E6%B2%88%E4%B8%87%E4%B8%89/868735?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">沈万三</a>敦请张三丰，不赴。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[27]</span></p><p>\\t明洪武二十四年（1391年），明太祖派遣三山道士沈万三寻访，不得。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[22]&nbsp;[24]</span></p><p>\\t明洪武二十五年（1392年），张三丰遁入云南，恰逢明太祖徙<a href=\\\"https://baike.baidu.com/item/%E6%B2%88%E4%B8%87%E4%B8%89/868735?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">沈万三</a>于海上，缘此践约来会，同炼天元服食大药。</p><p>\\t明洪武二十六年（1393年），药成，张三丰至贵州<a href=\\\"https://baike.baidu.com/item/%E5%B9%B3%E8%B6%8A/6536003?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">平越</a><a href=\\\"https://baike.baidu.com/item/%E7%A6%8F%E6%B3%89%E5%B1%B1/7379833?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">福泉山</a>，朝真礼斗，候诏飞升。</p><p>\\t明永乐五年（1407年），<a href=\\\"https://baike.baidu.com/item/%E6%98%8E%E6%88%90%E7%A5%96/449258?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">明成祖</a>派遣<a href=\\\"https://baike.baidu.com/item/%E7%BB%99%E4%BA%8B%E4%B8%AD/1081312?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">给事中</a>胡濴、指挥杨永吉等寻访张三丰，不得。<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[25]&nbsp;[28]</span></p><p>\\t明永乐十年（1413年），命<a href=\\\"https://baike.baidu.com/item/%E5%AD%99%E7%A2%A7%E4%BA%91/4769455?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">孙碧云</a>于<a href=\\\"https://baike.baidu.com/item/%E6%AD%A6%E5%BD%93/16139?fromModule=lemma_inlink\\\" rel=\\\"noopener noreferrer\\\" target=\\\"_blank\\\" style=\\\"color: rgb(19, 110, 194);\\\">武当</a>建宫拜候，并致书相请，不得。</p>\",\"sort\":0}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 11:47:51', NULL);
INSERT INTO `sys_operation_log` VALUES (33, '3c43913917e9416ebeddbdf676ec7eeb', '16', 'zhangsan', '添加家族成员', '192.168.31.73', NULL, NULL, '/family/member/add', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'add', 'POST', 'application/json', 1, '{\"familyId\":1,\"name\":\"张三\",\"relation\":\"父亲\",\"phone\":\"\",\"role\":\"成员\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:45:44', NULL);
INSERT INTO `sys_operation_log` VALUES (34, '960c4de6622143c9ad966143fe66e1de', '16', 'zhangsan', '修改家族成员', '192.168.31.73', NULL, NULL, '/family/member/update', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'update', 'POST', 'application/json', 1, '{\"id\":1,\"familyId\":4,\"name\":\"张三\",\"relation\":\"父亲\",\"phone\":\"\",\"role\":\"成员\"}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:46:12', NULL);
INSERT INTO `sys_operation_log` VALUES (35, '50a510e00ba246fdbc61a60c74ddec07', '16', 'zhangsan', '添加家族成员', '192.168.31.73', NULL, NULL, '/family/member/add', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'add', 'POST', 'application/json', 1, '{\"familyId\":5,\"name\":\"张无忌\",\"relation\":\"父亲\",\"phone\":\"\",\"role\":\"成员\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:46:57', NULL);
INSERT INTO `sys_operation_log` VALUES (36, '6c5a20f8c10d4c799de78ff5e98c0ac8', '16', 'zhangsan', '修改家族成员', '192.168.31.73', NULL, NULL, '/family/member/update', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'update', 'POST', 'application/json', 1, '{\"id\":2,\"familyId\":5,\"name\":\"张无忌\",\"relation\":\"父亲\",\"phone\":\"\",\"role\":\"管理员\"}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:47:44', NULL);
INSERT INTO `sys_operation_log` VALUES (37, '0df744c9ef11422db1d0b1419b597467', '16', 'zhangsan', '生成成员绑定码', '192.168.31.73', NULL, NULL, '/family/member/ensureBindCode/1', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'ensureBindCode', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:47:52', NULL);
INSERT INTO `sys_operation_log` VALUES (38, '3a333df77fa746558b2dba566942c168', '16', 'zhangsan', '修改家族成员', '192.168.31.73', NULL, NULL, '/family/member/update', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'update', 'POST', 'application/json', 1, '{\"id\":1,\"familyId\":4,\"name\":\"张书平\",\"relation\":\"父亲\",\"phone\":\"\",\"role\":\"成员\"}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:50:14', NULL);
INSERT INTO `sys_operation_log` VALUES (39, 'c7e11c4032ff42ab9095b63a921e1492', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13200000001\",\"password\":\"bTCdmSNDOY+5KPhTYjb35OkpuvILIGMWkSbGI0a+PVVqQD37B475BQlBFcyajbRWPQQH/1MevSZp1Hef9CKO4mU4V7ZFhsAobtZCtOMpcQW8MZFGD0KYIXDRLPrwfOd3F/cP61kLEt/a2NzMwi0X8gR1qhEMzLSRK1IeLeizOUk=\",\"nickname\":\"张书平\",\"mobile\":\"13200000001\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 13:51:16', NULL);
INSERT INTO `sys_operation_log` VALUES (40, 'e2cce145a7cd4df080893ee9d64f3b25', NULL, NULL, '用户登录（短信验证码）', '192.168.31.19', NULL, NULL, '/user/login/sms', 'memorial', 'com.memorial.system.controller.UserController', 'smsLogin', 'POST', 'application/json', 1, '{\"mobile\":\"13200000001\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 13:51:39', NULL);
INSERT INTO `sys_operation_log` VALUES (41, 'f873928a3c7141d0972bae794bdebb21', '16', 'zhangsan', '生成成员绑定码', '192.168.31.73', NULL, NULL, '/family/member/ensureBindCode/1', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'ensureBindCode', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:53:56', NULL);
INSERT INTO `sys_operation_log` VALUES (42, '634fa1c388944e79bd06fc2beacf0ffa', '16', 'zhangsan', '生成成员绑定码', '192.168.31.73', NULL, NULL, '/family/member/ensureBindCode/1', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'ensureBindCode', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:57:10', NULL);
INSERT INTO `sys_operation_log` VALUES (43, 'ac0f2e49a3ad46708508db3f15473b84', '18', '13200000001', '扫码绑定成员', '192.168.31.19', NULL, NULL, '/family/member/bindByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'bindByCode', 'POST', 'application/json', 1, '{\"bindCode\":\"6D3E06F9\"}', '6f20e945d90b1d5b9feca86ee50942f9', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 13:57:29', NULL);
INSERT INTO `sys_operation_log` VALUES (44, '9ba508feaaf6483fb0cdb9749b66f670', '16', 'zhangsan', '生成成员绑定码', '192.168.31.73', NULL, NULL, '/family/member/ensureBindCode/1', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'ensureBindCode', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 2, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '该成员已绑定用户，无需生成绑定码', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:57:36', NULL);
INSERT INTO `sys_operation_log` VALUES (45, '411a547c11254d81b6810ead6b02a790', '16', 'zhangsan', '生成成员绑定码', '192.168.31.73', NULL, NULL, '/family/member/ensureBindCode/2', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'ensureBindCode', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 13:59:15', NULL);
INSERT INTO `sys_operation_log` VALUES (46, 'a8ef4f5c4aa547e0acfed03faeef114c', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13200000002\",\"password\":\"NmCwbcHuv9S+PSHMrTW1f7tdJrXxsqmlAgVr0we1quWK/xtDt/1MbnHfsYsVIysHX1k9RZuRSZcFYyfwz7WXRF0hQu3x8N8WrWMfL93lhNRxf3YVyg59OYM9GkzM4vVxIP/zwyrrF5Jq8sXYg3gjVoGDgafo8yIdjPxFaikvIgQ=\",\"nickname\":\"张无忌\",\"mobile\":\"13200000002\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:00:17', NULL);
INSERT INTO `sys_operation_log` VALUES (47, '0060b4a397a5481989a399b3e1173de2', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"1320000002\",\"password\":\"HkHJqTSVwkQilNeQrT1L5eSijWpHfbLs8tNESaOeaTkBLgA00+BGcu0oaKB6jC8hsQlGzUxuizE1/qDV3TN55QOLrGhqQcpq8vkdKckEFjyuHhAPIAVArfEGZbSg60021PnpMCzdZuVjPbMwNAZGv/oVnpMHGSuPLxa9FbILfOE=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:00:45', NULL);
INSERT INTO `sys_operation_log` VALUES (48, '0169cc28bd9541eea5480be682c5bc75', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000002\",\"password\":\"DT4bVI0UH0OPwEDLHleB8xJwmOWi1ZUJhlWelTUCMBXNrIpDsUlEHfW5WqhPMfuEa6NS96aR5fc9f+V3C6gs2khLz/IaMUIzsE2zn8UWU5fDlrK+73BKZCBgdlMc8H98XNRnr/R0uhXsYehgEzmbUb8BBbVhOHdd15J0SxQc9gY=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:00:49', NULL);
INSERT INTO `sys_operation_log` VALUES (49, 'da3e114431a74ad2afabf951a037c413', '19', '13200000002', '扫码绑定成员', '192.168.31.19', NULL, NULL, '/family/member/bindByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'bindByCode', 'POST', 'application/json', 1, '{\"bindCode\":\"D542D765\"}', '834afc48a41a601c10aa45ccf93433cc', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:00:51', NULL);
INSERT INTO `sys_operation_log` VALUES (50, '6c5c310d22834968acaf1017b528848b', '16', 'zhangsan', '添加家族成员', '192.168.31.73', NULL, NULL, '/family/member/add', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'add', 'POST', 'application/json', 1, '{\"familyId\":5,\"name\":\"赵敏\",\"relation\":\"母亲\",\"phone\":\"\",\"role\":\"成员\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:01:55', NULL);
INSERT INTO `sys_operation_log` VALUES (51, 'fe2339c75acb47e7b49a9c0b3a9c77a3', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13200000003\",\"password\":\"Wp76Px4yLrV24bwAhKR7xRpmUTMXRAG+2uwIhoOwWe8K4L7UM48yebJon9k6VpgHGWvLCxJtLJs0mXo9so1ZqBBbJwZ1wvda7bIkp6cSaEtyiNM7NPcN1IjoSPCKf+aDcaFDRkUwqR4LY0umh8iiMHVKxhjLavqALLu2b5FsLMM=\",\"nickname\":\"赵敏\",\"mobile\":\"13200000003\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:03:14', NULL);
INSERT INTO `sys_operation_log` VALUES (52, '1d8a31376a984dc9a31f4e891a17aded', NULL, NULL, '用户登录（短信验证码）', '192.168.31.19', NULL, NULL, '/user/login/sms', 'memorial', 'com.memorial.system.controller.UserController', 'smsLogin', 'POST', 'application/json', 1, '{\"mobile\":\"13200000003\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:03:27', NULL);
INSERT INTO `sys_operation_log` VALUES (53, '5488b03d8f60415fa3d1bcc6c263e6c8', '20', '13200000003', '扫码绑定成员', '192.168.31.19', NULL, NULL, '/family/member/bindByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'bindByCode', 'POST', 'application/json', 1, '{\"bindCode\":\"A0D6CF1B\"}', '244cd23dea99c2cfc58155b85baad931', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:03:29', NULL);
INSERT INTO `sys_operation_log` VALUES (54, 'b41701460ab04cd59de2cf2cbf993883', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13200000004\",\"password\":\"ZMXk/s5qEtu7K/ePY4s/y08jTdICcfRExrqj869AJzif8579j2qh90xIwZAGxfwwQ4ACPWP2Qe8ekYIT3mitOr5Wqh9AFgOFtJ4DkpjYOBZuAZ135Mcm71dKt46PJw3WSSNUur6jPEODUR8MShXPMqROP1qWzZpdtdpzWzW8qvc=\",\"nickname\":\"张小凡\",\"mobile\":\"13200000004\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:05:44', NULL);
INSERT INTO `sys_operation_log` VALUES (55, '01ac23acee754cfa98e189fc2c24ada1', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000004\",\"password\":\"DyVVH/9Io9/VgJwZYETrpJCxIScdq3zRN95Fq/ItErg5lXVI6GQSNWH64wIbWKuW3pokT5nxCfqmYCLkXocR7fwb5kmfbnTz9vKuraKbOQx2ROraOdV/mTU1PvXnML74Af1ve//wzeFv9mcuO5I7QAIb7HwX1uMG08IsoL1RwAI=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:05:55', NULL);
INSERT INTO `sys_operation_log` VALUES (56, 'd999112a39974172a411286446937b05', '16', 'zhangsan', '新增墓碑', '192.168.31.73', NULL, NULL, '/tomb/add', 'memorial', 'com.memorial.system.controller.TombController', 'add', 'POST', 'application/json', 1, '{\"name\":\"张翼德\",\"photo\":\"http://192.168.31.73:2168/file/0Tf8kHwi3DR8HI4MY7QHLVerV0JzBEKC\",\"birthday\":\"2020-01-31\",\"deathday\":\"2026-03-20\",\"biography\":\"<p><br></p>\",\"story\":\"\",\"epitaph\":\"\",\"visitorActionOpen\":true,\"familyId\":6,\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:08:48', NULL);
INSERT INTO `sys_operation_log` VALUES (57, '7064a69651474ba0b95fa6747e995fd7', '21', '13200000004', '申请加入家族', '192.168.31.19', NULL, NULL, '/family/member/apply', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'apply', 'POST', 'application/json', 1, '{\"familyId\":6,\"relation\":\"儿子\",\"reason\":\"这是一个测试\"}', '18126aff65c0ec669ebce87c741a87f5', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:12:59', NULL);
INSERT INTO `sys_operation_log` VALUES (58, 'cc21e35c70e14f63bf98072a45c6adfd', '21', '13200000004', '申请加入家族', '192.168.31.19', NULL, NULL, '/family/member/apply', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'apply', 'POST', 'application/json', 1, '{\"familyId\":6,\"relation\":\"父亲\",\"reason\":\"这是一个测试\"}', '18126aff65c0ec669ebce87c741a87f5', 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '您已提交过申请，请等待审核', 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:13:10', NULL);
INSERT INTO `sys_operation_log` VALUES (59, '76ace7e6b3b84fd8951e7894131944e6', '16', 'zhangsan', '通过家族成员申请', '192.168.31.73', NULL, NULL, '/family/member/apply/approve/1', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'approveApply', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:13:30', NULL);
INSERT INTO `sys_operation_log` VALUES (60, '612f56a9e5534edaa69e67b4078ee91e', '16', 'zhangsan', '新增墓碑', '192.168.31.73', NULL, NULL, '/tomb/add', 'memorial', 'com.memorial.system.controller.TombController', 'add', 'POST', 'application/json', 1, '{\"name\":\"张子凡\",\"photo\":\"http://192.168.31.73:2168/file/Ke5MBSc9rHpSq3ER04AdKWYaMWPmFQdb\",\"birthday\":\"2022-01-06\",\"deathday\":\"2026-02-03\",\"biography\":\"\",\"story\":\"\",\"epitaph\":\"\",\"visitorActionOpen\":true,\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:14:36', NULL);
INSERT INTO `sys_operation_log` VALUES (61, '72f6707909f743289b424b1c0dd538d6', '16', 'zhangsan', '修改墓碑', '192.168.31.73', NULL, NULL, '/tomb/update', 'memorial', 'com.memorial.system.controller.TombController', 'update', 'POST', 'application/json', 1, '{\"id\":3,\"name\":\"张子凡\",\"photo\":\"http://192.168.31.73:2168/file/Ke5MBSc9rHpSq3ER04AdKWYaMWPmFQdb\",\"birthday\":\"2022-01-06\",\"deathday\":\"2026-02-03\",\"biography\":\"\",\"story\":\"\",\"epitaph\":\"\",\"visitorActionOpen\":false,\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:15:13', NULL);
INSERT INTO `sys_operation_log` VALUES (62, '7c5b92bd59e64d3495ef75e3c45ee239', '16', 'zhangsan', '修改墓碑', '192.168.31.73', NULL, NULL, '/tomb/update', 'memorial', 'com.memorial.system.controller.TombController', 'update', 'POST', 'application/json', 1, '{\"id\":3,\"name\":\"张子凡\",\"photo\":\"http://192.168.31.73:2168/file/Ke5MBSc9rHpSq3ER04AdKWYaMWPmFQdb\",\"birthday\":\"2022-01-06\",\"deathday\":\"2026-02-03\",\"biography\":\"\",\"story\":\"\",\"epitaph\":\"\",\"visitorActionOpen\":false,\"familyId\":5,\"address\":\"\"}', 'dc3c1281adca93d607bf20c59bbfe138', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:15:52', NULL);
INSERT INTO `sys_operation_log` VALUES (63, 'f0d633d1cefe42b69aac134e9c0a89a8', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000001\",\"password\":\"B9vEfPc2BL4UoawJhjwMu+2rQBxB5HZJuLpeZrC5AGgR+mvQvk7GxqtSe7yp9BEw6Ee9RwPZQ5j8MqiUfjihido+w0wZYQ89F406UZJHRKH8lGahQ3CTlOMqdlTx9ILK2YhdSA779RfgEUUWZtIjQuOjpd8lqBCoMxj8+7BkdGc=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:21:29', NULL);
INSERT INTO `sys_operation_log` VALUES (64, 'fb77ffefb65c4ba0b1741182477929f8', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"admin\",\"password\":\"ZsgHukGPGfHd+4Bta0yvF6ytDX4Xlq/1H0XyXP0GJ2FFwZoS5h/y6XHKDhbrpb6VcykHskoVTLz0Y7O+73/0XlUqmDz8maJLjW4gT6LKElxycRyw8mjhHQG6N3tuPrLdD+YevJe2rq8SCVHRMv+qQ93NbYMai3ZLkFm7Y4ZVxIA=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:22:00', NULL);
INSERT INTO `sys_operation_log` VALUES (65, 'ecb7c3964e784ae4856dd1eb6b30a450', NULL, NULL, '退出登录（当前用户）', '192.168.31.73', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, 'dc3c1281adca93d607bf20c59bbfe138', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:23:44', NULL);
INSERT INTO `sys_operation_log` VALUES (66, 'c2fe5495f3b4496f8fdb6f2542a04cb9', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000004\",\"password\":\"QK3zLQqCQAdpaPvkc46sRARqKRXybwjjo3CVDGEnerjORjEZ6hfvRgOcm6gxXHGm0iVJv4V5zxuXzIEHFF4pouaVWiw3YV5JLs+0Y4LiCdVQX+SyRzGMEMj1U1scL1Ygvxsniwxt/0cuOaOdng0xAHAS9PX5mtiZ/xb1bgRdS+w=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:23:56', NULL);
INSERT INTO `sys_operation_log` VALUES (67, '9c0c2065472d4f3fb9c54013ee659119', NULL, NULL, '退出登录（当前用户）', '192.168.31.73', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, 'cc7739815348dbd7ffc01be3c95a6d73', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:27:54', NULL);
INSERT INTO `sys_operation_log` VALUES (68, 'd7daaedaa6914298a809e1f9b36b681f', NULL, NULL, '用户登录（短信验证码）', '192.168.31.73', NULL, NULL, '/user/login/sms', 'memorial', 'com.memorial.system.controller.UserController', 'smsLogin', 'POST', 'application/json', 1, '{\"mobile\":\"13200000002\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:28:12', NULL);
INSERT INTO `sys_operation_log` VALUES (69, '051064882f5440b88228ddfe5f401469', NULL, NULL, '退出登录（当前用户）', '192.168.31.73', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, '7f579c7b28380f2eaa782fa687cebdac', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:30:28', NULL);
INSERT INTO `sys_operation_log` VALUES (70, '790bacbccd52416b915b08d681d2f3e5', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000001\",\"password\":\"KxiCG0C6XFExfL2PwCupeVVMYviQ/yLn9iUVAbN0m0u9CMXFmQug3RE7aBbNNrCC9KxmnajL1+8nB08MzPCPMWSfnAGwWmFaNgDVKl7e3u0cJ3L4k+RX7MU8r9bgAuhnRQfViGkQxUOBO7DVI932psLtt6vLYwB5As/fPJ73ziQ=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:30:40', NULL);
INSERT INTO `sys_operation_log` VALUES (71, '4cc292f452c64b6484213e9cfe79aa72', NULL, NULL, '退出登录（当前用户）', '192.168.31.73', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, '4eddf58022625bf5c96747908ca5d446', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:31:11', NULL);
INSERT INTO `sys_operation_log` VALUES (72, 'a149c892ea0c463498d72afdb0341fae', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000000\",\"password\":\"AoJvxtT35T2RXUvx3sHOnIsZ86873g9OwZBIiEO+AtbXYbVA1AFRiXkbRWEw/K1//bYUfmHzPdpbU75GBnZuEtedIhj1xrklc8YoIbg2oJ5E8juDOM0zprhPTpeAUmBscKwQDWASBi29HxAv5QJMNlN21ji82NzaVjp1JEa2dTg=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:31:27', NULL);
INSERT INTO `sys_operation_log` VALUES (73, '23336855a0ca4bb097a310cf4bb1194a', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000000\",\"password\":\"PPFwshkC/10FSD3Fz8HZNdZn8UVLSBXkNPkY5Citi8kg5EpH6+dh/vk46FRLOJrVR8kqVtBkrxRHIQSRjGhWUpwZJIoktl08O0+3dE+8Se5X/ys79koLqjq4umEQNFEEeO6xKWH7/Iq6FW5JEO5UPXyYfFFtD36BNdplnmqzlKY=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:31:35', NULL);
INSERT INTO `sys_operation_log` VALUES (74, '4abc0875cac749b0bf654d2659d561b3', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000000\",\"password\":\"Joe6vd794xvQRT922vglW8s4c8BglmA6aWXfVpRGnwolvzoDAuVbcMziGwgtGD2rIA4PnYzUHHX4UPmrBhpskXgk9mQi19vNmv1NXzWGu4ZzRZ0/kNFPspPOO17FYfNiR+80lczU43Eg+MHdpsllTb+OnPYgpbntEo1uczj8j+M=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:31:45', NULL);
INSERT INTO `sys_operation_log` VALUES (75, '17e89ed538704bd9b3e7306447b8b046', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13200000000\",\"password\":\"bMNxWIijLwKT1eUe5iu/Mih/rGynNJNtJPM5AJ80Qh/wCT+teJvXBpAarSCTBmtWhHsg3dnLuPvqgwVVt09e5FvC5HXaeoyuI1Hy5+Zkf3ZhPh/2ovnO0YI2Hxz+QuQ66AgWAr2OkgSVRqgNb4GTyIdbKYcmCz4LCPkAlyvYEBE=\"}', NULL, 1, 0, 500, NULL, 'com.memorial.common.exception.BusinessException', '用户名或密码错误', 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:31:50', NULL);
INSERT INTO `sys_operation_log` VALUES (76, 'ab917f4e94d448dc9571449c7c62883e', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"zhangsan\",\"password\":\"gAogc5Db3taji8BUV/PIV4rCF1ltIhpHefHeylPRs31DBcWLIpofJ4wWWgjaDWoZKDCTW+vtOCSAaNGdjbDzNTjTQ9x1qk/1xHZ1U2fZtaKA4OapR3INtY6URFVxObHlwpWl5HdthwfMp5tO1GTPGbvYoYR2j5d2V0aSPX/wxh8=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:32:07', NULL);
INSERT INTO `sys_operation_log` VALUES (77, '50c437a8208949ee9898752410026777', '16', 'zhangsan', '审核通过留言', '192.168.31.73', NULL, NULL, '/tomb/message/approve/2', 'memorial', 'com.memorial.system.controller.TombMessageController', 'approve', 'POST', NULL, 0, NULL, 'fabaa16f13678e3e79e76026f8add9e4', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:32:21', NULL);
INSERT INTO `sys_operation_log` VALUES (78, '8fe0a86b243c45f4856560f1491a16f6', '16', 'zhangsan', '审核通过留言', '192.168.31.73', NULL, NULL, '/tomb/message/approve/1', 'memorial', 'com.memorial.system.controller.TombMessageController', 'approve', 'POST', NULL, 0, NULL, 'fabaa16f13678e3e79e76026f8add9e4', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:32:22', NULL);
INSERT INTO `sys_operation_log` VALUES (79, '8fb92d6307b94e2d8781c8eabaaf9deb', NULL, NULL, '退出登录（当前用户）', '192.168.31.73', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, 'fabaa16f13678e3e79e76026f8add9e4', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:35:44', NULL);
INSERT INTO `sys_operation_log` VALUES (80, 'd3272e3010da496fa5c96f6d16490955', NULL, NULL, '用户登录（账号密码）', '192.168.31.73', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"lisi\",\"password\":\"Tr8QlUQm7wZZdciiJxpRfGICNP2kihCR52otJ+aaoAAayr+Lvlapy0P7OwfOHsdRn1ziyatfMT+6GcFS+1LXKQkhRXzMa3eCCvlx/EB5MCHfkp82703ne7MZi+BQ3DP6SxStYRwytXRn4EeV+eTNXjQlXyYzTbumK8Lf55CZpKU=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:35:55', NULL);
INSERT INTO `sys_operation_log` VALUES (81, 'ce4183e0ea4646b4a87030a4e9cc0948', '17', 'lisi', '新增家族', '192.168.31.73', NULL, NULL, '/family/add', 'memorial', 'com.memorial.system.controller.FamilyController', 'add', 'POST', 'application/json', 1, '{\"pid\":0,\"name\":\"李四家\",\"description\":\"\",\"phone\":\"\",\"address\":\"\"}', 'dd325b347df79bb5c06c50e49c73949c', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:36:17', NULL);
INSERT INTO `sys_operation_log` VALUES (82, '219ad590a7c84038b7640fbc27a063ed', '17', 'lisi', '新增墓碑', '192.168.31.73', NULL, NULL, '/tomb/add', 'memorial', 'com.memorial.system.controller.TombController', 'add', 'POST', 'application/json', 1, '{\"name\":\"李天帝\",\"photo\":\"http://192.168.31.73:2168/file/taLrxkmlIIUyrugr91S7GRkRme8aHwgP\",\"birthday\":\"2026-03-01\",\"deathday\":\"2026-03-31\",\"biography\":\"<p>\\t李槐是中国网络动画《剑来》及其衍生作品中的男性角色，骊珠洞天人士，十境武夫李二之子，母亲善撒泼骂街。作为气运天命之子，被列为四大内定十五境人选之一，是齐静春最小的弟子，自幼受杨老头偏爱。其姐李柳为远古水神转世，家族成员对其极为疼爱<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[3-4]</span>。</p><p>\\t李槐在山崖书院求学期间，与李宝瓶、林守一等人同窗时目睹崔东山施法，因顽劣举动被李宝瓶揪耳塞苹果<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[2]&nbsp;[5]</span>，期间意外获得剑修圣地蝉蜕洞天并收服七彩神鹿。及冠后与裴钱游历北俱芦洲，拜蛮荒天下十四境老瞎子为师习得武夫聚音成线之法<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[3]</span>。</p><p>\\t李槐福缘深厚，与裴钱共同游历时展现出超越贺小凉和黄庭的福运表现，两人关系日益密切<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[3]</span>。其父李二曾在大隋皇宫与守门人吴鉞大战并晋入十境<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[5]</span>。</p>\",\"story\":\"\",\"epitaph\":\"世间气运10斗，李天帝独得12斗\",\"visitorActionOpen\":true,\"address\":\"\"}', 'dd325b347df79bb5c06c50e49c73949c', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:38:11', NULL);
INSERT INTO `sys_operation_log` VALUES (83, '32e3d7cfda4a4fc1881f9fbec5eeff17', '17', 'lisi', '修改墓碑', '192.168.31.73', NULL, NULL, '/tomb/update', 'memorial', 'com.memorial.system.controller.TombController', 'update', 'POST', 'application/json', 1, '{\"id\":4,\"name\":\"李天帝\",\"photo\":\"http://192.168.31.73:2168/file/taLrxkmlIIUyrugr91S7GRkRme8aHwgP\",\"birthday\":\"2026-03-01\",\"deathday\":\"2026-03-31\",\"biography\":\"<p>\\t李槐是中国网络动画《剑来》及其衍生作品中的男性角色，骊珠洞天人士，十境武夫李二之子，母亲善撒泼骂街。作为气运天命之子，被列为四大内定十五境人选之一，是齐静春最小的弟子，自幼受杨老头偏爱。其姐李柳为远古水神转世，家族成员对其极为疼爱<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[3-4]</span>。</p><p>\\t李槐在山崖书院求学期间，与李宝瓶、林守一等人同窗时目睹崔东山施法，因顽劣举动被李宝瓶揪耳塞苹果<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[2]&nbsp;[5]</span>，期间意外获得剑修圣地蝉蜕洞天并收服七彩神鹿。及冠后与裴钱游历北俱芦洲，拜蛮荒天下十四境老瞎子为师习得武夫聚音成线之法<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[3]</span>。</p><p>\\t李槐福缘深厚，与裴钱共同游历时展现出超越贺小凉和黄庭的福运表现，两人关系日益密切<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]&nbsp;[3]</span>。其父李二曾在大隋皇宫与守门人吴鉞大战并晋入十境<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[5]</span>。</p>\",\"story\":\"\",\"epitaph\":\"世间气运10斗，李天帝独得12斗\",\"visitorActionOpen\":true,\"familyId\":7,\"address\":\"\"}', 'dd325b347df79bb5c06c50e49c73949c', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:38:21', NULL);
INSERT INTO `sys_operation_log` VALUES (84, '8e43febdcc994d59a54362d1b259c6a2', '17', 'lisi', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":4,\"title\":\"角色简介\",\"content\":\"<p>\\t李二之子，本书气运天命之子，在山崖书院读书。福源深厚，书院中得七彩鹿认主。与裴钱关系极好，一起游历了北俱芦洲。福运之好，甚至超过贺小凉和黄庭。蛮荒天下老瞎子（十四境）的徒弟<span style=\\\"color: rgb(51, 102, 204);\\\">&nbsp;[1]</span>。</p><p><br></p>\",\"sort\":0}', 'dd325b347df79bb5c06c50e49c73949c', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:38:47', NULL);
INSERT INTO `sys_operation_log` VALUES (85, '93ad0b38dd5c4f819d5625212423076b', '17', 'lisi', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":4,\"title\":\"角色经历\",\"content\":\"<p>\\t李槐在家里的安排下，跟随陈平安前往大隋山崖书院求学。在山崖书院期间，他结识了众多好友，包括阿良、陈平安等，与他们称兄道弟，共同度过了许多难忘的时光。在求学过程中，李槐虽然有时会偷懒，但在李宝瓶等勤奋好学的同学的影响下，偶尔也会认真学习。</p><p>\\t李槐福缘深厚。他曾随手一捡，就得到了剑修圣地蝉蜕洞天，这座洞天对宝瓶洲本土剑修有着非比寻常的意义，却被与剑道根本扯不上关系的李槐收入囊中。他还莫名其妙地得到了一头跟随大儒的五彩神鹿追随。</p><p>\\t在与宝瓶游历剑气长城之时，李槐有了一个十四境的师傅，身边又有了位十三境的嫩道人大妖。及冠后，他与裴钱一同游历北俱芦洲，渐渐地有了独当一面的风采，学会了武夫的聚音成线之法。</p><p>\\t角色关系</p><p>\\t李槐出生于骊珠洞天，他的父亲李二是十境武夫，实力强悍，他的母亲虽然看似普通，但实际上也是小镇上的民风高手之一，性格泼辣，对李槐疼爱有加。他的姐姐李柳是远古水神转世，对弟弟李槐更是疼爱备至。</p>\",\"sort\":0}', 'dd325b347df79bb5c06c50e49c73949c', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:39:02', NULL);
INSERT INTO `sys_operation_log` VALUES (86, '9f3a8d90385440eeb430e90ba03b98bc', '17', 'lisi', '修改墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/update', 'memorial', 'com.memorial.system.controller.TombStoryController', 'update', 'POST', 'application/json', 1, '{\"id\":6,\"tombId\":4,\"title\":\"角色经历\",\"content\":\"<p>\\t李槐在家里的安排下，跟随陈平安前往大隋山崖书院求学。在山崖书院期间，他结识了众多好友，包括阿良、陈平安等，与他们称兄道弟，共同度过了许多难忘的时光。在求学过程中，李槐虽然有时会偷懒，但在李宝瓶等勤奋好学的同学的影响下，偶尔也会认真学习。</p><p>\\t李槐福缘深厚。他曾随手一捡，就得到了剑修圣地蝉蜕洞天，这座洞天对宝瓶洲本土剑修有着非比寻常的意义，却被与剑道根本扯不上关系的李槐收入囊中。他还莫名其妙地得到了一头跟随大儒的五彩神鹿追随。</p><p>\\t在与宝瓶游历剑气长城之时，李槐有了一个十四境的师傅，身边又有了位十三境的嫩道人大妖。及冠后，他与裴钱一同游历北俱芦洲，渐渐地有了独当一面的风采，学会了武夫的聚音成线之法。</p><p><br></p>\",\"sort\":0}', 'dd325b347df79bb5c06c50e49c73949c', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:39:12', NULL);
INSERT INTO `sys_operation_log` VALUES (87, 'a729ce83bbf14bb19af7543b16e1b146', '17', 'lisi', '新增墓碑事迹', '192.168.31.73', NULL, NULL, '/tomb/story/add', 'memorial', 'com.memorial.system.controller.TombStoryController', 'add', 'POST', 'application/json', 1, '{\"tombId\":4,\"title\":\"角色关系\",\"content\":\"<p>\\t李槐出生于骊珠洞天，他的父亲李二是十境武夫，实力强悍，他的母亲虽然看似普通，但实际上也是小镇上的民风高手之一，性格泼辣，对李槐疼爱有加。他的姐姐李柳是远古水神转世，对弟弟李槐更是疼爱备至。</p>\",\"sort\":0}', 'dd325b347df79bb5c06c50e49c73949c', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:39:36', NULL);
INSERT INTO `sys_operation_log` VALUES (88, 'a50a2cf9ec71437ab4e282f100f7d8c5', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13300000001\",\"password\":\"FMgjrIpX6969LECpHqALp5Yqh8e547S/YEHO0Ruhg8LNn7iC6BB5pKOuB3yj1l7OknyuGqyZ5SjlSNn5l0KVaXY2bI3Z11TuWqLNX/ZkXROC9qO5Bqz4+ISdhRrtM319JAMCIfyrjt1LF9iew6B+g3JOSq+Q220paPM6OvtGv7Y=\",\"nickname\":\"李柳\",\"mobile\":\"13300000001\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '145.0.7632.159', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'SM-S9260', '', '2026-03-20 14:43:18', NULL);
INSERT INTO `sys_operation_log` VALUES (89, 'd1a268e8701e448b9812069c5c087606', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13300000001\",\"password\":\"I4Xb4s2odjhG/JdySSrQg0g/c6SiCtSpcr1kKTdCB4d38xAHLIoxIneHXQ8aVfbJdtVxcTOHgf/sovpBN09Y/BPD58VBKGH+MsX/bExV6nYxdS6ilLF/2m34ZWfG+yfoQmXlhAzhwIdDZ2H5/xqVTANCp9FIAZSiXNF3DXX6D/4=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '145.0.7632.159', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'SM-S9260', '', '2026-03-20 14:43:30', NULL);
INSERT INTO `sys_operation_log` VALUES (90, 'a10103d6d75c4d4abcdf3b223ffd84ee', '22', '13300000001', '通过邀请码加入家族', '192.168.31.19', NULL, NULL, '/family/member/joinByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'joinByCode', 'POST', 'application/json', 1, '{\"inviteCode\":\"A3C45671\",\"relation\":\"女儿\",\"role\":\"成员\"}', '6d69140bceb4236e8370ef253a14ae25', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '145.0.7632.159', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'SM-S9260', '', '2026-03-20 14:46:26', NULL);
INSERT INTO `sys_operation_log` VALUES (91, '27f1f4d602e646fb8cc3e44f02028635', '17', 'lisi', '添加家族成员', '192.168.31.73', NULL, NULL, '/family/member/add', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'add', 'POST', 'application/json', 1, '{\"familyId\":7,\"name\":\"李凡\",\"relation\":\"儿子\",\"phone\":\"\",\"role\":\"成员\"}', 'dd325b347df79bb5c06c50e49c73949c', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:47:55', NULL);
INSERT INTO `sys_operation_log` VALUES (92, 'f920138d06d440d88c37d79be68de0b4', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13300000002\",\"password\":\"aeQVGMHKD+AINLnd9OJCJ0ZE/xOahwEt5JdjN71d7hDAgpLmmDulcKBOcB4arib6JyfuIBPyaZ2UpxFLMhZzZnwoJAyH8xurTUxo04vjviCD6l/E0UtpRMrP1A85GdkflJ47PmTAFX7XcaOEPGp5n8THmDVi6ZKxw0ab6xT/QL0=\",\"nickname\":\"李凡\",\"mobile\":\"13300000002\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:49:19', NULL);
INSERT INTO `sys_operation_log` VALUES (93, 'd9a7b499ae1344fd9c8d18e14be0cb88', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13300000002\",\"password\":\"AQDcYSTkbZl53OuJQ7CqxU4Lkjq1Hvq3kKxhmWGuUuUwc9jE/jPGtxuec1S1YiH/31T+H8mlvIGKNyTmfYBkfZb9m4aqg27vtbD++hvBvRK9MF0Xcsb9QADEwtr7sgP+seRhywcQkxPtyJXj88kCBoY/abRJ9IMevX+PU65/elA=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:49:34', NULL);
INSERT INTO `sys_operation_log` VALUES (94, '19d2b0c4fe9e4fa5ab9e254b392fdce5', '23', '13300000002', '扫码绑定成员', '192.168.31.19', NULL, NULL, '/family/member/bindByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'bindByCode', 'POST', 'application/json', 1, '{\"bindCode\":\"E99CD6F7\"}', 'cc1c8f0fcd28bc7653d75e4ebf46de23', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:49:36', NULL);
INSERT INTO `sys_operation_log` VALUES (95, '943f9bf31ddb48d89ac92d1fa9627d4e', '1', 'admin', '审核通过留言', '192.168.31.73', NULL, NULL, '/tomb/message/approve/4', 'memorial', 'com.memorial.system.controller.TombMessageController', 'approve', 'POST', NULL, 0, NULL, '46d7ce0021fe8b9a14f6cc87ca1df718', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:50:43', NULL);
INSERT INTO `sys_operation_log` VALUES (96, '01ae795165b04023a34f6b4a347dc8ce', '1', 'admin', '审核通过留言', '192.168.31.73', NULL, NULL, '/tomb/message/approve/3', 'memorial', 'com.memorial.system.controller.TombMessageController', 'approve', 'POST', NULL, 0, NULL, '46d7ce0021fe8b9a14f6cc87ca1df718', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:50:44', NULL);
INSERT INTO `sys_operation_log` VALUES (97, '7fab3ea3baec4daabfee757873985a0e', NULL, NULL, '用户注册', '192.168.31.19', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13300000003\",\"password\":\"DVqLYf5cH7OVN8+D6H/hWuDhwkUQGApFj0tFn49nVXQmGyi1EjAccHdWbShHDno9kjAaY5PMTG04AzLgz3c0VTzGD8l0Lh5LtEjk5WSZQeIS+HdLV7ZzQI/apQMyt6MbBKKgVSYcNYWWLP8FHNFf0rzecWCcNlo19xwJHB8+ccc=\",\"nickname\":\"李白\",\"mobile\":\"13300000003\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:52:45', NULL);
INSERT INTO `sys_operation_log` VALUES (98, '689e99b7200c4eff8d84235a505c1f16', NULL, NULL, '用户登录（账号密码）', '192.168.31.19', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"13300000003\",\"password\":\"OCLS7+3Vll64YEjqAbCRGFeM5oKs08NaTwe7Gr8RSK1tkHpDWAelCejpI7XJlSQlBLyIheugVkUkcHWQw+8XyMrteCnELhkJo+wHI0G1TuVqQWuXFJdsgIPMytTKZxfR7/4RaTxVpfsxnjvXd31KW6HmO9aeKrtBs78eQrIOQ84=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:53:12', NULL);
INSERT INTO `sys_operation_log` VALUES (99, 'bca035f73c4b4ab790c941b7c3a033d2', '24', '13300000003', '通过邀请码加入家族', '192.168.31.19', NULL, NULL, '/family/member/joinByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'joinByCode', 'POST', 'application/json', 1, '{\"inviteCode\":\"A3C45671\",\"relation\":\"兄弟\",\"role\":\"成员\"}', 'f7aa8f8e0057635df73f306a2539d003', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 14:53:18', NULL);
INSERT INTO `sys_operation_log` VALUES (100, '6c4a9c064ab84d38ada051ad8d007dc2', '17', 'lisi', '修改用户（当前用户）', '192.168.31.73', NULL, NULL, '/user/update', 'memorial', 'com.memorial.system.controller.UserController', 'update', 'POST', 'application/json', 1, '{\"headImg\":\"http://192.168.31.73:2168/file/2voBY6TUA9RiCyMpdJVCSeCCR38N2eC2\"}', 'dd325b347df79bb5c06c50e49c73949c', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:54:54', NULL);
INSERT INTO `sys_operation_log` VALUES (101, 'a29f8a3e11584c7ca86f389db778e9e5', '17', 'lisi', '修改用户（当前用户）', '192.168.31.73', NULL, NULL, '/user/update', 'memorial', 'com.memorial.system.controller.UserController', 'update', 'POST', 'application/json', 1, '{\"headImg\":\"http://192.168.31.73:2168/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd\"}', 'dd325b347df79bb5c06c50e49c73949c', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 14:55:59', NULL);
INSERT INTO `sys_operation_log` VALUES (179, '18269377f9704bad8475e98fc80af714', NULL, NULL, '用户登录（账号密码）', '113.87.152.199', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"admin\",\"password\":\"WZ3lI94TpciFXnYuIZ/OnU7UWxFW3Lb22bTF0ufzGsu0YX63vXtq4QBVzp1EsXE/Hht+xWAyIn7D6k4uy2VRkP1RxE7Mn2hcSF4XyqPgZ4I9MWJXY8/pFmBEne4LIeJPhycmH6E2Af5ptUt/dOUc6tmPI6v6FNRYa9ftN8Fki7E=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 15:43:29', NULL);
INSERT INTO `sys_operation_log` VALUES (180, '65e5628b20f7480d8de95375b25f78be', '1', 'admin', '审核通过留言', '113.87.152.199', NULL, NULL, '/tomb/message/approve/6', 'memorial', 'com.memorial.system.controller.TombMessageController', 'approve', 'POST', NULL, 0, NULL, '968f702798d7e97dc4498e4507c0faf0', 2, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 15:51:25', NULL);
INSERT INTO `sys_operation_log` VALUES (181, '9bfeef7027094e759201cc2c96aae6bd', NULL, NULL, '退出登录（当前用户）', '113.87.152.199', NULL, NULL, '/user/logOut', 'memorial', 'com.memorial.system.controller.UserController', 'logOut', 'POST', NULL, 0, NULL, '968f702798d7e97dc4498e4507c0faf0', 14, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 16:00:52', NULL);
INSERT INTO `sys_operation_log` VALUES (182, '0091ddbd5b984f1dac6aeb3d739c0930', NULL, NULL, '用户登录（账号密码）', '113.87.152.199', NULL, NULL, '/user/login/password', 'memorial', 'com.memorial.system.controller.UserController', 'passwordLogin', 'POST', 'application/json', 1, '{\"username\":\"admin\",\"password\":\"Hhc4Iwe96LGDIv6D8TQi9iXNO/lauZbGEx15z9k+9KFkyHRfWliNy97DPUtBaYtwZU4U4otgHPlYrkAVQkkicvDEuaKBpaxm0FZfB+ERK27U29f7GejXRD1UhxqbB8y5UZjOWywSCg84At6bqwQMs306VisrM20N1/Ct6SDncKU=\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '146.0.0.0', 'Webkit', '537.36', 'Windows 10 or Windows Server 2016', 'Windows', 0, NULL, 'Win64; x64', '', '2026-03-20 16:02:31', NULL);
INSERT INTO `sys_operation_log` VALUES (183, '3c5cd23214514c16ba148bea2383fe56', NULL, NULL, '用户注册', '113.87.152.199', NULL, NULL, '/user/register', 'memorial', 'com.memorial.system.controller.UserController', 'register', 'POST', 'application/json', 1, '{\"username\":\"13300000005\",\"password\":\"dJ/J9raNJWwuEpPEHVirniDuffqCBWz6xDzUCltNIAINysLDe3RJ0AMnXElSjU83aWnyuurl2shUVaSST7xICTamRzv/fV4Dw4Wjby5/9IdUVMFpJET7EfuPalZsIm7yVTHoT5o2l/q1ENxkJXEc/V/dI0D+71zsZj48zGAFYe0=\",\"nickname\":\"李沫\",\"mobile\":\"13300000005\",\"smsCode\":\"666666\"}', NULL, 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 16:08:44', NULL);
INSERT INTO `sys_operation_log` VALUES (184, '3a3bbc55ad5d481c9cf33aa6ce259e2a', '25', '13300000005', '通过邀请码加入家族', '113.87.152.199', NULL, NULL, '/family/member/joinByCode', 'memorial', 'com.memorial.system.controller.FamilyMemberController', 'joinByCode', 'POST', 'application/json', 1, '{\"inviteCode\":\"A3C45671\",\"relation\":\"姐妹\",\"role\":\"成员\"}', 'a7c9df78f9f8d02f494a449cdb8efa66', 1, 1, 200, '操作成功', NULL, NULL, 'Chrome', '121.0.6167.71', 'Webkit', '537.36', 'Android', 'Android', 1, 'BP2A.250605.031.A3', 'zh-cn; SM-S9260', '', '2026-03-20 16:13:12', NULL);

-- ----------------------------
-- Table structure for tomb
-- ----------------------------
DROP TABLE IF EXISTS `tomb`;
CREATE TABLE `tomb`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '逝者姓名',
  `photo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片URL',
  `photos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '生前照片（多张，逗号分隔）',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `deathday` date NULL DEFAULT NULL COMMENT '逝世日期',
  `biography` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介',
  `story` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '生平事迹',
  `epitaph` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '墓志铭（不超过64字）',
  `visitor_action_open` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否开放访客互动（留言/献花等）：1开放 0仅家族成员',
  `family_id` bigint NULL DEFAULT NULL COMMENT '所属家族ID',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所处位置',
  `qr_code_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '二维码识别标识（随机字符串，唯一）',
  `visit_count` int NULL DEFAULT 0 COMMENT '访问量',
  `message_count` int NULL DEFAULT 0 COMMENT '留言数',
  `user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qr_code_key`(`qr_code_key`) USING BTREE,
  INDEX `idx_tomb_family_del`(`family_id`, `del_flag`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '墓碑表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tomb
-- ----------------------------
INSERT INTO `tomb` VALUES (1, '张三丰', 'https://ny.px-apricot.cn/api/file/kagppUpROoQoK661TYPd3kjNzD7Xrpjz', NULL, '2020-01-01', '2026-03-01', '<p>	张三丰（生卒年不详）<span style=\"color: rgb(51, 102, 204);\">&nbsp;[1]&nbsp;[17]&nbsp;[19-21]</span>，是辽东<a href=\"https://baike.baidu.com/item/%E6%87%BF%E5%B7%9E/10344882?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">懿州</a>人<span style=\"color: rgb(51, 102, 204);\">&nbsp;[30]</span>。名全一，一名君宝，三丰其号也。以其不修边幅，又号张邋遢<span style=\"color: rgb(51, 102, 204);\">&nbsp;[30]</span>。自称<a href=\"https://baike.baidu.com/item/%E5%BC%A0%E9%81%93%E9%99%B5/1707760?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">张道陵</a>后裔，又名彭俊、全一、思廉、玄素、玄化、三仹、三峰、君实<span style=\"color: rgb(51, 102, 204);\">&nbsp;[29]</span>，字铉一、蹋仙、居宝、昆阳、剌闼、元元、玄玄、符元，号三侔、三丰子、玄玄子<span style=\"color: rgb(51, 102, 204);\">&nbsp;[2-8]&nbsp;[11]&nbsp;[13-15]&nbsp;[18]</span>。</p><p>	元世祖<a href=\"https://baike.baidu.com/item/%E4%B8%AD%E7%BB%9F/19140493?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">中统</a>元年，官至中山博陵令。大耳圆目、须髯如戟，居宝鸡<a href=\"https://baike.baidu.com/item/%E9%87%91%E5%8F%B0%E8%A7%82/7970580?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">金台观</a>时曾“阳神出游”、时隐时现。皇帝封其“犹龙六祖隐仙寓化虚微普度天尊”、“通微显化真人”、“韬光尚志真仙”、“清虚元妙真君”、“飞龙显化宏仁济世真君”等<span style=\"color: rgb(51, 102, 204);\">&nbsp;[15]&nbsp;[19]</span>。</p><p>	张三丰影响后世最深的是在前代理论及技艺的基础上，开创了中国武术中的内家功夫——武当派。张三丰深受阴阳平衡、动静相生理念的影响，于是根据道家“道法自然”“处柔守雌”理论，创造了以养生为首、防身为要，以柔克刚、以静制动、借力打力、后发制人的独特功理与拳法，为中华武术的发展作出了突出的贡献。</p>', '', '明可明，非常明。道可道，非常道', 1, 3, '武当山', 'Hdk2aTAt59G9', 25, 3, 16, 1, '2026-03-20 11:25:22', 16, '2026-03-20 11:43:42', 16, 0);
INSERT INTO `tomb` VALUES (2, '张翼德', 'https://ny.px-apricot.cn/api/file/0Tf8kHwi3DR8HI4MY7QHLVerV0JzBEKC', NULL, '2020-01-31', '2026-03-20', '<p><br></p>', '', '', 1, 6, '', 'JPMXTL5vJnnm', 12, 1, 16, 1, '2026-03-20 14:08:48', 16, NULL, NULL, 0);
INSERT INTO `tomb` VALUES (3, '张子凡', 'https://ny.px-apricot.cn/api/file/Ke5MBSc9rHpSq3ER04AdKWYaMWPmFQdb', NULL, '2022-01-06', '2026-02-03', '', '', '', 0, 5, '', 'bHLxf34PS5qn', 8, 1, 16, 1, '2026-03-20 14:14:36', 16, '2026-03-20 14:15:53', 16, 0);
INSERT INTO `tomb` VALUES (4, '李天帝', 'https://ny.px-apricot.cn/api/file/taLrxkmlIIUyrugr91S7GRkRme8aHwgP', NULL, '2026-03-01', '2026-03-31', '<p>	李槐是中国网络动画《剑来》及其衍生作品中的男性角色，骊珠洞天人士，十境武夫李二之子，母亲善撒泼骂街。作为气运天命之子，被列为四大内定十五境人选之一，是齐静春最小的弟子，自幼受杨老头偏爱。其姐李柳为远古水神转世，家族成员对其极为疼爱<span style=\"color: rgb(51, 102, 204);\">&nbsp;[1]&nbsp;[3-4]</span>。</p><p>	李槐在山崖书院求学期间，与李宝瓶、林守一等人同窗时目睹崔东山施法，因顽劣举动被李宝瓶揪耳塞苹果<span style=\"color: rgb(51, 102, 204);\">&nbsp;[2]&nbsp;[5]</span>，期间意外获得剑修圣地蝉蜕洞天并收服七彩神鹿。及冠后与裴钱游历北俱芦洲，拜蛮荒天下十四境老瞎子为师习得武夫聚音成线之法<span style=\"color: rgb(51, 102, 204);\">&nbsp;[1]&nbsp;[3]</span>。</p><p>	李槐福缘深厚，与裴钱共同游历时展现出超越贺小凉和黄庭的福运表现，两人关系日益密切<span style=\"color: rgb(51, 102, 204);\">&nbsp;[1]&nbsp;[3]</span>。其父李二曾在大隋皇宫与守门人吴鉞大战并晋入十境<span style=\"color: rgb(51, 102, 204);\">&nbsp;[5]</span>。</p>', '', '世间气运10斗，李天帝独得12斗', 1, 7, '', 'aXWHEepPhedv', 3, 1, 17, 1, '2026-03-20 14:38:11', 17, '2026-03-20 14:38:21', 17, 0);

-- ----------------------------
-- Table structure for tomb_checkin
-- ----------------------------
DROP TABLE IF EXISTS `tomb_checkin`;
CREATE TABLE `tomb_checkin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `tomb_id` bigint NOT NULL COMMENT '墓碑ID',
  `visitor_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访客姓名',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '打卡类型：献花/点蜡烛/扫码访问',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tomb_id`(`tomb_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '墓碑打卡记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tomb_checkin
-- ----------------------------
INSERT INTO `tomb_checkin` VALUES (1, 1, '张小凡', 21, '献花', '192.168.31.19', NULL, '2026-03-20 14:05:55', 0);
INSERT INTO `tomb_checkin` VALUES (2, 2, '张小凡', 21, '献花', '192.168.31.19', NULL, '2026-03-20 14:12:42', 0);
INSERT INTO `tomb_checkin` VALUES (3, 2, '张小凡', 21, '点蜡烛', '192.168.31.19', NULL, '2026-03-20 14:12:43', 0);
INSERT INTO `tomb_checkin` VALUES (4, 2, '张小凡', 21, '献花', '192.168.31.19', NULL, '2026-03-20 14:12:45', 0);
INSERT INTO `tomb_checkin` VALUES (5, 3, '张小凡', 21, '献花', '192.168.31.19', NULL, '2026-03-20 14:15:24', 0);
INSERT INTO `tomb_checkin` VALUES (6, 3, '张书平', 18, '献花', '192.168.31.19', NULL, '2026-03-20 14:21:29', 0);
INSERT INTO `tomb_checkin` VALUES (7, 3, '张书平', 18, '献花', '192.168.31.19', NULL, '2026-03-20 14:21:32', 0);
INSERT INTO `tomb_checkin` VALUES (8, 3, '张书平', 18, '点蜡烛', '192.168.31.19', NULL, '2026-03-20 14:21:33', 0);
INSERT INTO `tomb_checkin` VALUES (9, 1, '张书平', 18, '献花', '192.168.31.19', NULL, '2026-03-20 14:23:01', 0);
INSERT INTO `tomb_checkin` VALUES (10, 1, '张书平', 18, '点蜡烛', '192.168.31.19', NULL, '2026-03-20 14:23:01', 0);
INSERT INTO `tomb_checkin` VALUES (11, 2, '李凡', 23, '献花', '192.168.31.19', NULL, '2026-03-20 14:50:02', 0);
INSERT INTO `tomb_checkin` VALUES (12, 2, '李凡', 23, '点蜡烛', '192.168.31.19', NULL, '2026-03-20 14:50:03', 0);
INSERT INTO `tomb_checkin` VALUES (13, 1, '李凡', 23, '点蜡烛', '192.168.31.19', NULL, '2026-03-20 14:50:27', 0);
INSERT INTO `tomb_checkin` VALUES (14, 1, '李凡', 23, '献花', '192.168.31.19', NULL, '2026-03-20 14:50:27', 0);
INSERT INTO `tomb_checkin` VALUES (15, 1, '李凡', 23, '献花', '192.168.31.19', NULL, '2026-03-20 14:50:27', 0);
INSERT INTO `tomb_checkin` VALUES (16, 4, '李凡', 23, '献花', '192.168.31.19', NULL, '2026-03-20 14:51:26', 0);
INSERT INTO `tomb_checkin` VALUES (17, 4, '李凡', 23, '点蜡烛', '192.168.31.19', NULL, '2026-03-20 14:51:26', 0);

-- ----------------------------
-- Table structure for tomb_message
-- ----------------------------
DROP TABLE IF EXISTS `tomb_message`;
CREATE TABLE `tomb_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `tomb_id` bigint NOT NULL COMMENT '墓碑ID',
  `visitor_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '留言人姓名',
  `user_id` bigint NULL DEFAULT NULL COMMENT '留言用户ID（可为空，匿名留言）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '留言人头像',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '留言内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '审核状态：pending待审核 approved已通过 rejected已拒绝',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝理由',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tomb_id`(`tomb_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '墓碑留言表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tomb_message
-- ----------------------------
INSERT INTO `tomb_message` VALUES (1, 3, '张书平', 18, NULL, '你好呀！', 'approved', NULL, '2026-03-20 14:21:40', 18, '2026-03-20 14:32:23', NULL, 0);
INSERT INTO `tomb_message` VALUES (2, 1, '张书平', 18, NULL, '这是一个测试', 'approved', NULL, '2026-03-20 14:23:10', 18, '2026-03-20 14:32:21', NULL, 0);
INSERT INTO `tomb_message` VALUES (3, 2, '李凡', 23, NULL, '你好', 'approved', NULL, '2026-03-20 14:50:11', 23, '2026-03-20 14:50:44', NULL, 0);
INSERT INTO `tomb_message` VALUES (4, 1, '李凡', 23, NULL, '你好你好', 'approved', NULL, '2026-03-20 14:50:36', 23, '2026-03-20 14:50:43', NULL, 0);
INSERT INTO `tomb_message` VALUES (5, 4, '李凡', 23, NULL, '天帝', 'approved', NULL, '2026-03-20 14:51:35', 23, '2026-03-20 15:51:25', NULL, 0);
INSERT INTO `tomb_message` VALUES (6, 1, '李白', 24, 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', '这是一个长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长测试', 'approved', NULL, '2026-03-20 15:51:15', 24, '2026-03-20 15:51:26', NULL, 0);

-- ----------------------------
-- Table structure for tomb_story
-- ----------------------------
DROP TABLE IF EXISTS `tomb_story`;
CREATE TABLE `tomb_story`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tomb_id` bigint NOT NULL COMMENT '墓碑ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事迹标题',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事迹内容（富文本HTML）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `create_time` datetime NULL DEFAULT NULL,
  `create_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `del_flag` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tomb_story_tomb_id`(`tomb_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '墓碑生平事迹' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tomb_story
-- ----------------------------
INSERT INTO `tomb_story` VALUES (1, 1, '早年经历', '<p>	<a href=\"https://baike.baidu.com/item/%E5%AE%8B%E6%9C%AB/35286?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">宋末</a>	<span style=\"color: rgb(51, 51, 51);\">元初，张三丰出生。生有异质，龟形鹤骨，大耳圆目。身长七尺余，修髯如戟，顶作一髻，常戴偃月冠。一笠一衲，寒暑御之。不饰</span>	<a href=\"https://baike.baidu.com/item/%E8%BE%B9%E5%B9%85/10947312?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">边幅</a>	<span style=\"color: rgb(51, 51, 51);\">，人皆目为张邋遢</span>	<span style=\"color: rgb(51, 51, 51);\">。</span></p><p>	<span style=\"color: rgb(51, 51, 51);\">张三丰五岁时，目染异疾，积久渐昏。当时有道人名张云庵，是方外异人，住持碧落宫，自号白云禅老。见而奇之曰：此子仙风道骨，自非凡器，但目遭</span>	<a href=\"https://baike.baidu.com/item/%E9%AD%94%E9%9A%9C/7519630?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">魔障</a>	<span style=\"color: rgb(51, 51, 51);\">，须拜贫道为弟子，了脱尘翳，慧珠再朗，即送还。林氏许之，遂投云庵为徒。静居半载而目渐明，教习道经过目便晓，有暇兼读儒、释两家之书，随手披阅，会通其大意即止</span>	<span style=\"color: rgb(51, 51, 51);\">。</span></p><p>	张三丰十三岁时，其母林氏想念他，张云庵也不留，于是张三丰拜辞归家，专究儒业。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[26]</span></p><p>	<a href=\"https://baike.baidu.com/item/%E5%85%83/2634049?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">元</a>世祖<a href=\"https://baike.baidu.com/item/%E4%B8%AD%E7%BB%9F/19140493?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">中统</a>元年（1260年），张三丰举茂才异等。</p><p>	元中统二年（1261年），张三丰文学才识，列名上闻，以备擢用，然而做官不是其向来的志向。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[26]</span></p><p>	元<a href=\"https://baike.baidu.com/item/%E8%87%B3%E5%85%83/386003?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">至元</a>元年（1264年）秋，游燕京。当时元朝刚刚在燕京定都，诏令旧列文学才识者待用。张三丰声望渐起，始与<a href=\"https://baike.baidu.com/item/%E5%B9%B3%E7%AB%A0%E6%94%BF%E4%BA%8B/9533026?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">平章政事</a><a href=\"https://baike.baidu.com/item/%E5%BB%89%E5%B8%8C%E5%AE%AA/1408671?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">廉希宪</a>相交。廉希宪惊异他的才能，奏补为中山博陵令。</p>', 0, '2026-03-20 11:46:52', 16, NULL, NULL, 0);
INSERT INTO `tomb_story` VALUES (2, 1, '弃官归隐', '<p>	元至元二年（1265年），张三丰父母病逝，张三丰弃官<a href=\"https://baike.baidu.com/item/%E4%B8%81%E5%BF%A7/998469?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">丁忧</a>，不久断绝仕进的想法，奉讳归辽阳，终日哀毁。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[26]</span>闲居数年，张三丰束装出游，将田产托付给族人，嘱代扫墓，带领二行童相随。游历各地名山古刹，吟咏闲观，且行且住。几乎三十年，均无所遇。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[26-27]</span></p><p>	元延祐年间，张三丰六十七岁，入终南山修炼，拜<a href=\"https://baike.baidu.com/item/%E7%81%AB%E9%BE%99%E7%9C%9F%E4%BA%BA/19911111?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">火龙真人</a>为师，得金丹之旨。山居四载，功效寂然。闻近斯道者，必须<a href=\"https://baike.baidu.com/item/%E6%B3%95%E8%B4%A2/1970359?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">法财</a>两用，平生游访，兼颇好善，囊箧殆空，不觉泪下，火龙怪之，进告以故，乃传<a href=\"https://baike.baidu.com/item/%E4%B8%B9%E7%A0%82/3405377?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">丹砂</a>点化之诀，命出山修炼。立辞恩师，和光混俗者数年。</p>', 0, '2026-03-20 11:47:08', 16, NULL, NULL, 0);
INSERT INTO `tomb_story` VALUES (3, 1, '得道成真', '<p>	元泰定元年（1324年）春，南至<a href=\"https://baike.baidu.com/item/%E6%AD%A6%E5%BD%93%E5%B1%B1/84549?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">武当山</a>，调神九载而道成。隐显遨游又十余年。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[27]</span></p><p>	元至正初年，由楚还辽阳，省墓讫，复之燕市，公卿故交，死亡已尽矣。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[27]</span></p><p>	元至正十九年（1359年），秦淮渔户沈万山，又名万三，好善乐施，张三丰传道于沈万三。沈万三自号三山道士。临别，先生预知万三有徙边之祸，嘱曰：东南王气正盛，当晤子于西南也。不久返回陕西，居宝鸡金台观。</p><p>	<span style=\"color: rgb(51, 51, 51);\">元至正二十六年九月二十日（1366年10月30日</span>	<span style=\"color: rgb(51, 51, 51);\">）</span>	<span style=\"color: rgb(51, 51, 51);\">，自言辞世，留颂而逝。士民杨轨山置棺殓讫，临窆复生。</span></p>', 0, '2026-03-20 11:47:23', 16, '2026-03-20 11:47:35', 16, 0);
INSERT INTO `tomb_story` VALUES (4, 1, '踪迹莫测', '<p>	明洪武十七年（1384年），明太祖<a href=\"https://baike.baidu.com/item/%E6%9C%B1%E5%85%83%E7%92%8B/25626?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">朱元璋</a>以华夷宾服，诏求张三丰，不赴。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[27]</span></p><p>	明洪武十八年（1385年），<a href=\"https://baike.baidu.com/item/%E6%98%8E%E5%A4%AA%E7%A5%96/2329247?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">明太祖</a>强迫<a href=\"https://baike.baidu.com/item/%E6%B2%88%E4%B8%87%E4%B8%89/868735?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">沈万三</a>敦请张三丰，不赴。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[27]</span></p><p>	明洪武二十四年（1391年），明太祖派遣三山道士沈万三寻访，不得。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[22]&nbsp;[24]</span></p><p>	明洪武二十五年（1392年），张三丰遁入云南，恰逢明太祖徙<a href=\"https://baike.baidu.com/item/%E6%B2%88%E4%B8%87%E4%B8%89/868735?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">沈万三</a>于海上，缘此践约来会，同炼天元服食大药。</p><p>	明洪武二十六年（1393年），药成，张三丰至贵州<a href=\"https://baike.baidu.com/item/%E5%B9%B3%E8%B6%8A/6536003?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">平越</a><a href=\"https://baike.baidu.com/item/%E7%A6%8F%E6%B3%89%E5%B1%B1/7379833?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">福泉山</a>，朝真礼斗，候诏飞升。</p><p>	明永乐五年（1407年），<a href=\"https://baike.baidu.com/item/%E6%98%8E%E6%88%90%E7%A5%96/449258?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">明成祖</a>派遣<a href=\"https://baike.baidu.com/item/%E7%BB%99%E4%BA%8B%E4%B8%AD/1081312?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">给事中</a>胡濴、指挥杨永吉等寻访张三丰，不得。<span style=\"color: rgb(51, 102, 204);\">&nbsp;[25]&nbsp;[28]</span></p><p>	明永乐十年（1413年），命<a href=\"https://baike.baidu.com/item/%E5%AD%99%E7%A2%A7%E4%BA%91/4769455?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">孙碧云</a>于<a href=\"https://baike.baidu.com/item/%E6%AD%A6%E5%BD%93/16139?fromModule=lemma_inlink\" rel=\"noopener noreferrer\" target=\"_blank\" style=\"color: rgb(19, 110, 194);\">武当</a>建宫拜候，并致书相请，不得。</p>', 0, '2026-03-20 11:47:50', 16, NULL, NULL, 0);
INSERT INTO `tomb_story` VALUES (5, 4, '角色简介', '<p>	李二之子，本书气运天命之子，在山崖书院读书。福源深厚，书院中得七彩鹿认主。与裴钱关系极好，一起游历了北俱芦洲。福运之好，甚至超过贺小凉和黄庭。蛮荒天下老瞎子（十四境）的徒弟<span style=\"color: rgb(51, 102, 204);\">&nbsp;[1]</span>。</p><p><br></p>', 0, '2026-03-20 14:38:47', 17, NULL, NULL, 0);
INSERT INTO `tomb_story` VALUES (6, 4, '角色经历', '<p>	李槐在家里的安排下，跟随陈平安前往大隋山崖书院求学。在山崖书院期间，他结识了众多好友，包括阿良、陈平安等，与他们称兄道弟，共同度过了许多难忘的时光。在求学过程中，李槐虽然有时会偷懒，但在李宝瓶等勤奋好学的同学的影响下，偶尔也会认真学习。</p><p>	李槐福缘深厚。他曾随手一捡，就得到了剑修圣地蝉蜕洞天，这座洞天对宝瓶洲本土剑修有着非比寻常的意义，却被与剑道根本扯不上关系的李槐收入囊中。他还莫名其妙地得到了一头跟随大儒的五彩神鹿追随。</p><p>	在与宝瓶游历剑气长城之时，李槐有了一个十四境的师傅，身边又有了位十三境的嫩道人大妖。及冠后，他与裴钱一同游历北俱芦洲，渐渐地有了独当一面的风采，学会了武夫的聚音成线之法。</p><p><br></p>', 0, '2026-03-20 14:39:02', 17, '2026-03-20 14:39:11', 17, 0);
INSERT INTO `tomb_story` VALUES (7, 4, '角色关系', '<p>	李槐出生于骊珠洞天，他的父亲李二是十境武夫，实力强悍，他的母亲虽然看似普通，但实际上也是小镇上的民风高手之一，性格泼辣，对李槐疼爱有加。他的姐姐李柳是远古水神转世，对弟弟李槐更是疼爱备至。</p>', 0, '2026-03-20 14:39:36', 17, NULL, NULL, 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户编号',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `head_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd' COMMENT '头像',
  `sex` tinyint NULL DEFAULT NULL COMMENT '性别：1男 2女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盐',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '角色：admin/user',
  `login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0注销 1正常 2禁用 3冻结 4临时冻结',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识：0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`, `del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '8820260101000001', 'admin', '管理员', '13100000000', 'https://ny.px-apricot.cn/api/file/x4J1WwUegYd60dOhduZ2bSWOPBGfBIKJ', 1, '2026-01-01', '', '01ad451503b2245158a65c2a1a598cf9120b13bff668a896070725f8d84e1327', '1054bdb71c2b985f191441f92f5b1c4b18f077fd3f9b57e619fbc430ce59e025', 'admin', '2026-03-20 16:02:31', 1, '2026-03-13 17:44:57', NULL, '2026-03-16 14:26:26', 1, 0);
INSERT INTO `user` VALUES (16, '8820260320194818', 'zhangsan', '张三', '13200000000', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', 1, NULL, NULL, 'b209ead5a10d1bbe124bad4eb4a8a5c9a9af9e0fb3d0bcb4a9c6fee1def8b9e6', '0101e8ba929c7231e0d3c1d58d3209065857e2805c1046cbfc4163bf81da3e21', 'user', '2026-03-20 14:32:07', 1, '2026-03-20 10:54:19', 1, '2026-03-20 10:54:19', 1, 0);
INSERT INTO `user` VALUES (17, '8820260320469805', 'lisi', '李四', '13300000000', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', 1, NULL, NULL, '79c47a31142f4a10dd463e089d4bc3cbb8b5e3c85228ebc83613ddf64743e06f', '458df67b44bebb0746c599de124810a338b3a171d68e9a67c29bc6813456975b', 'user', '2026-03-20 14:35:55', 1, '2026-03-20 10:55:05', 1, '2026-03-20 14:55:59', 17, 0);
INSERT INTO `user` VALUES (18, '8820260320893058', '13200000001', '张书平', '13200000001', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, '065964285ff5cc9b4aceb0dbf1c223fc4328a3bf5cbf1a3301b69dbe9c674b70', '69e6006c98f0148bf272ead0001f8f358e8cbfe0f483b3570c7a6e7bf0bc43e8', 'user', '2026-03-20 14:30:40', 1, '2026-03-20 13:51:16', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (19, '8820260320141132', '13200000002', '张无忌', '13200000002', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, '7d25d0163e8a5ab524c4809aed71dc5ca5e23974e7999d523f2d0c15a4ea3e65', '48367d28708c95ee8dacccbc271219c107f510ca77d7ad18c74b267b97a8c532', 'user', '2026-03-20 14:28:12', 1, '2026-03-20 14:00:17', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (20, '8820260320594612', '13200000003', '赵敏', '13200000003', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, '9ae4b01b47beb4f2e5afb8f5d2a9534d45e11fdcbd08ae8615307737c602d0f7', 'f16ee6681973001abc1d2299ccb721c80cf13b5d74cd50973a229d38cb5b7450', 'user', '2026-03-20 14:03:27', 1, '2026-03-20 14:03:14', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (21, '8820260320387374', '13200000004', '张小凡', '13200000004', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, 'd9bb96b7d1ced012c093dcc548fcd54c007ab8e98cc4363f32ecd8392b29a359', 'f471970ba295ad889322ad4f08ca4aa77c4f29ee0f83c5f2d81495c5501dc7c6', 'user', '2026-03-20 14:23:56', 1, '2026-03-20 14:05:44', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (22, '8820260320938035', '13300000001', '李柳', '13300000001', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, 'baffd899fea576d8538d01881ede2956d7a717747b3bd100d6bfd2db7599bc32', '77159222ae1ec96d1d5a10dea1d506c70a1704465ddcad30e6dc15302a10abdb', 'user', '2026-03-20 16:01:02', 1, '2026-03-20 14:43:18', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (23, '8820260320344513', '13300000002', '李凡', '13300000002', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, '8aa1321be4132e55562a6bb9631e532d70efb860491face9e99cb840e39ab0c7', '628db3a53288995bad7ae15e6249bf0b837a1a7a87454418b9920d4af406883d', 'user', '2026-03-20 14:49:34', 1, '2026-03-20 14:49:19', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (24, '8820260320614770', '13300000003', '李白', '13300000003', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, '185c8b91231dcf613b041f9ae6d20f1e9e3a2673d3ba1088e9c9b7edb2779a99', 'ab7978a4b92ea136d128f6260bb46adcd47b002ce9fa09d05e9dda3f60b35553', 'user', '2026-03-20 15:50:21', 1, '2026-03-20 14:52:45', NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (25, '8820260320676810', '13300000005', '李沫', '13300000005', 'https://ny.px-apricot.cn/api/file/6Fh6K3oPeqPLXtA43FBuj9Q2H0WGYzkd', NULL, NULL, NULL, '928e0ee12a11f25f2f47996ee40f82e01fb066564f3e288aaf476ba1116bba04', '66005ad730913209679a68d31ad8251e312e1d02723647eb16fd98eb9befc3c5', 'user', '2026-03-20 16:13:08', 1, '2026-03-20 16:08:43', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 1);
INSERT INTO `user_role` VALUES (12, 16, 2);
INSERT INTO `user_role` VALUES (13, 17, 2);
INSERT INTO `user_role` VALUES (14, 18, 2);
INSERT INTO `user_role` VALUES (15, 19, 2);
INSERT INTO `user_role` VALUES (16, 20, 2);
INSERT INTO `user_role` VALUES (17, 21, 2);
INSERT INTO `user_role` VALUES (18, 22, 2);
INSERT INTO `user_role` VALUES (19, 23, 2);
INSERT INTO `user_role` VALUES (20, 24, 2);
INSERT INTO `user_role` VALUES (21, 25, 2);

SET FOREIGN_KEY_CHECKS = 1;
