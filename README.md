# MMS 管理系统

一个基于Spring Boot 3.2.4和Spring Cloud的微服务管理系统项目

## 项目简介

MMS（Management System）是一个企业级管理系统，采用微服务架构设计，提供完整的用户权限管理体系。项目采用Spring Cloud微服务架构，使用Nacos作为服务注册中心和配置中心，通过Spring Cloud Gateway实现统一网关路由和JWT+RSA双重鉴权。

项目包含完整的RBAC权限模型，支持用户管理、角色管理、权限控制、数据字典、系统配置等企业级功能。数据库设计包含13个核心表，支持逻辑删除和完整的审计功能。

## 技术栈

### 核心框架
- **Java**: 17
- **Spring Boot**: 3.2.4
- **Spring Cloud**: 2023.0.1
- **Spring Cloud Alibaba**: 2023.0.1.0

### 服务治理
- **Nacos**: 服务注册与发现、配置中心
- **Spring Cloud Gateway**: API网关
- **OpenFeign**: 服务间调用
- **Spring Cloud LoadBalancer**: 负载均衡

### 数据持久化
- **MyBatis Plus**: 3.5.7
- **MySQL**: 8.0.33
- **Redis**: 6.2.14

### 安全认证
- **JWT**: 0.12.5（基于JJWT）
- **BCrypt**: 0.4

### 工具类库
- **Lombok**: 1.18.30
- **Hutool**: 5.8.25

### 文档工具
- **Knife4j**: 4.4.0（API文档）

### 构建工具
- **Maven**: 3.x

## 项目结构

```
MyManagementSystem-Backend/
├── mms-common-bc/                  # 公共模块
│   ├── mms-common-bc-core/         # 核心工具类（异常、响应、上下文等）
│   ├── mms-common-bc-web-mvc/      # Web MVC模块（全局异常处理器、Swagger配置）
│   ├── mms-common-bc-database/     # 数据库配置（MyBatis Plus、Redis）
│   ├── mms-common-bc-security/     # 安全组件（JWT工具类）
│   └── mms-common-bc-all/          # Common模块聚合包
├── mms-gateway-bc/                 # API网关服务
│   └── src/main/java/              # 网关路由、鉴权、过滤器等
├── mms-base-bc/                    # 基础数据服务
│   ├── mms-base-bc-common/         # 公共组件（实体、DTO、VO等）
│   ├── mms-base-bc-controller/     # 控制器层
│   ├── mms-base-bc-feign-api/      # Feign接口
│   ├── mms-base-bc-server/         # 服务启动类
│   └── mms-base-bc-service/        # 业务逻辑层
└── mms-usercenter-bc/              # 用户中心服务
    ├── mms-usercenter-bc-common/   # 公共组件（实体、DTO、VO等）
    ├── mms-usercenter-bc-controller/ # 控制器层
    ├── mms-usercenter-bc-feign-api/  # Feign接口
    ├── mms-usercenter-bc-server/     # 服务启动类
    └── mms-usercenter-bc-service/    # 业务逻辑层
```

## 服务说明

### 网关服务 (mms-gateway-bc)
- **端口**: 5092
- **服务名**: gateway
- **功能**:
  - 统一API入口
  - 路由转发
  - JWT鉴权 + RSA签名验证
  - 链路追踪（TraceId）
  - 请求头透传用户信息

### 用户中心服务 (mms-usercenter-bc)
- **端口**: 5090
- **服务名**: usercenter
- **功能**:
  - 用户注册/登录/登出
  - JWT Token管理（访问令牌+刷新令牌）
  - 用户信息管理（CRUD操作）
  - 角色权限管理（RBAC模型）
  - 用户登录日志记录

### 基础数据服务 (mms-base-bc)
- **端口**: 5091
- **服务名**: base
- **功能**:
  - 数据字典管理
  - 系统配置管理
  - 基础业务数据维护

