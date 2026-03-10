# `mms-common-bc-cache` 演进路线图（循序渐进）

## 当前阶段（你现在已有的能力）

当前模块处于 **“最小可用（MVP）”** 状态，核心目标是：让业务方能快速、统一地使用 Redis。

- **Redis 基础配置**：`com.mms.common.cache.config.RedisConfig`
  - Key 序列化：`StringRedisSerializer`
  - Value 序列化：`GenericJackson2JsonRedisSerializer`（JSON，企业级常见做法）
- **Key 前缀常量**：`com.mms.common.cache.constants.CacheKeyPrefixConstants`
- **常用操作工具**：`com.mms.common.cache.utils.RedisUtils`
  - `set/get/delete/exists/expire`
  - `increment/decrement`
  - `hSet/hGet/hDelete/hExists/...`（Hash）

> 这套组合适合你当前“先跑起来、先统一用法”的阶段。

---

## 演进目标（最终你希望得到什么）

一个成熟的缓存组件一般会逐步具备：

- **统一规范**：key 命名、TTL 策略、序列化策略
- **开箱即用**：以 common 组件形态被业务模块引入即可生效（自动装配）
- **声明式缓存**：用注解完成缓存读写/失效（减少样板代码）
- **治理能力**：缓存穿透/击穿/雪崩处理、热点 key、监控告警
- **扩展能力**：分布式锁、多级缓存（Caffeine + Redis）、可插拔实现

---

## 推荐的“分步完善”路线（每一步都可独立交付）

### 第 1 步：把“约定”定下来（规范先行）

**目标**：让所有使用者用同一种方式拼 key、同一种 TTL 规则，避免后期混乱。

- **建议新增**
  - `CacheKeyPrefix` 继续保留（你已有）
  - 新增一个简单的 `CacheKeys`（或 `CacheKeyBuilder`）：
    - 统一 key 拼接方式：`prefix + bizId` / `prefix + ":" + bizId`
    - 统一“业务维度”的 key（例如：`USER_DETAIL`, `USER_PERMISSION`）
  - 新增 `CacheTtl` 常量（或枚举）：
    - 例如：5 分钟 / 30 分钟 / 1 小时 / 1 天（带统一单位）

- **验收标准**
  - 新代码里不再出现大量散落的 `"mms:xxx:xxx"` 字符串硬编码
  - 每个缓存点都明确写出 TTL 的选择理由（短/中/长）

> 这一步不涉及任何框架改造，只是让“用法可控”。

---

### 第 2 步：让组件“自动生效”（Spring Boot 自动装配）

**目标**：像 `mms-common-bc-security`、`mms-common-bc-threadpool` 一样，引入依赖即可自动生效。

- **你仓库里已有参考模式**
  - `mms-common-bc-security/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  - `mms-common-bc-threadpool/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

- **建议新增**
  - `com.mms.common.cache.config.CacheAutoConfiguration`（或 `RedisAutoConfiguration`）
    - `@ConditionalOnClass(RedisTemplate.class)`：有 Redis 依赖才装配
    - `@ConditionalOnMissingBean`：避免业务方自定义 Bean 冲突
  - `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
    - 写入你的自动装配类全限定名

- **验收标准**
  - 业务服务只要依赖 `mms-common-bc-cache`，不需要显式 `@Import` 也能拿到 `RedisTemplate`
  - 不影响业务方自定义 `RedisTemplate/CacheManager` 的能力

> 这一步完成后，`mms-common-bc-cache` 就具备“企业 common 组件”的基本形态。

---

### 第 3 步：引入“注解缓存”（Spring Cache，最小可用版）

**目标**：让业务方写缓存不再手动 `RedisUtils.set/get`，而是用注解声明式完成。

- **依赖**
  - 增加 `spring-boot-starter-cache`

- **建议新增/配置**
  - 业务服务（或统一的 starter）开启：`@EnableCaching`
  - 提供 `RedisCacheManager`（或 Spring Boot 默认）：
    - 默认 TTL（例如 1 小时）
    - 允许按 cacheName 配置不同 TTL（例如 user 10 分钟、dict 24 小时）
  - 统一 key 规则：
    - `KeyGenerator`（可选，但建议做）
    - 或使用 `key = "#id"`、`key = "'prefix:' + #id"` 的 SpEL 约定

- **业务方用法示例**
  - `@Cacheable`：读缓存（查到直接返回，未命中才执行方法并写缓存）
  - `@CacheEvict`：写后删缓存（更新/删除时使用）
  - `@CachePut`：强制刷新缓存（不常用，但有场景）

- **验收标准**
  - 选一个“读多写少”的接口（例如用户详情）完成注解改造
  - 更新用户时能正确失效缓存

> 这一步就是你问的“大厂常用注解缓存”，核心是 **AOP + CacheManager**。

---

### 第 4 步：缓存稳定性（穿透/击穿/雪崩的最小防护）

**目标**：上了缓存后不只“能用”，还要“抗压、可控”。

- **缓存穿透（查不到的数据反复打 DB）**
  - 缓存空值（短 TTL，如 1~5 分钟）
  - 或引入布隆过滤器（后期可选）

- **缓存击穿（热点 key 过期瞬间大量并发）**
  - 互斥锁（分布式锁/本地锁）
  - 或“逻辑过期 + 后台刷新”（进阶）

- **缓存雪崩（大量 key 同时过期）**
  - TTL 增加随机抖动（例如 $ttl + random(0, 60s)$）

- **验收标准**
  - 对 1~2 个热点接口加上“击穿保护”
  - 线上问题定位时能快速识别是“缓存问题”还是“业务问题”（日志/埋点）

---

### 第 5 步：分布式锁（可选，但常见）

**目标**：把“锁”从业务代码里收口到缓存组件里，避免乱写 Lua/乱用 setnx。

两条路线：

- **路线 A（推荐）**：引入 Redisson（成熟、功能全）
  - `RLock` / `RReadWriteLock` / `RSemaphore` 等
- **路线 B（轻量）**：基于 Redis `SET key value NX EX` + Lua 释放锁
  - 需要非常谨慎处理“误删锁”“锁续期”等问题

**验收标准**
- 选一个需要幂等/互斥的场景（如定时任务、重复提交）接入

---

### 第 6 步：多级缓存（大厂常见，按需上）

**目标**：提升读性能、降低 Redis 压力。

- L1：本地缓存（Caffeine）
- L2：分布式缓存（Redis）

