# 部署指南

## 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 1.8+ |
| Maven | 3.x |
| MySQL | 8.0 |
| Redis | 6.2 |
| RocketMQ | 4.9.8 |
| Node.js | ^20.19.0 或 >=22.12.0（仅前端构建需要） |

## 一、数据库

1. 创建数据库并执行主脚本：

```bash
mysql -u root -p < sql/memorial_db.sql
```

2. 若为已有库升级，按需执行 `sql/` 下增量脚本，详见 [database.md](database.md)。

## 二、后端部署

### 开发环境

```bash
mvn clean install
cd memorial-service
mvn spring-boot:run
```

### 生产环境

1. 修改 `memorial-service/src/main/resources/application.yml`：
   - 数据源：`spring.datasource.url`、`username`、`password`
   - Redis：`spring.redis.host`、`port`、`password`
   - RocketMQ：`rocketmq.name-server`
   - 阿里云短信（可选）：`aliyun.sms`
   - 服务地址：`local.host`（用于生成二维码等链接）

2. 打包：

```bash
mvn clean package -pl memorial-service -am -DskipTests
```

3. 运行：

```bash
java -jar memorial-service/target/memorial-service-1.0.0.jar
```

默认端口：2168。可通过 `--server.port=8080` 修改。

## 三、前端部署

1. 修改 `memorial-vue/.env.production` 中的 `VITE_BASE_URL` 为生产环境后端 API 地址。

2. 构建：

```bash
cd memorial-vue
npm install
npm run build
```

3. 将 `memorial-vue/dist/` 部署到 Nginx 或其他静态服务器。

### Nginx 示例（ny.px-apricot.cn）

域名部署：前端 `https://ny.px-apricot.cn/`，后端 API `https://ny.px-apricot.cn/api/`。

完整配置见 [nginx-ny.px-apricot.cn.conf](nginx-ny.px-apricot.cn.conf)，简要步骤：

1. 前端构建：`cd memorial-vue && npm run build`（`.env.production` 已配置 `VITE_BASE_URL=/api/`）
2. 后端启动：`java -jar memorial-service-xxx.jar --spring.profiles.active=prod`
3. 将 `docs/nginx-ny.px-apricot.cn.conf` 复制到 Nginx，修改 `root`、`ssl_certificate`、`ssl_certificate_key` 路径
4. `nginx -t && nginx -s reload`

## 四、注意事项

- 生产环境建议将敏感配置（数据库、Redis、短信等）移至环境变量或配置中心
- Knife4j 文档建议生产环境关闭或加访问控制
- 确保 RocketMQ、Redis 已正确启动，否则服务可能启动失败或功能异常