## 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Nacos**: 2.x（服务注册与配置中心）
- **Redis**: 6.0+（可选，用于缓存和Token黑名单）

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/city-of-star/MyManagementSystem-Backend.git
cd MyManagementSystem-Backend
```

### 2. 数据库初始化

执行数据库初始化脚本：

```bash
mysql -u root -p < mysql/init_mms_dev_core.sql
```

或使用MySQL客户端工具执行 `mysql/init_mms_dev_core.sql` 文件。

### 3. Nacos配置

确保Nacos服务已启动，并在Nacos控制台配置以下配置文件：

- `public-DEV.yaml`    - 公共配置（Spring 公共配置、Swagger 等）
- `mysql-DEV.yaml`     - MySQL 数据源通用配置（用户名/密码为占位符）
- `redis-DEV.yaml`     - Redis 通用配置（地址/密码为占位符）
- `jwt-DEV.yaml`       - JWT 配置（密钥为占位符）
- `gateway-DEV.yaml`   - 网关服务配置（端口、超时、网关私钥为占位符）
- `usercenter-DEV.yaml`- 用户中心服务配置（端口、数据源 URL 为占位符）
- `base-DEV.yaml`      - 基础数据服务配置（端口、数据源 URL 为占位符）
- `secret-DEV.yaml`    - 网关公钥等安全相关通用配置（公钥为占位符）
- `log-DEV.yaml`       - 日志级别与输出路径配置
- `whitelist-DEV.yaml` - 网关与各服务的接口白名单配置

> 说明：仓库中的上述 `*-DEV.yaml` 文件仅作为 **示例模板**，所有数据库账号、密码、JWT 密钥、RSA 密钥等敏感信息均已使用 `YOUR_***` 占位符处理，请在 **Nacos 控制台中创建同名配置并填入真实值**，不要将真实敏感信息写回到代码仓库。

**Nacos连接信息**（请根据自己环境填写）示例：
- 地址: `http://YOUR_NACOS_HOST:8848`
- 用户名: `YOUR_NACOS_USERNAME`
- 密码: `YOUR_NACOS_PASSWORD`

### 4. 编译项目

```bash
mvn clean install
```

### 5. 启动服务

**启动顺序**：
1. 先启动网关服务
2. 再启动业务服务（usercenter、base）

**启动方式**：

```bash
# 方式1: 使用Maven启动
cd mms-gateway-bc && mvn spring-boot:run
cd mms-usercenter-bc/mms-usercenter-bc-server && mvn spring-boot:run
cd mms-base-bc/mms-base-bc-server && mvn spring-boot:run

# 方式2: 使用IDE启动
# 分别运行各服务的启动类
```

### 6. 访问服务

- **网关地址**: http://localhost:5092
- **API文档** (通过网关访问):
  - 用户中心文档: http://localhost:5092/usercenter/doc.html
  - 基础数据文档: http://localhost:5092/base/doc.html

## 开发说明

### 项目特点

- ✅ **Maven多模块管理**: 统一依赖版本，模块职责清晰
- ✅ **微服务架构**: 服务独立部署，支持水平扩展
- ✅ **统一认证**: 网关统一JWT验证 + RSA数字签名，支持Token刷新
- ✅ **安全防护**: Token黑名单机制，防JWT盗用攻击
- ✅ **服务治理**: 基于Nacos实现服务注册与发现、配置中心
- ✅ **多环境配置**: 支持DEV/TEST/PROD环境配置分离
- ✅ **API文档**: 集成Knife4j，自动生成Swagger文档
- ✅ **统一响应**: 全局统一Response格式和异常处理
- ✅ **链路追踪**: 全链路TraceId追踪请求链路
- ✅ **数据库设计**: 完整的RBAC权限模型，支持逻辑删除和审计
- ✅ **开发规范**: 统一的代码分层和命名规范

### 服务间调用

使用OpenFeign进行服务间调用：

```java
@FeignClient(name = "usercenter", path = "/usercenter/api/user")
public interface UserCenterFeignClient {
    @GetMapping("/{id}")
    Response<UserVO> getUserById(@PathVariable Long id);
}
```

### 数据库操作

使用MyBatis Plus进行数据库操作，支持：
- 自动填充创建时间、更新时间
- 逻辑删除
- 分页查询
- 条件构造器

### 安全架构说明

项目采用多层次安全防护体系：

#### JWT + RSA签名双重认证
- **网关层**: 验证JWT Token完整性，使用RSA私钥对用户信息生成数字签名
- **服务层**: 验证网关RSA签名，确保请求来自可信网关
- **优势**: 防止请求头篡改、防止绕过网关直接访问、轻量级高性能验证

#### Token管理机制
- **双Token设计**: 访问令牌(Access Token) + 刷新令牌(Refresh Token)
- **黑名单机制**: 支持Token强制失效，防止JWT盗用
- **自动刷新**: 支持Token自动续期，提升用户体验

