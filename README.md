# memorial-system

念园系统。在墓碑上刻制二维码，扫码进入墓主人纪念页面，可查看照片、生平事迹，游客可留言、打卡。需用户先配置墓主人信息和权限（如是否允许游客留言、留言审核等），支持家族宗族组织体系。

## 墓碑二维码
<img width="320" height="320" alt="墓碑二维码_张三丰" src="https://github.com/user-attachments/assets/7f6097eb-ffb9-4faa-87f0-3147d6d2dd3a" />

## 扫码后的页面
![86ddf28cf236b03b5d477a4d70564e63](https://github.com/user-attachments/assets/ac6662e3-46ee-47a4-b0a1-6c2048d01bef)

## 个人简介
![1626a637e3ac14491cd658bc457be821](https://github.com/user-attachments/assets/b5ad1d8d-d84f-48ae-8ac8-01c43c284d1a)

## 生平事迹
![c510e5ff4be52171b3c49c55a70f6891](https://github.com/user-attachments/assets/a1b546f2-c62c-4707-9581-578f4f0eaeb4)

## 留言
![26f5bc94e36399bf8b25dc9579064130](https://github.com/user-attachments/assets/c7bcadca-a0d5-402f-bba3-0392568b783e)

## 打卡
![658402234607d53b42f9eb07ca6e7132](https://github.com/user-attachments/assets/dfb67911-be18-48bf-9e35-66d0de424194)

## 后台地址
https://ny.px-apricot.cn/

## 后台管理界面
<img width="1920" height="929" alt="image" src="https://github.com/user-attachments/assets/ac010751-082a-4202-8ed0-fc9de5b05dd5" />


## 功能概述

- **墓碑管理**：逝者信息、生平事迹、二维码、留言、打卡
- **家族管理**：家族、成员、加入申请、成员绑定
- **用户与权限**：用户管理、角色、菜单、操作日志
- **公开纪念页**：扫码访问，无需登录
- **文件管理**：图片、文档等上传与预览

## 技术栈

| 组件 | 版本 |
|------|------|
| JDK | 1.8 |
| MySQL | 8.0 |
| Redis | 6.2 |
| RocketMQ | 4.9.8 |
| 后端 | Spring Boot 2.7.6、MyBatis-Plus、Druid、Knife4j |
| 前端 | Vue 3、Vite 7、Element Plus、Pinia |

## 目录结构

```
memorial-system/
├── memorial-service/    # 主业务服务（Spring Boot）
├── memorial-common/     # 公共模块（异常、工具、分页等）
├── memorial-generator/  # 代码生成器
├── memorial-vue/        # 前端（Vue 3，独立项目）
├── sql/                 # 数据库脚本
└── pom.xml              # Maven 根 POM
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.x
- MySQL 8.0
- Redis
- Node.js ^20.19.0 或 >=22.12.0（前端）

### 2. 数据库初始化

```bash
# 执行主建表脚本
mysql -u root -p < sql/memorial_db.sql

# 按需执行增量脚本（见 docs/database.md）
```

### 3. 启动后端

```bash
mvn clean install
cd memorial-service
mvn spring-boot:run
```

默认端口：2168。API 文档：http://localhost:2168/doc.html

### 4. 启动前端

```bash
cd memorial-vue
npm install
npm run dev
```

默认端口：1267。修改 `.env.development` 中的 `VITE_BASE_URL` 指向后端地址。

## 部署说明

1. **后端**：`mvn -pl memorial-service package` 生成 jar，使用 `java -jar` 运行
2. **前端**：`npm run build` 生成 `dist/`，部署到 Nginx 等静态服务器
3. **配置**：修改 `application.yml` 中的数据库、Redis、RocketMQ 等为生产环境地址

详见 [docs/deployment.md](docs/deployment.md)。

## 前后端仓库关系

- **本仓库**：包含后端（memorial-service、memorial-common、memorial-generator）和前端（memorial-vue）
- **前端独立仓库**：https://github.com/PX1206/sweep-grave-admin（与 memorial-vue 同源，可单独克隆开发）

## 各模块说明

| 模块 | 说明 |
|------|------|
| [memorial-service](memorial-service/README.md) | 主业务服务 |
| [memorial-common](memorial-common/README.md) | 公共基础模块 |
| [memorial-generator](memorial-generator/README.md) | 代码生成器 |
| [memorial-vue](memorial-vue/README.md) | 前端管理后台 |

## 文档

- [部署指南](docs/deployment.md)
- [数据库脚本说明](docs/database.md)
- [API 文档](docs/api.md)
