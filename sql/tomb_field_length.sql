-- 墓碑字段长度限制：墓志铭32字、所处位置200字
-- 执行前请确认 tomb 表已存在 epitaph、address 字段
-- 若 epitaph 原为 VARCHAR(255) 或 VARCHAR(64)，可改为 32

ALTER TABLE `tomb` MODIFY COLUMN `epitaph` VARCHAR(32) DEFAULT NULL COMMENT '墓志铭（不超过32字）';

-- address 若已存在可改为 200；若不存在则添加（根据实际表结构调整）
-- ALTER TABLE `tomb` ADD COLUMN `address` VARCHAR(200) DEFAULT NULL COMMENT '所处位置（不超过200字）' AFTER `family_id`;
-- 若 address 已存在且为 VARCHAR(255)，可改为 200 以与业务一致（可选）：
-- ALTER TABLE `tomb` MODIFY COLUMN `address` VARCHAR(200) DEFAULT NULL COMMENT '所处位置（不超过200字）';
