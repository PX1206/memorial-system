# memorial-service

念园管理主业务服务模块，提供墓碑、家族、用户、权限、文件等核心功能的 REST API。

## 模块职责

- **墓碑管理**：逝者信息、生平事迹、二维码、留言、打卡
- **家族管理**：家族、成员、加入申请、成员绑定
- **用户管理**：用户 CRUD、个人资料、登录认证
- **权限管理**：角色、菜单、操作日志
- **文件管理**：文件上传、下载、预览
- **公开纪念页**：扫码访问的公开纪念页面（`/open/memorial`）
- **其他**：验证码、地区、仪表盘、短信

## 技术栈

- Spring Boot 2.7.6
- MyBatis-Plus 3.3.1
- Druid 数据源
- Knife4j（Swagger API 文档）
- Redis、RocketMQ、阿里云短信

## 启动方式

```bash
# 在项目根目录
mvn clean install
cd memorial-service
mvn spring-boot:run
```

或直接运行主类：`com.memorial.system.MemorialApplication`

## 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `server.port` | 服务端口 | 2168 |
| `spring.datasource` | MySQL 数据源 | 见 application.yml |
| `spring.redis` | Redis 连接 | 见 application.yml |
| `rocketmq.name-server` | RocketMQ 地址 | 见 application.yml |
| `knife4j.enable` | 是否开启 API 文档 | true |
| `aliyun.sms` | 阿里云短信配置 | 见 application.yml |
| `local.host` | 当前服务 IP 端口（用于生成二维码等） | 见 application.yml |

## API 文档

启动服务后访问 Knife4j 文档：

- 地址：`http://localhost:2168/doc.html`
- 默认账号：px / px123456（在 application.yml 中配置）

## 依赖的外部服务

- **MySQL 8.0**：主数据库
- **Redis**：缓存、会话等
- **RocketMQ**：消息队列
- **阿里云短信**：短信验证码（可选，不配置则短信功能不可用）

## 主要 API 路由

| 路径 | 功能 |
|------|------|
| `/captcha` | 验证码 |
| `/area` | 地区 |
| `/family`、`/family/member` | 家族、家族成员 |
| `/tomb`、`/tomb/message`、`/tomb/story`、`/tomb/checkin` | 墓碑、留言、生平、打卡 |
| `/open/memorial` | 公开纪念页（扫码访问） |
| `/user` | 用户 |
| `/role`、`/menu` | 角色、菜单 |
| `/file` | 文件 |
| `/dashboard` | 仪表盘 |
| `/sms` | 短信 |
| `/sysOperationLog` | 操作日志 |
