-- 墓碑新增“是否开放访客互动”配置
-- MySQL 8.x

ALTER TABLE `tomb`
    ADD COLUMN `visitor_action_open` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否开放访客互动（留言/献花等）：1开放 0仅家族成员' AFTER `epitaph`;

