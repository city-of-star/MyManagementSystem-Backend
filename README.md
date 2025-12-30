# MMS 管理系统

一个基于Spring Boot 3.2.4和Spring Cloud的微服务管理系统项目

## 项目简介

MMS（Management System）是一个企业级管理系统，采用微服务架构设计，提供用户管理、基础数据管理等功能模块。项目采用Spring Cloud微服务架构，使用Nacos作为服务注册中心和配置中心，通过Spring Cloud Gateway实现统一网关路由和鉴权。

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
- **Redis**: 缓存支持

### 安全认证
- **JWT**: 0.12.5（基于JJWT）
- **BCrypt**: 密码加密

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
- **功能**: 
  - 统一API入口
  - 路由转发
  - JWT鉴权
  - 链路追踪（TraceId）

### 用户中心服务 (mms-usercenter-bc)
- **服务名**: usercenter
- **功能**:
  - 用户注册/登录
  - JWT Token管理
  - 用户信息管理
  - 角色权限管理

### 基础数据服务 (mms-base-bc)
- **服务名**: base
- **功能**:
  - 基础数据管理
  - 业务数据维护

## 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Nacos**: 2.x（服务注册与配置中心）
- **Redis**: 6.0+（可选，用于缓存和Token黑名单）

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/city-of-star/MyManagementSystem.git
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

- `public-DEV.yaml` - 公共配置（数据库连接等）
- `jwt-DEV.yaml` - JWT配置
- `gateway-DEV.yaml` - 网关服务配置
- `usercenter-DEV.yaml` - 用户中心服务配置
- `base-DEV.yaml` - 基础数据服务配置

**Nacos连接信息**（开发环境）:
- 地址: 111.229.150.28:8848
- 用户名: nacos
- 密码: nacos
- 命名空间: DEV

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
- **API文档**:
  - 用户中心文档: http://localhost:5090/usercenter/doc.html
  - 基础数据文档: http://localhost:5091/base/doc.html

## 开发说明

### 项目特点

- ✅ **Maven多模块管理**: 统一依赖版本，模块职责清晰
- ✅ **微服务架构**: 服务独立部署，支持水平扩展
- ✅ **统一认证**: 网关统一JWT验证，支持Token刷新
- ✅ **服务发现**: 基于Nacos实现服务注册与发现
- ✅ **配置中心**: 使用Nacos配置中心，支持多环境配置
- ✅ **API文档**: 集成Knife4j，自动生成API文档
- ✅ **统一响应**: 全局统一响应格式和异常处理
- ✅ **链路追踪**: 支持TraceId追踪请求链路

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

## 配置文件说明

项目使用Nacos作为配置中心，各服务的 `application.yml` 仅包含基础配置：
- 服务名
- 环境配置
- Nacos连接信息

具体业务配置（数据库连接、JWT密钥等）需在Nacos配置中心配置。

### 网关签名配置

网关使用RSA数字签名机制，确保请求来自网关且未被篡改：

1. **生成RSA密钥对**：
   ```bash
   # 运行密钥生成工具类
   java -cp target/classes com.mms.common.security.utils.RsaKeyGenerator
   ```

2. **配置网关签名密钥**（在Nacos的`public-DEV.yaml`或`gateway-DEV.yaml`中）：
   ```yaml
   gateway:
     signature:
       # RSA私钥（Base64编码的PKCS#8格式），仅网关持有
       private-key: <生成的私钥>
       # RSA公钥（Base64编码的X.509格式），各下游服务持有
       public-key: <生成的公钥>
       # 签名时间戳有效期（毫秒），默认5分钟，用于防重放攻击
       timestamp-validity: 300000
   ```

3. **安全架构**：
   - **网关层**：完整验证JWT token（签名、过期、黑名单），使用RSA私钥对用户信息生成数字签名
   - **服务层**：验证网关签名（RSA公钥），信任网关透传的用户信息，加载权限
   - **优势**：防止请求头篡改、防止绕过网关直接访问、轻量级验证、高性能

## 目录说明

- `mysql/`: 数据库初始化脚本
- `logs/`: 服务日志文件
- `report/`: 项目评估报告
- `teacher/`: 教学指南文档

## 联系方式

- **开发团队**: MMS开发团队
- **邮箱**: 2825646787@qq.com

## 许可证

本项目采用 [MIT License](LICENSE) 许可证
