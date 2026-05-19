# MMS 管理系统后端（MyManagementSystem-Backend）

基于 **Spring Boot 3.2.4**、**Spring Cloud 2023.0.1**、**Spring Cloud Alibaba 2023.0.1.0** 的微服务管理系统后端。提供网关统一入口、JWT + RSA 签名的多层鉴权、用户中心 RBAC、基础数据、定时作业、附件与本地文件存储、WebSocket、审计日志等能力，并预留消息队列（RocketMQ）等扩展模块。

## 项目简介

MMS（Management System）采用 **Maven 多模块 + 微服务** 架构：

- **Nacos**：服务注册发现与配置中心（多环境 `DEV` / `TEST` / `PROD`）
- **Spring Cloud Gateway**：统一 API 入口、限流、熔断降级、链路 TraceId
- **OpenFeign**：服务间调用
- **MyBatis Plus + MySQL**：持久化，逻辑删除与审计字段
- **Redis**：缓存、Token 黑名单、网关限流等

业务侧覆盖用户与权限、组织（部门/岗位）、数据字典与系统配置、附件上传与流式访问、定时任务与执行记录、登录/操作/异常/API 访问审计、在线用户、用户偏好、WebSocket 推送等。

## 技术栈

### 核心框架

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.4 |
| Spring Cloud | 2023.0.1 |
| Spring Cloud Alibaba | 2023.0.1.0 |

### 服务治理

- **Nacos**：注册中心、配置中心
- **Spring Cloud Gateway**：路由、JWT 鉴权、RSA 签名透传、Redis 限流、Resilience4j 熔断
- **OpenFeign** + **Spring Cloud LoadBalancer**

### 数据与中间件

- **MyBatis Plus** 3.5.7
- **MySQL** 8.0.33
- **Redis** 6.x（缓存、黑名单、网关限流）
- **RocketMQ Spring** 2.3.5（`mms-common-bc-mq-rocket`，封装进行中；Broker 建议自建 **5.3.x**）

### 安全认证

- **JWT**（JJWT 0.12.5）：访问令牌 + 刷新令牌（Cookie）
- **BCrypt** 0.4：密码加密
- **RSA**：网关私钥签名、下游公钥验签

### 工具与文档

- **Lombok** 1.18.30、**Hutool** 5.3.1
- **EasyExcel** 3.3.2（`mms-common-bc-document` 导出）
- **Knife4j** 4.4.0（OpenAPI 3 / SpringDoc）

### 构建

- **Maven** 3.6+

## 项目结构

```
MyManagementSystem-Backend/
├── mms-common-bc/                      # 公共能力（多子模块）
│   ├── mms-common-bc-core/            # 响应、异常、上下文、工具类
│   ├── mms-common-bc-webmvc/          # Web 通用能力、UserContextUtils、文件下载
│   ├── mms-common-bc-datasource/       # 数据源、MyBatis Plus
│   ├── mms-common-bc-cache/           # Redis 缓存封装（单模块）
│   ├── mms-common-bc-security/        # 安全（聚合）
│   │   ├── mms-common-bc-security-core/    # JWT、RSA、权限注解与切面
│   │   └── mms-common-bc-security-servlet/ # Servlet 过滤器、网关验签
│   ├── mms-common-bc-threadpool/      # 统一线程池
│   ├── mms-common-bc-job/             # 任务执行通用约定、JobExecuteController
│   ├── mms-common-bc-websocket/       # WebSocket 鉴权、会话、推送
│   ├── mms-common-bc-document/        # EasyExcel 导出
│   ├── mms-common-bc-es/              # Elasticsearch（预留，待实现）
│   └── mms-common-bc-mq/              # 消息队列（聚合）
│       ├── mms-common-bc-mq-api/      # 契约层（预留）
│       ├── mms-common-bc-mq-rocket/   # RocketMQ 实现（当前开发）
│       └── mms-common-bc-mq-kafka/    # Kafka（预留）
├── mms-gateway-bc/                     # API 网关
├── mms-usercenter-bc/                  # 用户中心
│   ├── mms-usercenter-bc-common/
│   ├── mms-usercenter-bc-controller/
│   ├── mms-usercenter-bc-feign-api/
│   ├── mms-usercenter-bc-server/
│   └── mms-usercenter-bc-service/
├── mms-base-bc/                        # 基础数据
│   ├── mms-base-bc-common/
│   ├── mms-base-bc-controller/
│   ├── mms-base-bc-feign-api/
│   ├── mms-base-bc-server/
│   └── mms-base-bc-service/
├── mms-job-bc/                         # 定时作业
│   ├── mms-job-bc-common/
│   ├── mms-job-bc-core/                # 任务定义、调度、执行记录 API
│   └── mms-job-bc-server/
├── mysql/                              # 库表初始化与增量脚本
├── nacos/                              # Nacos 配置示例（DEV）
├── script/                             # 发布、重启、日志查看脚本
├── prompt/                             # 项目 AI/开发知识库（索引与架构约定）
└── pom.xml                             # 父 POM、依赖版本管理
```

