-- 家族成员申请表：用户申请成为家族成员，族长/管理员审核
CREATE TABLE IF NOT EXISTS `family_member_apply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `family_id` BIGINT NOT NULL COMMENT '家族ID',
    `user_id` BIGINT NOT NULL COMMENT '申请用户ID',
    `relation` VARCHAR(32) DEFAULT NULL COMMENT '与族长关系',
    `reason` VARCHAR(255) DEFAULT NULL COMMENT '申请理由',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending待审核 approved已通过 rejected已拒绝',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝理由',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '审核人',
    PRIMARY KEY (`id`),
    KEY `idx_family_id` (`family_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族成员申请表';
