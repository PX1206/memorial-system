-- 【历史脚本】由 birthday/deathday DATE 拆成 birth_year 等分量；当前主分支已恢复为 DATE + lunar 标记，新建库请用 memorial_db.sql，勿执行本脚本。
-- 已有库：由 birthday/deathday DATE 改为年月日分量存库（不做农历换算）
-- 若已执行过仅增加 lunar 标记的旧脚本，请先备份后按需合并。

ALTER TABLE `tomb`
  ADD COLUMN `birth_year` int NULL DEFAULT NULL COMMENT '出生年' AFTER `photos`,
  ADD COLUMN `birth_month` int NULL DEFAULT NULL COMMENT '出生月' AFTER `birth_year`,
  ADD COLUMN `birth_day` int NULL DEFAULT NULL COMMENT '出生日' AFTER `birth_month`,
  ADD COLUMN `birth_leap` tinyint(1) NOT NULL DEFAULT 0 COMMENT '出生闰月' AFTER `birth_day`,
  ADD COLUMN `death_year` int NULL DEFAULT NULL COMMENT '逝世年' AFTER `birth_leap`,
  ADD COLUMN `death_month` int NULL DEFAULT NULL COMMENT '逝世月' AFTER `death_year`,
  ADD COLUMN `death_day` int NULL DEFAULT NULL COMMENT '逝世日' AFTER `death_month`,
  ADD COLUMN `death_leap` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逝世闰月' AFTER `death_day`;

UPDATE `tomb` SET
  `birth_year` = YEAR(`birthday`),
  `birth_month` = MONTH(`birthday`),
  `birth_day` = DAY(`birthday`),
  `birth_leap` = 0
WHERE `birthday` IS NOT NULL;

UPDATE `tomb` SET
  `death_year` = YEAR(`deathday`),
  `death_month` = MONTH(`deathday`),
  `death_day` = DAY(`deathday`),
  `death_leap` = 0
WHERE `deathday` IS NOT NULL;

ALTER TABLE `tomb`
  DROP COLUMN `birthday`,
  DROP COLUMN `deathday`;

-- 说明：若原 birthday_lunar=1 的旧数据，DATE 曾为农历换算后的公历锚点，迁移后的分量是公历拆分；
-- 需按农历展示的用户请重新编辑保存一次。
