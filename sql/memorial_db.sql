-- =============================================
-- Memorial System 数据库建表脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS `memorial_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `memorial_db`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_no` VARCHAR(32) DEFAULT NULL COMMENT '用户编号',
    `username` VARCHAR(64) NOT NULL COMMENT '账号',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `head_img` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `sex` TINYINT DEFAULT NULL COMMENT '性别：1男 2女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `salt` VARCHAR(255) DEFAULT NULL COMMENT '盐',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `role` VARCHAR(32) DEFAULT 'user' COMMENT '角色：admin/user',
    `login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0注销 1正常 2禁用 3冻结 4临时冻结',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 文件表
CREATE TABLE IF NOT EXISTS `file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(64) NOT NULL COMMENT '文件编码',
    `name` VARCHAR(255) DEFAULT NULL COMMENT '文件名称',
    `type` TINYINT DEFAULT NULL COMMENT '文件类型 1.图片 2.文档 3.视频 4.音频 5.其它',
    `domain` VARCHAR(255) DEFAULT NULL COMMENT '前缀',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路径',
    `suffix` VARCHAR(32) DEFAULT NULL COMMENT '文件后缀',
    `source` TINYINT DEFAULT NULL COMMENT '来源：1用户 2商家 3管理',
    `size` INT DEFAULT NULL COMMENT '文件大小(字节)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 墓碑表
