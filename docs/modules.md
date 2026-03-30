# 念园系统 — 模块说明

本文档描述仓库内各 Maven 模块、前端工程与文档目录的职责边界，便于新人上手与二次开发。内容随功能迭代更新。

---

## 仓库总览

| 路径 | 类型 | 说明 |
| ---- | ---- | ---- |
| `memorial-service` | Maven 模块 | 主业务 Spring Boot 服务，REST API、定时任务、与 MySQL/Redis/RocketMQ/短信对接 |
| `memorial-common` | Maven 模块 | 公共能力：统一响应、异常、拦截器、分页、日志、工具类等 |
| `memorial-generator` | Maven 模块 | MyBatis-Plus 代码生成器，按表生成 Entity/Mapper/Service/Controller |
| `memorial-vue` | 前端工程 | Vue3 + Vite 管理端与公开纪念页（扫码访问） |
| `sql/` | SQL 脚本 | 建库初始化与可选增量脚本 |
| `docs/` | 文档 | 部署、数据库、API、本模块说明等 |

根 `pom.xml` 聚合上述 Java 模块；前端独立在 `memorial-vue/` 目录，通过环境变量对接后端。

---

## memorial-common

**定位**：被 `memorial-service` 依赖的公共层，不包含具体业务表逻辑。

**主要内容**：

- 统一 API 封装：`ApiResult`、`ApiCode`、分页 `PageInfo` 等
- 全局异常与参数校验处理
- 登录/日志拦截、操作日志 AOP
- `BaseEntity`、`BaseController`、`BaseService` 等基类
- Redis、验证码、短信工具、RSA 等通用工具

详见 [memorial-common/README.md](../memorial-common/README.md)。

---

## memorial-service

**定位**：念园核心业务服务，对外提供 HTTP API。

**典型业务能力**：

| 领域 | 说明 |
| ---- | ---- |
| 用户与权限 | 登录注册、用户 CRUD、角色、菜单、操作日志 |
| 家族 | 家族、成员、加入申请、成员绑定 |
| 墓碑 | 逝者信息、出生/逝世日期（支持农历语义 `lunar_flag`）、二维码、生平事迹 |
| 互动 | 留言、访客打卡 |
| 公开访问 | `/open/memorial` 扫码纪念页数据 |
| 文件 | 上传、下载、预览 |
| 其他 | 验证码、地区、仪表盘、短信验证码 |
| **墓碑个人提醒** | 用户可为单块墓碑配置忌日/清明/重阳/生辰/自定义日期等提醒；支持提前若干天；**农历墓碑**下自定义日期按农历月日计算周年；短信渠道需配置阿里云模板（见 `application.yml` 中 `aliyun.sms.reminder`）；定时任务类 `TombReminderScheduleJob` 按日扫描发送（cron 可按环境调整） |

**技术栈**：Spring Boot 2.x、MyBatis-Plus、Knife4j、Druid、Redis、RocketMQ 等。

**接口文档**：启动后 `http://<host>:<port>/doc.html`（见 [api.md](api.md)）。

详见 [memorial-service/README.md](../memorial-service/README.md)。

---

## memorial-generator

**定位**：根据数据库表批量生成 `memorial-service` 包结构下的代码骨架。

**注意**：生成会覆盖已有文件时需确认 `file-override`；生成后业务逻辑仍需手写。

详见 [memorial-generator/README.md](../memorial-generator/README.md)。

---

## memorial-vue

**定位**：管理后台 + 公开纪念页 SPA。

**主要路由**（节选）：

| 路径 | 说明 |
| ---- | ---- |
| `/tomb` | 墓碑列表与编辑（含日期公历/农历、二维码等） |
| `/memorial/:id` | 公开纪念页，扫码进入 |
| 其他 | 仪表盘、家族、用户、系统管理等同原 README |

**墓碑个人提醒（前端）**：

- 纪念页 `Memorial.vue`：登录用户可配置/开关本人对该墓碑的提醒（弹窗表单，自定义日期 + 备注等）
- 墓碑列表 `TombList.vue`：同类提醒入口，移动端弹窗已做适配

环境变量 `VITE_BASE_URL` 指向后端根路径。

详见 [memorial-vue/README.md](../memorial-vue/README.md)。

---

## sql / 数据库

- **主脚本**：`sql/memorial_db.sql` — 新库初始化（须先建库再导入）
- **增量脚本**（按需执行，执行前备份）：

| 文件 | 用途（摘要） |
| ---- | ------------ |
| `alter_tomb_birthday_deathday_lunar.sql` | 墓碑出生/逝世农历相关 |
| `alter_tomb_date_components.sql` | 日期分量字段 |
| `alter_tomb_lunar_flag_merge.sql` | 农历标记合并 |
| `alter_tomb_reminder_lunar_event_types.sql` | 提醒事件类型扩展 |
| `alter_tomb_reminder_custom_remarks.sql` | 自定义日期备注列等 |

详细约定见 [database.md](database.md)。

---

## docs 目录索引

| 文档 | 内容 |
| ---- | ---- |
| [deployment.md](deployment.md) | 环境要求、前后端生产部署、Nginx 示例 |
| [database.md](database.md) | 脚本导入、字符集、升级说明 |
| [api.md](api.md) | Knife4j、路径前缀、认证与响应格式 |
| [modules.md](modules.md) | 本文，各模块职责总览 |
| `nginx-*.conf` | 示例 Nginx 配置 |

---

## 相关链接

- 根目录 [README.md](../README.md)：功能预览、快速开始
- 在线体验（若仍有效）：根 README 中的演示地址
