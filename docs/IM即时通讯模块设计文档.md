# IM 即时通讯模块设计文档

> 适用工程：SpringBootServiceSeedProject（seed-im 模块） + AndroidSeedProject（shared KMP 模块）

## 一、架构概览

```
┌──────────────┐   WebSocket(ws://host:8066/ws)   ┌──────────────────┐
│  KMP 客户端   │ ◄──────────────────────────────► │  Spring Boot 服务端 │
│  (Android/iOS)│   REST(HTTP /chat/*)            │  (seed-im 模块)    │
└──────────────┘ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄► └──────────────────┘
                                                     │
                                          ┌──────────┴──────────┐
                                          │  MySQL (seed 库)     │
                                          │  + Redis (在线状态)  │
                                          └─────────────────────┘
```

- **实时通信**：原生 WebSocket（非 STOMP），服务端 `ChatWebSocketHandler`，客户端 Ktor WebSocket
- **鉴权**：WebSocket 握手时 token 通过 URL query 参数传递（`ws://host/ws?token=xxx`），服务端 `WebSocketAuthInterceptor` 复用若依标准 `TokenService` 解析
- **REST 接口**：走标准 JWT（Authorization header），统一返回 `Result`（code/msg/total/data）

---

## 二、数据库表结构（v2 主流设计）

### 设计原则

| 原则 | 说明 |
|---|---|
| **会话唯一** | 一条会话一条记录，用 `min_user_id + max_user_id` 保证唯一性（小 ID 在前，大 ID 在后） |
| **消息挂唯一会话** | `im_message.conversation_id` 指向唯一会话，历史查询按单字段过滤 |
| **未读数双字段** | 会话表上 `unread_count_a` / `unread_count_b` 分别维护两个参与者的未读数 |
| **群聊可扩展** | `type` 字段预留群聊（当前固定 1=单聊），群聊扩展时引入 `im_conversation_member` 表 |

### im_conversation（会话表）

| 字段 | 类型 | 说明 |
|---|---|---|
| conversation_id | BIGINT PK | 会话ID（全局唯一，自增） |
| type | TINYINT | 会话类型（1单聊 2群聊-预留） |
| min_user_id | BIGINT | 参与者A（userId 较小者） |
| max_user_id | BIGINT | 参与者B（userId 较大者） |
| unread_count_a | INT | 参与者A的未读消息数 |
| unread_count_b | INT | 参与者B的未读消息数 |
| last_msg_id | BIGINT | 最后一条消息ID |
| last_msg_content | VARCHAR(1000) | 最后一条消息内容摘要 |
| last_msg_time | DATETIME | 最后一条消息时间 |
| last_msg_sender | BIGINT | 最后一条消息发送者ID |
| status / del_flag / 审计字段 | | 同若依约定 |

**索引**：
- `UNIQUE KEY uk_min_max (min_user_id, max_user_id)` — 保证单聊会话唯一
- `KEY idx_min_user / idx_max_user` — 会话列表查询

### im_message（消息表）

| 字段 | 类型 | 说明 |
|---|---|---|
| msg_id | BIGINT PK | 消息ID（全局唯一，自增，兼作游标排序） |
| conversation_id | BIGINT | 会话ID（指向唯一会话，不分方向） |
| sender_id | BIGINT | 发送者用户ID |
| receiver_id | BIGINT | 接收者用户ID |
| msg_type | TINYINT | 消息类型（1文本 2图片） |
| content | TEXT | 消息内容（文本 / 图片URL） |
| send_time | DATETIME | 发送时间 |
| status | TINYINT | 消息状态（1已发送 2已送达 3已读 4已撤回） |
| del_flag / 审计字段 | | 同若依约定 |

**索引**：
- `KEY idx_conv_time (conversation_id, send_time)` — 历史消息分页查询

---

## 三、服务端接口

### WebSocket 协议

**连接**：`ws://host:8066/ws?token=<标准JWT>`

**客户端→服务端**：

```jsonc
// 发送消息
{"type":"chat","receiverId":2,"msgType":1,"content":"你好","clientMsgId":"唯一ID"}

// 心跳
{"type":"ping"}
```

**服务端→客户端**：

