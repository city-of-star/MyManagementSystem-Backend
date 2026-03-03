# mms-common-bc-websocket

## 模块目标

提供一套“业务服务可直接复用”的 **WebSocket 基础封装**，包括：

- 握手阶段鉴权（支持 **网关透传头** / **JWT** / **不鉴权**）
- 绑定 `Principal`（拿到 `userId/username`）
- Session 注册表（按 userKey 定向推送、多端多连接管理）
- 发送工具（按 `sessionId` / `userKey` 推送、广播）
- 默认联调 Handler（`ping/pong/echo`）

> 适用场景：Spring Boot 3.x + Spring WebMVC（Servlet）体系的业务服务。

---

## 1. 引入依赖

业务服务的 `pom.xml` 引入：

```xml
<dependency>
  <groupId>com.mms</groupId>
  <artifactId>mms-common-bc-websocket</artifactId>
  <version>${project.version}</version>
</dependency>
```

本模块使用 Spring Boot 3 的自动装配机制（`AutoConfiguration.imports`），**引入即可生效**。

---

## 2. 最小配置（推荐：网关透传模式）

默认模式为 `GATEWAY_HEADERS`（符合你们当前“网关验证 JWT、服务信任网关透传”的安全架构）。

`application.yml` 示例：

```yaml
websocket:
  enabled: true
  auto-register: true
  endpoint-path: /ws
  allowed-origins:
    - "*"
  auth:
    mode: GATEWAY_HEADERS
```

握手时需要网关透传以下请求头（默认值，可配置）：

- `X-User-Id`
- `X-User-Name`

---

## 3. JWT 直连模式（不经过网关或网关不透传身份时使用）

```yaml
websocket:
  auth:
    mode: JWT
    token-param: token
    authorization-header: Authorization
    authorization-prefix: "Bearer "
    expected-token-type: ACCESS
```

客户端握手携带方式（二选一）：

- `Authorization: Bearer <access_token>`
- `ws://host/ws?token=<access_token>`

> 注意：企业生产更常见的是 **握手只做一次校验**，后续长连接期间如果 token 过期/权限变化，需要额外策略（见下方演进清单）。

---

## 4. 自定义业务 Handler（推荐）

默认提供 `mmsWebSocketHandler`（联调用）。业务侧建议覆盖：

```java
@Bean(name = "mmsWebSocketHandler")
public WebSocketHandler myHandler(...) {
  return new MyBusinessWebSocketHandler(...);
}
```

只要 Bean 名称还是 `mmsWebSocketHandler`，自动注册的 endpoint 会自动指向你的 handler。

---

## 5. 服务端推送（按用户 / 广播）

业务服务注入 `MmsWebSocketSender`：

- `sendTextToUser(userKey, text)`：给某个用户推送（多端登录则多个连接都会收到）
- `broadcastText(text)`：广播
- `sendTextToSession(sessionId, text)`：给某个连接推送

`userKey` 默认优先使用 `userId`，其次 `username`（由握手鉴权阶段写入）。

---

## 6. 默认联调行为

默认 handler 行为：

- 收到 `"ping"` 返回 `"pong"`
- 收到 `{"type":"ping"}` 返回 `{"type":"pong"}`
- 其他文本消息：原样 `echo` 回去

---

## 7. 常见企业级落地建议（非常重要）

### 7.1 单机/单实例

- **握手鉴权**：网关透传模式最稳（下游避免重复验证 JWT、避免 Redis 阻塞验证）
- **连接管理**：维护 session registry；连接断开及时清理；必要时限制每用户连接数
- **心跳**：应用层 ping/pong（或 WebSocket 原生 ping/pong）+ 空闲超时踢下线
- **限流**：按 userKey / ip 做消息频率限制，避免被刷爆

### 7.2 多实例/集群（大厂常态）

WebSocket 的核心难点是 **连接是有状态的**。常见方案：

- **Sticky Session**：网关/负载均衡按用户固定落到同一台实例（实现简单，但弹性/故障切换受影响）
- **Connection 只在边缘层**：把 WS 连接放到“专用实时网关/边缘服务”，后端业务通过 MQ/HTTP 推送（很多大厂这样做）
- **跨节点路由**：每个节点维护本地连接；全局推送通过 Redis PubSub / Kafka topic 通知到目标节点，再由节点本地发送
- **STOMP + Broker**：使用 Spring `spring-messaging` + STOMP，后端走 Broker relay（RabbitMQ/ActiveMQ）做路由（开发体验好，但体系更重）

---

## 8. 你接下来最值得封装的点（下一节也有 Roadmap）

- 统一协议：`messageId/type/timestamp/data`，支持 ack、重试、幂等
- 统一错误码/断开码（close code）与握手失败返回体
- 连接鉴权续期（token refresh / 重新鉴权）
- 权限模型：按“频道/主题/房间”授权，支持动态变更
- 集群推送：Redis/Kafka 路由、在线状态服务、用户-节点映射
- 可观测性：连接数、入/出消息、失败率、延迟、慢消费者；Prometheus 指标

