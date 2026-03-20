# memorial-vue（sweep-grave-admin）

念园系统前端管理后台，与 `memorial-system` 后端配套使用。墓碑刻制二维码后，扫码可进入公开纪念页查看逝者信息、留言、打卡等。

## 功能模块

| 路径 | 功能 |
|------|------|
| `/login` | 登录 |
| `/dashboard` | 仪表盘 |
| `/tomb` | 墓碑管理 |
| `/tomb/message` | 墓碑留言管理 |
| `/tomb/checkin` | 墓碑打卡管理 |
| `/family` | 家族管理 |
| `/family/member` | 家族成员 |
| `/family/join` | 加入家族 |
| `/family/member/bind` | 成员绑定 |
| `/user` | 用户列表 |
| `/profile` | 个人资料 |
| `/system/role` | 角色管理 |
| `/system/menu` | 菜单管理 |
| `/system/file` | 文件管理 |
| `/system/log` | 操作日志 |
| `/memorial/:id` | 公开纪念页（扫码访问，无需登录） |

## 技术栈

- Vue 3
- Vite 7
- Element Plus
- Pinia（状态管理）
- Vue Router 5
- Axios
- Quill（富文本）、qrcode、jsencrypt

## 环境变量

通过 `VITE_BASE_URL` 配置后端 API 地址：

- 开发环境：`.env.development`，默认 `http://192.168.31.73:2168/`
- 生产环境：`.env.production`，如 `https://sn.px-apricot.cn/`

## 开发

```sh
npm install
npm run dev
```

开发服务器默认端口：1267

## 构建

```sh
npm run build
```

## 其他命令

```sh
npm run lint      # ESLint 检查
npm run format    # Prettier 格式化
npm run preview   # 预览构建结果
```

## 与后端的关系

- 前端通过 `VITE_BASE_URL` 请求后端 API
- 后端项目：https://github.com/PX1206/memorial-system
- 本前端与 `memorial-system` 配套使用，需先启动后端服务

## IDE 推荐

- [VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)（禁用 Vetur）
- 浏览器安装 [Vue.js devtools](https://devtools.vuejs.org/)