## 服务说明

| 服务 | 模块 | 默认端口 | 说明 |
|------|------|----------|------|
| gateway | `mms-gateway-bc` | 5092 | 统一入口、鉴权、限流、熔断、WS 代理 |
| usercenter | `mms-usercenter-bc` | 5090 | 认证、用户、角色权限、组织、审计、在线用户、WS |
| base | `mms-base-bc` | 5091 | 字典、系统配置、附件与本地文件存储 |
| job | `mms-job-bc` | 5093 | 定时任务定义、调度、执行记录 |

### 网关（mms-gateway-bc）

- HTTP 路由：`/api/{service}/**` → 对应微服务（`StripPrefix=2`）
- WebSocket：`/ws/{service}/**` → `lb:ws://{service}`
- JWT 校验、Token 黑名单、RSA 签名与请求头透传（`userId`、`username`、`tokenJti` 等）
- Redis **RequestRateLimiter**、**Resilience4j** 熔断与 `GatewayFallbackController` 降级
- Actuator：`health`、`metrics`、`prometheus`、`gateway` 等

### 用户中心（mms-usercenter-bc）

| 能力 | 控制器（包路径摘要） |
|------|----------------------|
| 登录注册、Token 刷新 | `auth.AuthController` |
| 用户 CRUD、资料 | `auth.UserController` |
| 用户偏好 | `auth.UserPreferenceController` |
| 角色、权限 | `auth.RoleController`、`auth.PermissionController` |
| 部门、岗位 | `org.DeptController`、`org.PostController` |
| 用户权限查询 | `security.UserAuthorityController` |
| 在线用户 | `security.OnlineUserController` |
| 登录日志 | `audit.UserLoginLogController` |
| WebSocket 示例/推送 | `websocket.WebsocketController` |

登录安全（Nacos）：失败次数限制、锁定时长、默认密码前缀等。

### 基础数据（mms-base-bc）

| 能力 | 说明 |
|------|------|
| 数据字典 | 字典类型、字典数据 CRUD、按类型编码查询启用项 |
| 系统配置 | 配置项 CRUD、按 key 查询、唯一性校验 |
| 附件管理 | 元数据 CRUD、上传、软删/硬删、批量操作 |
| 文件存储 | `FileServiceImpl` 本地磁盘存储；流式访问 `/attachment/stream/**` |

文件相关配置见 `nacos/base-DEV.yaml`（`file.upload.*`、Multipart 大小限制）。

### 作业服务（mms-job-bc）

| 能力 | 说明 |
|------|------|
| 任务定义 | `JobController`：CRUD、启停、立即执行 |
| 执行记录 | `JobRunLogController`：分页、删除（导出/重试/终止接口预留） |
| 调度 | `JobScheduler`：`@Scheduled` 扫描 + `CronExpression` 计算下次触发 |

## 数据库

### 初始化

```bash
mysql -u root -p < mysql/init_mms_dev_core.sql
```

开发库名示例：`mms_dev_core`（以 Nacos 中 `spring.datasource.url` 为准）。

### 核心表（约 21 张）

| 分类 | 表名 |
|------|------|
| 权限与用户 | `system_user`、`system_role`、`system_permission`、`system_user_role`、`system_role_permission`、`system_user_preference` |
| 组织 | `system_dept`、`system_post`、`system_user_dept`、`system_user_post` |
| 基础数据 | `system_config`、`system_dict_type`、`system_dict_data`、`system_attachment` |
| 作业 | `job_def`、`job_run_log`、`job_lock` |
| 审计 | `audit_user_login_log`、`audit_operation_log`、`audit_exception_log`、`audit_api_access_log` |
| 安全 | `security_online_user` |

