-- 已有库升级：忌日/生辰按农历推算
ALTER TABLE `tomb_reminder`
  ADD COLUMN `lunar_event_types` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL
  COMMENT 'JSON：忌日/生辰按农历推算，取值 DEATH_ANNIVERSARY、BIRTHDAY'
  AFTER `advance_offsets`;