CREATE TABLE IF NOT EXISTS `tomb` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `name` VARCHAR(64) NOT NULL COMMENT '逝者姓名',
    `photo` VARCHAR(500) DEFAULT NULL COMMENT '照片URL',
    `birthday` DATE DEFAULT NULL COMMENT '出生日期',
    `deathday` DATE DEFAULT NULL COMMENT '逝世日期',
    `biography` TEXT DEFAULT NULL COMMENT '个人简介',
    `story` TEXT DEFAULT NULL COMMENT '生平事迹',
    `family_id` BIGINT DEFAULT NULL COMMENT '所属家族ID',
    `qr_code` VARCHAR(500) DEFAULT NULL COMMENT '二维码URL',
    `qr_code_key` VARCHAR(32) DEFAULT NULL COMMENT '二维码识别标识（随机字符串，唯一）',
    `visit_count` INT DEFAULT 0 COMMENT '访问量',
    `message_count` INT DEFAULT 0 COMMENT '留言数',
    `user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_qr_code_key` (`qr_code_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='墓碑表';

-- 墓碑留言表
CREATE TABLE IF NOT EXISTS `tomb_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `tomb_id` BIGINT NOT NULL COMMENT '墓碑ID',
    `visitor_name` VARCHAR(64) DEFAULT NULL COMMENT '留言人姓名',
    `user_id` BIGINT DEFAULT NULL COMMENT '留言用户ID（可为空，匿名留言）',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '留言人头像',
    `content` TEXT NOT NULL COMMENT '留言内容',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '审核状态：pending待审核 approved已通过 rejected已拒绝',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝理由',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`),
    KEY `idx_tomb_id` (`tomb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='墓碑留言表';

-- 墓碑打卡记录表
CREATE TABLE IF NOT EXISTS `tomb_checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `tomb_id` BIGINT NOT NULL COMMENT '墓碑ID',
    `visitor_name` VARCHAR(64) DEFAULT NULL COMMENT '访客姓名',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `type` VARCHAR(32) DEFAULT NULL COMMENT '打卡类型：献花/点蜡烛/扫码访问',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT 'IP地址',
    `location` VARCHAR(255) DEFAULT NULL COMMENT '位置',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`),
    KEY `idx_tomb_id` (`tomb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='墓碑打卡记录表';

-- 家族表
CREATE TABLE IF NOT EXISTS `family` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `name` VARCHAR(64) NOT NULL COMMENT '家族名称',
    `description` TEXT DEFAULT NULL COMMENT '家族简介',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '所在地区',
    `founder_id` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
    `founder_name` VARCHAR(64) DEFAULT NULL COMMENT '创建人姓名',
    `member_count` INT DEFAULT 1 COMMENT '成员数',
    `tomb_count` INT DEFAULT 0 COMMENT '墓碑数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族表';

-- 家族成员表
CREATE TABLE IF NOT EXISTS `family_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `family_id` BIGINT NOT NULL COMMENT '家族ID',
    `name` VARCHAR(64) NOT NULL COMMENT '成员姓名',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `relation` VARCHAR(32) DEFAULT NULL COMMENT '与族长关系',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `role` VARCHAR(32) DEFAULT '成员' COMMENT '家族角色：族长/管理员/成员',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族成员表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `request_id` VARCHAR(64) DEFAULT NULL COMMENT '请求ID',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    `user_name` VARCHAR(64) DEFAULT NULL COMMENT '用户名称',
    `name` VARCHAR(128) DEFAULT NULL COMMENT '日志名称',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT 'IP',
    `area` VARCHAR(128) DEFAULT NULL COMMENT '区域',
    `operator` VARCHAR(128) DEFAULT NULL COMMENT '运营商',
    `path` VARCHAR(500) DEFAULT NULL COMMENT '全路径',
    `module` VARCHAR(128) DEFAULT NULL COMMENT '模块名称',
    `class_name` VARCHAR(255) DEFAULT NULL COMMENT '类名',
    `method_name` VARCHAR(128) DEFAULT NULL COMMENT '方法名称',
    `request_method` VARCHAR(16) DEFAULT NULL COMMENT '请求方式',
    `content_type` VARCHAR(255) DEFAULT NULL COMMENT '内容类型',
    `request_body` TINYINT DEFAULT NULL COMMENT '是否是JSON请求映射参数',
    `param` TEXT DEFAULT NULL COMMENT '请求参数',
    `token` VARCHAR(255) DEFAULT NULL COMMENT 'tokenMd5值',
    `type` TINYINT DEFAULT NULL COMMENT '类型',
    `success` TINYINT DEFAULT NULL COMMENT '0:失败,1:成功',
    `code` INT DEFAULT NULL COMMENT '响应结果状态码',
    `message` VARCHAR(500) DEFAULT NULL COMMENT '响应结果消息',
    `exception_name` VARCHAR(255) DEFAULT NULL COMMENT '异常类名称',
    `exception_message` TEXT DEFAULT NULL COMMENT '异常信息',
    `browser_name` VARCHAR(128) DEFAULT NULL COMMENT '浏览器名称',
    `browser_version` VARCHAR(128) DEFAULT NULL COMMENT '浏览器版本',
    `engine_name` VARCHAR(128) DEFAULT NULL COMMENT '浏览器引擎名称',
    `engine_version` VARCHAR(128) DEFAULT NULL COMMENT '浏览器引擎版本',
    `os_name` VARCHAR(128) DEFAULT NULL COMMENT '系统名称',
    `platform_name` VARCHAR(128) DEFAULT NULL COMMENT '平台名称',
    `mobile` TINYINT DEFAULT NULL COMMENT '是否是手机',
    `device_name` VARCHAR(128) DEFAULT NULL COMMENT '移动端设备名称',
    `device_model` VARCHAR(128) DEFAULT NULL COMMENT '移动端设备型号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- 菜单表
CREATE TABLE IF NOT EXISTS `menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `pid` BIGINT DEFAULT NULL COMMENT '父ID',
    `title` VARCHAR(64) DEFAULT NULL COMMENT '菜单标题',
    `type` VARCHAR(32) DEFAULT NULL COMMENT '菜单类型',
    `permission` VARCHAR(128) DEFAULT NULL COMMENT '权限code',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件地址',
    `icon` VARCHAR(64) DEFAULT NULL COMMENT '前端菜单icon',
    `name` VARCHAR(64) DEFAULT NULL COMMENT '前端路由名',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路径',
    `redirect` VARCHAR(255) DEFAULT NULL COMMENT '重定向路径',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `hidden` TINYINT DEFAULT 0 COMMENT '是否被隐藏',
    `affix` TINYINT DEFAULT 0 COMMENT '是否被固定在tab',
    `keep_alive` TINYINT DEFAULT 0 COMMENT '是否缓存页面',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 初始管理员账号（密码为 Admin123 经过SHA256+salt加密）
-- 注意：实际使用时请通过注册接口创建管理员
INSERT INTO `user` (`user_no`, `username`, `nickname`, `mobile`, `salt`, `password`, `role`, `status`)
VALUES ('8800000001', 'admin', '管理员', '13800138000',
        'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
        '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
        'admin', 1);
