# 数据库脚本说明

## 仓库中的脚本

当前 `sql/` 目录下提供：

| 脚本            | 说明 |
| --------------- | ---- |
| `memorial_db.sql` | 主建表与初始化数据：用户、文件、墓碑、留言、打卡、家族、菜单与权限、`tomb_reminder` 等相关表 |
| `alter_tomb_birthday_deathday_lunar.sql` | 墓碑出生/逝世农历相关字段（按需） |
| `alter_tomb_date_components.sql` | 日期分量（按需） |
| `alter_tomb_lunar_flag_merge.sql` | 农历标记合并（按需） |
| `alter_tomb_reminder_lunar_event_types.sql` | 墓碑提醒事件类型扩展（按需） |
| `alter_tomb_reminder_custom_remarks.sql` | 自定义日期备注列等（按需） |
| `alter_tomb_message_image_urls.sql` | 墓碑留言配图（JSON URL 数组，最多 3 张）（按需） |

> **新库**：直接执行最新的 `memorial_db.sql` 即可得到当前表结构。  
> **已有库**：按上线顺序执行缺失的 `alter_*.sql`，执行前请备份。历史上若曾通过其他渠道升级，以实际库结构为准。

## 新库初始化（执行顺序）

1. 在 MySQL 中创建数据库（若尚未创建）：

```bash
mysql -u root -p -e "CREATE DATABASE memorial_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

2. 导入主脚本（在**已选中的库** `memorial_db` 上执行，避免表建到默认库）：

```bash
mysql -u root -p memorial_db < sql/memorial_db.sql
```

## 已有库升级

若后续在仓库中增加 `sql/migrations/` 或独立增量脚本，请按各脚本注释或发行说明按需执行。执行前请**备份数据库**。

## 字符集与排序规则

库与表使用 **utf8mb4**。具体列级排序规则（如 `utf8mb4_0900_ai_ci` 等）以 `memorial_db.sql` 导出内容为准。
