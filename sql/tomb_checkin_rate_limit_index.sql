-- 为献花/打卡限流（按 tomb_id + user_id + 日期统计）添加联合索引，提升 countTodayByTombAndUser 查询性能
-- 执行前请确认 tomb_checkin 表已存在
ALTER TABLE tomb_checkin ADD INDEX idx_tomb_user_date (tomb_id, user_id, create_time);
