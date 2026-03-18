-- 家族表新增 pid 字段，支持父子层级关系
ALTER TABLE family ADD COLUMN pid BIGINT NOT NULL DEFAULT 0 COMMENT '上级家族ID，0表示顶级' AFTER id;
CREATE INDEX idx_family_pid ON family(pid);
