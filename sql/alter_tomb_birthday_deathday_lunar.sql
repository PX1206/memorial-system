-- 【已废弃】请改用 lunar_flag 单字段，见 memorial_db.sql 或 alter_tomb_lunar_flag_merge.sql
-- 已有库：出生/逝世日期公历或农历标记（与提醒、展示联动）
ALTER TABLE `tomb`
  ADD COLUMN `birthday_lunar` tinyint(1) NOT NULL DEFAULT 0 COMMENT '出生日期是否按农历填写：0公历 1农历' AFTER `deathday`,
  ADD COLUMN `deathday_lunar` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逝世日期是否按农历填写：0公历 1农历' AFTER `birthday_lunar`;
