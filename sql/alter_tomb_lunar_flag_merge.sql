-- 已有库：将 birthday_lunar、deathday_lunar 合并为 lunar_flag（任一为农历则 1，否则 0）
-- 执行前请备份。若已仅有 lunar_flag，请勿重复执行。

ALTER TABLE `tomb`
  ADD COLUMN `lunar_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '日期历制：0公历 1农历（出生、逝世一致）' AFTER `deathday`;

UPDATE `tomb` SET `lunar_flag` = IF(`birthday_lunar` = 1 OR `deathday_lunar` = 1, 1, 0);

ALTER TABLE `tomb`
  DROP COLUMN `birthday_lunar`,
  DROP COLUMN `deathday_lunar`;
