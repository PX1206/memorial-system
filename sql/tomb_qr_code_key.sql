-- 墓碑表增加二维码识别标识（用于扫码访问，不再暴露数字ID）
-- 新库已在 memorial_db.sql 中包含该字段；已有库请执行本脚本

ALTER TABLE tomb ADD COLUMN qr_code_key VARCHAR(32) DEFAULT NULL COMMENT '二维码识别标识（随机字符串，唯一）' AFTER qr_code;
ALTER TABLE tomb ADD UNIQUE KEY uk_qr_code_key (qr_code_key);