### 开发规范

1. **模块命名**: `mms-{module}-bc-{layer}`
   - `bc`: Business Component（业务组件）
   - `layer`: common/controller/service/server/feign-api

2. **代码分层**:
   - `common`: 实体、DTO、VO等
   - `controller`: REST接口层
   - `service`: 业务逻辑层
   - `server`: 启动类与配置
   - `feign-api`: Feign客户端接口

3. **统一响应格式**: 使用 `Response<T>` 封装返回结果

4. **异常处理**: 使用 `BusinessException` 和 `ServerException`，由全局异常处理器统一处理

5. **数据库规范**:
   - 所有表使用逻辑删除(`deleted`字段)
   - 审计字段: `create_by`, `create_time`, `update_by`, `update_time`
   - 索引优化: 主键索引、状态索引、时间索引

## 配置文件说明

项目使用Nacos作为配置中心，各服务的 `application.yml` 仅包含基础配置：
- 服务名
- 环境配置
- Nacos连接信息

具体业务配置（数据库连接、JWT密钥等）需在Nacos配置中心配置。

### 网关签名配置

项目使用RSA数字签名机制，确保微服务间通信的安全性：

1. **生成RSA密钥对**：
   ```bash
   # 编译项目后运行密钥生成工具类
   mvn clean compile
   java -cp target/classes com.mms.common.security.utils.RsaKeyGenerator
   ```

2. **配置网关签名密钥**（在Nacos配置中心）：
   ```yaml
   # public-DEV.yaml 或 gateway-DEV.yaml
   gateway:
     signature:
       # RSA私钥（Base64编码的PKCS#8格式），仅网关持有
       private-key: <生成的私钥>
       # RSA公钥（Base64编码的X.509格式），各下游服务持有
       public-key: <生成的公钥>
       # 签名时间戳有效期（毫秒），默认5分钟，用于防重放攻击
       timestamp-validity: 300000
   ```

3. **安全工作流程**：
   - **请求进入**: 网关验证JWT Token完整性（签名、过期、黑名单检查）
   - **签名生成**: 使用RSA私钥对用户信息和时间戳生成数字签名
   - **信息透传**: 将用户信息和签名通过请求头传递给下游服务
   - **签名验证**: 下游服务使用RSA公钥验证签名，确保请求来自可信网关
   - **权限加载**: 服务层信任透传的用户信息，直接加载用户权限

4. **安全优势**：
   - ✅ **防篡改**: RSA签名确保用户信息不被中间人修改
   - ✅ **防绕过**: 必须通过网关获取有效签名才能访问服务
   - ✅ **轻量级**: 比JWT更轻量的签名验证机制
   - ✅ **高性能**: 非对称加密签名验证性能优秀

## 目录说明

- `mysql/`: 数据库初始化脚本
- `logs/`: 服务运行日志文件（按服务分别存储）
- `nacos/`: Nacos 配置示例（DEV环境，便于本地复现/学习）
- `mms-common-bc/`: 公共组件模块（核心工具类、Web配置、安全组件等）
- `mms-gateway-bc/`: API网关服务（路由、鉴权、签名验证）
- `mms-usercenter-bc/`: 用户中心服务（用户管理、权限控制）
- `mms-base-bc/`: 基础数据服务（数据字典、系统配置）
- `LICENSE`: 项目许可证文件

## 学习建议

这个项目非常适合以下学习场景：

- **微服务架构学习**: Spring Cloud全家桶实战项目
- **安全认证开发**: JWT + RSA签名双重认证机制
- **企业级开发**: 完整的RBAC权限模型和数据库设计
- **代码规范实践**: Maven多模块管理，统一开发规范
- **生产环境部署**: Nacos配置中心，多环境支持

**推荐学习路径**:
1. 先了解项目整体架构和数据库设计
2. 按模块学习：common → gateway → usercenter → base
3. 重点掌握JWT认证和RSA签名机制
4. 实践数据库操作和业务逻辑开发

## 联系方式

- **开发团队**: MMS开发团队
- **邮箱**: 2722562862@qq.com
- **项目主页**: https://github.com/city-of-star/MyManagementSystem-Backend

## 许可证

本项目采用 [MIT License](LICENSE) 许可证，详见LICENSE文件
