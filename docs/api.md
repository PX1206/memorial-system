# API 文档说明

## Knife4j 在线文档

启动 `memorial-service` 后，访问：

- **地址**：http://localhost:2168/doc.html
- **默认账号**：px / px123456（在 `application.yml` 的 `knife4j.basic` 中配置）

Knife4j 提供完整的接口列表、参数说明、在线调试功能。

## 主要 API 概览

下表为后端服务**根路径**下的前缀。生产环境若经 Nginx 将 API 挂在 **`/api/`** 下（与 `memorial-vue` 的 `VITE_BASE_URL=/api/` 一致），实际请求路径需加上此前缀，例如文档中的 `/user` 对应 `https://你的域名/api/user`。

| 路径前缀        | 功能           |
| --------------- | -------------- |
| /captcha        | 验证码         |
| /area           | 地区           |
| /family         | 家族           |
| /family/member  | 家族成员       |
| /tomb           | 墓碑           |
| /tomb/reminder  | 墓碑个人提醒（配置、开关、忌日/节日/自定义日期等） |
| /tomb/message   | 墓碑留言       |
| /tomb/story     | 墓碑生平       |
| /tomb/checkin   | 墓碑打卡       |
| /open/memorial  | 公开纪念页（扫码访问） |
| /user           | 用户           |
| /role           | 角色           |
| /menu           | 菜单           |
| /file           | 文件           |
| /dashboard      | 仪表盘         |
| /sms             | 短信           |
| /sysOperationLog | 操作日志       |

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

- `code`：业务状态码，200 表示成功
- `message`：提示信息
- `data`：响应数据，可为 null

## 认证

除 `/captcha`、`/open/memorial`、`/login` 等公开接口外，其余接口需在请求头携带登录接口返回的 **token**：

```http
Authorization: <token>
```

请求头名称为 `Authorization`，值为 token 字符串本身（与前端 `axios` 配置一致，**无需** `Bearer ` 前缀）。
