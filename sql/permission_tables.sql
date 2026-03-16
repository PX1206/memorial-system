-- ============================================
-- 权限管理相关表（角色、角色菜单、用户角色）
-- ============================================

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色标识',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `status` INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标识：1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================
-- 初始数据：角色
-- ============================================
INSERT INTO `role` (`name`, `code`, `description`, `status`, `create_time`, `del_flag`) VALUES
('超级管理员', 'admin', '系统最高权限，可管理所有功能', 1, NOW(), 0),
('普通用户', 'user', '普通注册用户，只能管理自己的数据', 1, NOW(), 0);

-- ============================================
-- 初始数据：菜单（type: dir目录 menu菜单 btn按钮）
-- ============================================

-- 如果menu表还没有数据，则插入初始菜单数据
-- 顶级目录/菜单
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(0, '首页概览', 'menu', '', 'DataAnalysis', '/dashboard', 1, 0, 0, NOW()),
(0, '墓碑管理', 'dir', '', 'Place', '', 2, 0, 0, NOW()),
(0, '家族管理', 'dir', '', 'Connection', '', 3, 0, 0, NOW()),
(0, '用户管理', 'dir', '', 'User', '', 4, 0, 0, NOW()),
(0, '系统管理', 'dir', '', 'Setting', '', 5, 0, 0, NOW());

-- 注意：以下SQL中的pid需要根据上面插入后的实际id来调整
-- 假设首页概览id=1, 墓碑管理id=2, 家族管理id=3, 用户管理id=4, 系统管理id=5

-- 墓碑管理子菜单
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(2, '墓碑列表', 'menu', 'tomb:list', '', '/tomb', 1, 0, 0, NOW()),
(2, '留言管理', 'menu', 'tomb:message', '', '/tomb/message', 2, 0, 0, NOW()),
(2, '打卡记录', 'menu', 'tomb:checkin', '', '/tomb/checkin', 3, 0, 0, NOW());

-- 家族管理子菜单
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(3, '家族列表', 'menu', 'family:list', '', '/family', 1, 0, 0, NOW()),
(3, '成员管理', 'menu', 'family:member', '', '/family/member', 2, 0, 0, NOW());

-- 用户管理子菜单
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(4, '用户列表', 'menu', 'sys:user:list', '', '/user', 1, 0, 0, NOW());

-- 系统管理子菜单
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(5, '角色管理', 'menu', 'sys:role:list', '', '/system/role', 1, 0, 0, NOW()),
(5, '菜单管理', 'menu', 'sys:menu:list', '', '/system/menu', 2, 0, 0, NOW()),
(5, '操作日志', 'menu', 'sys:log:list', '', '/system/log', 3, 0, 0, NOW());

-- ============================================
-- 按钮级别权限（以用户管理为例，pid对应用户列表菜单的id）
-- 假设用户列表菜单id=11
-- ============================================
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(11, '新增用户', 'btn', 'sys:user:add', '', '', 1, 0, 0, NOW()),
(11, '编辑用户', 'btn', 'sys:user:edit', '', '', 2, 0, 0, NOW()),
(11, '删除用户', 'btn', 'sys:user:delete', '', '', 3, 0, 0, NOW()),
(11, '重置密码', 'btn', 'sys:user:resetPwd', '', '', 4, 0, 0, NOW()),
(11, '禁用用户', 'btn', 'sys:user:disable', '', '', 5, 0, 0, NOW()),
(11, '恢复用户', 'btn', 'sys:user:restore', '', '', 6, 0, 0, NOW()),
(11, '分配角色', 'btn', 'sys:user:role', '', '', 7, 0, 0, NOW());

-- 角色管理按钮权限（假设角色管理菜单id=12）
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(12, '新增角色', 'btn', 'sys:role:add', '', '', 1, 0, 0, NOW()),
(12, '编辑角色', 'btn', 'sys:role:edit', '', '', 2, 0, 0, NOW()),
(12, '删除角色', 'btn', 'sys:role:delete', '', '', 3, 0, 0, NOW()),
(12, '权限设置', 'btn', 'sys:role:perm', '', '', 4, 0, 0, NOW());

-- 菜单管理按钮权限（假设菜单管理菜单id=13）
INSERT INTO `menu` (`pid`, `title`, `type`, `permission`, `icon`, `path`, `sort`, `hidden`, `del_flag`, `create_time`) VALUES
(13, '新增菜单', 'btn', 'sys:menu:add', '', '', 1, 0, 0, NOW()),
(13, '编辑菜单', 'btn', 'sys:menu:edit', '', '', 2, 0, 0, NOW()),
(13, '删除菜单', 'btn', 'sys:menu:delete', '', '', 3, 0, 0, NOW());

-- ============================================
-- 为超级管理员分配所有菜单权限
-- 需要在执行上面的INSERT后，根据实际的menu id来调整
-- 以下使用子查询自动关联
-- ============================================
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu` WHERE del_flag = 0;

-- 为普通用户分配基本菜单（首页、墓碑管理、家族管理）
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 2, id FROM `menu` WHERE del_flag = 0 AND (
  pid = 0 AND id IN (1, 2, 3)
  OR pid IN (2, 3)
);

-- ============================================
-- 为现有admin用户分配超级管理员角色
-- 请根据实际用户ID调整
-- ============================================
-- INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);
