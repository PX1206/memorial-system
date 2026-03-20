# API 文档说明

## Knife4j 在线文档

启动 `memorial-service` 后，访问：

- **地址**：http://localhost:2168/doc.html
- **默认账号**：px / px123456（在 `application.yml` 的 `knife4j.basic` 中配置）

Knife4j 提供完整的接口列表、参数说明、在线调试功能。

## 主要 API 概览

| 路径前缀 | 功能 |
|----------|------|
| /captcha | 验证码 |
| /area | 地区 |
| /family | 家族 |
| /family/member | 家族成员 |
| /tomb | 墓碑 |
| /tomb/message | 墓碑留言 |
| /tomb/story | 墓碑生平 |
| /tomb/checkin | 墓碑打卡 |
| /open/memorial | 公开纪念页（扫码访问） |
| /user | 用户 |
| /role | 角色 |
| /menu | 菜单 |
| /file | 文件 |
| /dashboard | 仪表盘 |
| /sms | 短信 |
| /sysOperationLog | 操作日志 |

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

除 `/captcha`、`/open/memorial`、`/login` 等公开接口外，其余接口需在请求头携带 Token：

```
Authorization: Bearer <token>
```

或根据项目实际约定的认证方式传递。
