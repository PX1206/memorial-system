-- 已有库：自定义提醒日期备注（与 custom_dates 一一对应）
ALTER TABLE `tomb_reminder`
  ADD COLUMN `custom_date_remarks` varchar(512) NULL DEFAULT NULL COMMENT 'JSON：与 custom_dates 一一对应的备注，最多3条' AFTER `custom_dates`;
