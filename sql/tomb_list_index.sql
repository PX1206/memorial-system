-- 墓碑列表查询优化索引（可选，按需执行）
-- 加速 getTombList、countByPermission 等按 family_id、del_flag 过滤
-- 若索引已存在会报错，可忽略或先 DROP INDEX

-- tomb 表：按 family_id 过滤、del_flag 过滤
ALTER TABLE tomb ADD INDEX idx_tomb_family_del (family_id, del_flag);

-- family 表：root_id 用于权限 JOIN
ALTER TABLE family ADD INDEX idx_family_root_id (root_id);

-- family_member：user_id + family_id 用于权限子查询
ALTER TABLE family_member ADD INDEX idx_fm_user_family (user_id, family_id, del_flag);

-- family_admin：user_id + family_id 用于权限子查询
ALTER TABLE family_admin ADD INDEX idx_fa_user_family (user_id, family_id);