```jsonc
// 新消息推送（发给发送方和接收方）
{"type":"chat","msgId":1,"conversationId":1,"senderId":1,"receiverId":2,"msgType":1,"content":"你好","sendTime":"2026-07-23 12:00:00","clientMsgId":"唯一ID"}

// 投递确认（回发送方）
{"type":"ack","clientMsgId":"唯一ID","msgId":1,"success":true}

// 心跳响应
{"type":"pong"}
```

### REST 接口（/chat/*）

| 方法 | 路径 | 说明 | 关键参数 |
|---|---|---|---|
| POST | /chat/conversation | 获取/创建会话 | body: `{"targetId": 2}` |
| GET | /chat/conversations | 会话列表（含对方昵称/头像/未读数） | — |
| GET | /chat/history | 历史消息分页 | conversationId, lastMsgId?, size=20 |
| POST | /chat/read | 标记已读 | body: `{"conversationId": 1}` |
| GET | /chat/unread/count | 未读总数 | — |

---

## 四、KMP 客户端架构

```
shared/src/commonMain/kotlin/com/demo/shared/
├── model/
│   ├── ImChat.kt         # ImMessage, ImConversation, MsgType, UnreadCount
│   └── WsFrame.kt        # WsInbound, WsChatOutbound, WsPingOutbound
├── network/
│   ├── Api.kt            # IM REST 方法
│   ├── ChatSocketClient.kt  # WebSocket 客户端（单例，SharedFlow 消息流）
│   └── AuthHeadersPlugin.kt # 动态注入 Authorization header
├── repository/
│   └── ChatRepository.kt # 组合 Api + ChatSocketClient
└── usecase/
    └── ChatUseCase.kt    # sealed Result 封装

mod_main/src/main/java/com/demo/main/ui/im/
├── ImLoginActivity.kt       # 登录页
├── ImConversationActivity.kt # 会话列表页
├── ImChatActivity.kt        # 聊天页
├── adapter/                 # RecyclerView Adapter
└── viewmodel/               # ViewModel
```

### 关键设计

- **ChatSocketClient 单例**：所有 Repository/UseCase 共享同一个 WebSocket 连接（`ChatSocketClient.instance`）
- **独立 HttpClient**：WS 用独立 HttpClient（只装 WebSockets 插件，不装 HttpTimeout/Retry，避免杀长连接）
- **消息去重**：收到 WS 推送时按 msgId 去重，避免发送方重复显示
- **心跳保活**：每 30s 发 ping
- **ack 等待**：发送消息后等 5s ack 超时

---

## 五、环境配置

### 服务端

| 配置 | 值 |
|---|---|
| 端口 | 8066 |
| WebSocket 端点 | /ws（SecurityConfig 已放行） |
| 数据库 | MySQL seed 库（im_conversation + im_message 表） |

### KMP 客户端

环境地址在 `shared/src/{androidMain,iosMain}/.../constant/ServerConfig.kt` 配置：

| 环境 | HTTP | WebSocket |
|---|---|---|
| 开发（DEV） | `http://192.168.213.145:8066` | `ws://192.168.213.145:8066/ws` |
| 生产（PROD） | `https://106.15.7.132:8443` | `wss://106.15.7.132:8443/ws` |

切换方式：Android 改 BuildConfig（debug/release），iOS 改 Build Configuration。

> **生产环境 TLS**：SpringBoot 只跑 HTTP 8066，TLS 由 Nginx 在 8443 端口终止。
> 客户端内置自签名证书 pinning（`server_cert.pem`，CN=`106.15.7.132`）。
> Nginx 需配置 WebSocket 代理头（详见 `阿里云IP自签名HTTPS配置指南.md` 第七章）。

---

## 六、建表与部署

```bash
# 1. 执行建表 SQL
mysql -u seed -p seed < sql/im_chat.sql

# 2. 启动服务端（IDEA 跑 SeedApplication，默认 druid-prod profile）

# 3. 验证 WebSocket
wscat -c "ws://localhost:8066/ws?token=<JWT>"

# 4. 客户端测试账号
# admin / admin123 (userId=1)
# ry / admin123 (userId=2)
```

---

## 七、二期扩展规划

- [ ] 群聊：引入 `im_conversation_member` 表，type=2
- [ ] SQLDelight 本地缓存（离线消息）
- [ ] wss/TLS（生产环境）
- [ ] 图片消息（复用 FileUploadController 上传）
- [ ] 消息撤回 / 已读回执
