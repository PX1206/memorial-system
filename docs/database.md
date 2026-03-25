# 数据库脚本说明

## 仓库中的脚本

当前 `sql/` 目录下提供：

| 脚本            | 说明 |
| --------------- | ---- |
| `memorial_db.sql` | 主建表与初始化数据：用户、文件、墓碑、留言、打卡、家族、菜单与权限相关表等 |

> 历史上若曾通过独立增量 SQL 升级，请以对应版本发布说明为准；本仓库未附带历史增量脚本文件时，以当前 `memorial_db.sql` 为准做新库初始化。

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
