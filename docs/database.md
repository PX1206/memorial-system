# 数据库脚本说明

## 执行顺序

### 新库初始化

1. **memorial_db.sql**（必选）  
   主建表脚本，创建数据库 `memorial_db` 及核心表：user、file、tomb、tomb_message、family、family_member 等。

2. **permission_tables.sql**（必选）  
   权限相关表：role、role_menu、user_role、menu、sys_operation_log 等。

### 增量升级（已有库按需执行）

以下脚本用于在已有库上增加字段或表，新库若已使用最新版 `memorial_db.sql` 可能已包含部分内容，请根据实际情况选择执行：

| 脚本 | 说明 |
|------|------|
| tomb_qr_code_key.sql | 墓碑表增加 `qr_code_key` 字段（扫码访问标识） |
| tomb_address.sql | 墓碑表增加地址相关字段 |
| tomb_field_length.sql | 墓碑表字段长度调整 |
| tomb_epitaph_story.sql | 墓碑表生平/墓志铭相关字段 |
| tomb_visitor_action_open.sql | 游客行为开关相关字段 |
| tomb_checkin_rate_limit_index.sql | 打卡限流索引 |
| family_pid.sql | 家族表增加 `pid` 字段（父级） |
| family_member_apply.sql | 家族成员申请表 |
| family_member_bind_code.sql | 家族成员绑定码相关 |
| family_restructure.sql | 家族表结构重构（若为空可忽略） |

## 执行方式

```bash
# 主脚本
mysql -u root -p memorial_db < sql/memorial_db.sql

# 权限表
mysql -u root -p memorial_db < sql/permission_tables.sql

# 增量脚本（按需）
mysql -u root -p memorial_db < sql/tomb_qr_code_key.sql
mysql -u root -p memorial_db < sql/family_member_apply.sql
# ...
```

## 字符集

数据库及表均使用 `utf8mb4`、`utf8mb4_general_ci`。
