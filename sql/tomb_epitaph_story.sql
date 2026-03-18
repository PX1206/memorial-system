-- 墓碑新增墓志铭字段 + 生平事迹拆表
-- MySQL 8.x

-- 1) 墓碑表增加墓志铭字段
ALTER TABLE `tomb`
    ADD COLUMN `epitaph` VARCHAR(255) NULL COMMENT '墓志铭' AFTER `story`;

-- 2) 新增墓碑生平事迹表（多条）
CREATE TABLE IF NOT EXISTS `tomb_story` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tomb_id` BIGINT NOT NULL COMMENT '墓碑ID',
    `title` VARCHAR(100) NOT NULL COMMENT '事迹标题',
    `content` MEDIUMTEXT NOT NULL COMMENT '事迹内容（富文本HTML）',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    `create_time` DATETIME NULL,
    `create_by` BIGINT NULL,
    `update_time` DATETIME NULL,
    `update_by` BIGINT NULL,
    `del_flag` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_tomb_story_tomb_id` (`tomb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='墓碑生平事迹';

