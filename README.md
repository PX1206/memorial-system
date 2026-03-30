# memorial-system

> 🪦 念园系统：让记忆可被看见，让思念有所寄托

在实体墓碑上刻制二维码，扫码即可进入逝者的数字纪念空间。  
支持展示生平事迹、照片、留言互动、访客打卡，并构建家族/宗族关系体系。

---

## ✨ 项目亮点

- 📱 扫码即访问：无需登录
- 🪦 数字墓碑：线上 + 线下结合
- 💬 留言系统：支持审核
- 📍 打卡功能：访客留痕
- 👨‍👩‍👧‍👦 家族体系
- 🔐 权限管理
- 🗂 文件管理
- 🔔 墓碑个人提醒：忌日/生辰/节日/自定义日期，支持提前提醒与短信通知（农历墓碑与自定义日期语义一致）

---

## 🧾 功能预览

### 📌 墓碑二维码

<p align="center">
  <img src="https://github.com/user-attachments/assets/7f6097eb-ffb9-4faa-87f0-3147d6d2dd3a" width="240"/>
</p>

---

### 📱 扫码访问页面

<p align="center">
  <img src="https://github.com/user-attachments/assets/ac6662e3-46ee-47a4-b0a1-6c2048d01bef" width="600"/>
</p>

---

### 👤 个人简介 & 生平事迹

<p align="center">
  <img src="https://github.com/user-attachments/assets/b5ad1d8d-d84f-48ae-8ac8-01c43c284d1a" width="400"/>
  <img src="https://github.com/user-attachments/assets/a1b546f2-c62c-4707-9581-578f4f0eaeb4" width="400"/>
</p>

---

### 💬 留言 & 📍 打卡

<p align="center">
  <img src="https://github.com/user-attachments/assets/c7bcadca-a0d5-402f-bba3-0392568b783e" width="400"/>
  <img src="https://github.com/user-attachments/assets/dfb67911-be18-48bf-9e35-66d0de424194" width="400"/>
</p>

---

### 🛠 后台管理界面

<details>
<summary>点击展开查看</summary>

<p align="center">
  <img src="https://github.com/user-attachments/assets/ac010751-082a-4202-8ed0-fc9de5b05dd5" width="800"/>
</p>

</details>

---

## 🔗 在线体验

- 🌐 https://ny.px-apricot.cn/

---

## 🧱 技术栈

- 后端：Spring Boot、MyBatis-Plus
- 前端：Vue3、Vite、Element Plus
- 数据库：MySQL
- 中间件：Redis、RocketMQ

---

## 📁 项目结构

```text
memorial-system/
├── memorial-service/
├── memorial-common/
├── memorial-generator/
├── memorial-vue/
├── sql/
└── pom.xml
```

---

## 📚 文档索引

- [模块说明](docs/modules.md)：各子工程职责、墓碑提醒等业务边界
- [部署指南](docs/deployment.md)：环境要求、后端/前端生产部署、Nginx 示例
- [数据库说明](docs/database.md)：`memorial_db.sql` 与增量脚本、字符集说明
- [API 说明](docs/api.md)：Knife4j 地址、接口前缀、认证与响应格式

---

## 🚀 快速开始

### 初始化数据库

脚本 `sql/memorial_db.sql` 不含建库语句，需先在 MySQL 中创建数据库，再导入表结构：

```bash
mysql -u root -p -e "CREATE DATABASE memorial_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p memorial_db < sql/memorial_db.sql
```

### 启动后端

```bash
mvn clean install
cd memorial-service
mvn spring-boot:run
```

### 启动前端

```bash
cd memorial-vue
npm install
npm run dev
```

---

## 📦 部署

生产环境打包、配置修改、Nginx 与前后端联调等步骤见 [docs/deployment.md](docs/deployment.md)。

---

## 📄 License

MIT