### 增量脚本

`mysql/prod/` 下为结构变更脚本（如用户头像字段、用户偏好表等），按版本在目标环境执行；全量初始化仍以 `init_mms_dev_core.sql` / `init_mms_prod_core.sql` 为主。

## 环境要求

- **JDK** 17+
- **Maven** 3.6+
- **MySQL** 8.0+
- **Nacos** 2.x
- **Redis** 6.0+（缓存、黑名单、网关限流）
- **RocketMQ** 5.3.x（可选；使用 MQ 能力时需自建 NameServer + Broker）

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/city-of-star/MyManagementSystem-Backend.git
cd MyManagementSystem-Backend
```

### 2. 数据库初始化

执行 `mysql/init_mms_dev_core.sql`，并按需执行 `mysql/prod/` 下增量脚本。

### 3. Nacos 配置

在 Nacos 控制台创建配置（**namespace** = `spring.profiles.active`，如 `DEV`；**group** = `DEFAULT_GROUP`；**dataId** = `{name}-{profile}.yaml`）。

仓库 `nacos/` 目录为 **DEV 示例模板**，敏感信息使用 `YOUR_***` 占位，**勿将真实密码、密钥提交到 Git**。

| 配置文件 | 用途 |
|----------|------|
| `public-DEV.yaml` | Jackson、MyBatis Plus、Swagger 网关地址等 |
| `mysql-DEV.yaml` | 数据源账号（占位） |
| `redis-DEV.yaml` | Redis |
| `jwt-DEV.yaml` | JWT 密钥（占位） |
| `secret-DEV.yaml` | 网关 RSA 公钥等 |
| `threadpool-DEV.yaml` | 线程池 |
| `websocket-DEV.yaml` | WebSocket |
| `whitelist-DEV.yaml` | 网关/服务白名单 |
| `log-DEV.yaml` | 日志 |
| `gateway-DEV.yaml` | 网关端口、路由、限流、熔断 |
| `usercenter-DEV.yaml` | 用户中心端口、库 URL、登录策略 |
| `base-DEV.yaml` | 基础服务、文件上传 |
| `job-DEV.yaml` | 作业服务 |
| `mq-DEV.yaml` | RocketMQ（使用 MQ 时在 Nacos 新建，仓库暂无示例） |

各服务 `application.yml` 通过 `spring.config.import` 按环境加载上述配置（网关与用户中心/基础/作业的 import 列表略有差异，以各模块 `application.yml` 为准）。

**本地覆盖示例**：

```bash
java -jar app.jar \
  --spring.profiles.active=DEV \
  --spring.cloud.nacos.server-addr=127.0.0.1:8848 \
  --spring.cloud.nacos.username=nacos \
  --spring.cloud.nacos.password=nacos
```

### 4. 编译

```bash
mvn clean install -DskipTests
```

### 5. 启动顺序（推荐）

1. MySQL、Nacos、Redis（按需 RocketMQ）
2. 业务服务：`usercenter` → `base` → `job`
3. 最后启动 `gateway`

```bash
# Maven 启动示例
cd mms-usercenter-bc/mms-usercenter-bc-server && mvn spring-boot:run
cd mms-base-bc/mms-base-bc-server && mvn spring-boot:run
cd mms-job-bc/mms-job-bc-server && mvn spring-boot:run
cd mms-gateway-bc && mvn spring-boot:run
```

或在 IDE 中运行各 `*Application` 启动类。

### 6. 访问

| 入口 | 地址 |
|------|------|
| 网关 | http://localhost:5092 |
| API 前缀 | `/api/usercenter/**`、`/api/base/**`、`/api/job/**` |
| WebSocket | `ws://localhost:5092/ws/{service}/...` |
| Knife4j（经网关） | http://localhost:5092/api/usercenter/doc.html |
| | http://localhost:5092/api/base/doc.html |
| | http://localhost:5092/api/job/doc.html |

Swagger 文档地址以各服务 Nacos 中 `springdoc.swagger-ui.url` 及网关 `swagger.gateway-url` 为准。

## 公共模块能力摘要

| 模块 | 能力 |
|------|------|
| `mms-common-bc-core` | `Response`、`BusinessException`、错误码、TraceId、工具类 |
| `mms-common-bc-webmvc` | 全局 Web 能力、`UserContextUtils`、`FileDownloadService` |
| `mms-common-bc-security-*` | JWT、网关签名校验、`@RequiresPermission`、认证过滤器 |
| `mms-common-bc-cache` | Redis 封装 |
| `mms-common-bc-datasource` | MyBatis Plus 自动配置 |
| `mms-common-bc-threadpool` | 线程池属性与自动装配 |
| `mms-common-bc-job` | 任务执行约定、`JobExecuteController` |
| `mms-common-bc-websocket` | 握手鉴权、会话注册、消息推送 |
| `mms-common-bc-document` | `ExcelExportService` |
| `mms-common-bc-mq-rocket` | RocketMQ Spring 集成（封装进行中） |

业务 BC 父 POM 当前依赖 **`mms-common-bc-mq-rocket`**（传递依赖 `mq-api`）。Kafka 模块为预留，未接入业务。

更细的能力清单与代码入口见仓库内 **`prompt/`** 目录（面向开发与 AI 辅助维护的项目文档）。

## 安全架构

### JWT + RSA 网关签名

1. 网关校验 JWT（签名、过期、黑名单）
2. 网关使用 **RSA 私钥** 对用户信息 + 时间戳签名，写入请求头
3. 下游使用 **RSA 公钥** 验签，信任透传身份并加载权限
4. `SecurityContext` 仅存最小认证信息；完整用户资料通过 Feign 查询 usercenter

### Token

- 访问令牌 + 刷新令牌（刷新令牌可走 HttpOnly Cookie）
- Redis 黑名单支持强制失效

### 密钥生成

```bash
mvn clean compile -pl mms-common-bc/mms-common-bc-security/mms-common-bc-security-core
# 运行 RsaKeyGenerator（具体类路径以 security-core 模块为准）
```

将生成的公私钥分别配置到 Nacos（网关私钥、下游 `secret-*.yaml` 公钥）。

## 开发说明

### 模块命名

- 业务组件：`mms-{domain}-bc`
- 分层：`common` / `controller` / `service` / `server` / `feign-api`
- 公共能力：`mms-common-bc-{能力}`

### 分层职责

- **controller**：参数校验、`Response<T>` 返回
- **service**：业务与事务（`@Transactional(rollbackFor = Exception.class)`）
- **mapper**：持久化

### 服务间调用

```java
@FeignClient(name = "usercenter", path = "/usercenter/api/user")
public interface UserCenterFeignClient {
    @GetMapping("/{id}")
    Response<UserVO> getUserById(@PathVariable Long id);
}
```

实际 `path` 需与各服务 `context-path`、网关路由一致。

### 消息队列（进行中）

- 应用依赖：`rocketmq-spring-boot-starter` **2.3.5**（父 POM 统一管理）
- 建议自建 Broker：**RocketMQ 5.3.x** 二进制包
- 配置：在 Nacos 增加 `mq-{profile}.yaml`，并在业务服务 `spring.config.import` 中引用（usercenter 等已预留 import）

## 目录说明

| 目录 | 说明 |
|------|------|
| `mysql/` | 全量初始化与 `prod/` 增量脚本 |
| `nacos/` | DEV 配置模板 |
| `script/` | 前后端发布、版本切换、服务重启、日志查看 |
| `prompt/` | 架构、鉴权、能力清单、任务入口索引（维护用） |
| `logs/` | 运行日志（按服务，本地运行时生成） |

## 学习建议

1. 阅读 `prompt/索引.md` → `架构/项目总览.md` → `安全/鉴权链路.md`
2. 从 `mms-common-bc-core`、网关过滤器、usercenter 登录链路入手
3. 再阅读 base 附件/字典、job 调度实现

## 联系方式

- **开发团队**：MMS 开发团队
- **邮箱**：2722562862@qq.com
- **项目主页**：https://github.com/city-of-star/MyManagementSystem-Backend

## 许可证

本项目采用 [MIT License](LICENSE)。
