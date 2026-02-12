# MyBatis Plus 数据源模块 - 功能实现清单

本文档列出了 `mms-common-bc-datasource` 模块需要实现的功能，用于指导开发和完善模块功能。

## 📋 功能清单

### ✅ 已完成功能

- [x] MyBatis Plus 基础配置（分页插件）
- [x] SQL 日志拦截器（带颜色格式化）
- [x] 自动填充时间字段（createTime, updateTime）

---

### 🔴 高优先级（建议优先实现）

#### 1. 基础实体类（BaseEntity）✅
**位置**: `com.mms.common.datasource.entity.BaseEntity`

**功能说明**:
- 提供所有实体类的公共字段
- 包含：id、deleted、createBy、createTime、updateBy、updateTime
- 可选：version（乐观锁版本号）

**参考字段**:
```java
- Long id;                    // 主键ID
- Integer deleted;            // 逻辑删除标记
- Long createBy;             // 创建人ID
- LocalDateTime createTime;   // 创建时间
- Long updateBy;             // 更新人ID
- LocalDateTime updateTime;   // 更新时间
- Integer version;           // 乐观锁版本号（可选）
```

**验收标准**:
- [x] BaseEntity 已创建，包含所有公共字段
- [ ] 所有现有实体类继承 BaseEntity（需要手动修改实体类）
- [ ] 移除实体类中重复的公共字段定义
- [ ] 验证继承后功能正常

---

#### 2. 完善自动填充处理器（MetaObjectHandler）✅
**位置**: `com.mms.common.datasource.config.MyBatisPlusMetaObjectHandler`

**功能说明**:
- 自动填充 createBy（创建人ID）
- 自动填充 updateBy（更新人ID）
- 从 UserContextUtils 获取当前用户ID（使用反射，避免直接依赖）

**需要实现**:
- [x] 获取当前登录用户ID的工具方法（通过反射调用 UserContextUtils.getUserId()）
- [x] insertFill 方法中填充 createBy
- [x] updateFill 方法中填充 updateBy
- [x] 处理用户未登录的情况（系统操作、定时任务等，返回 null）

**验收标准**:
- [x] 创建实体时自动填充 createBy（如果用户已登录）
- [x] 更新实体时自动填充 updateBy（如果用户已登录）
- [x] 用户未登录时能正常处理（字段可为 null）
- [ ] 实际测试验证功能正常

---

#### 3. 乐观锁插件
**位置**: `com.mms.common.datasource.config.MyBatisPlusConfig`

**功能说明**:
- 添加乐观锁拦截器
- 防止并发更新导致的数据覆盖问题

**需要实现**:
- [ ] 在 MybatisPlusInterceptor 中添加 OptimisticLockerInnerInterceptor
- [ ] BaseEntity 中添加 version 字段（如果选择实现）
- [ ] 实体类中使用 @Version 注解标记版本字段

**验收标准**:
- [ ] 并发更新时能检测到版本冲突
- [ ] 更新失败时抛出 OptimisticLockException
- [ ] 更新成功时版本号自动递增

---

#### 4. 性能监控插件
**位置**: `com.mms.common.datasource.config.MyBatisPlusConfig` 或新建拦截器

**功能说明**:
- 监控 SQL 执行时间
- 记录慢 SQL（超过阈值）
- 统计 SQL 执行次数和耗时

**需要实现**:
- [ ] 创建性能监控拦截器
- [ ] 配置慢 SQL 阈值（如 1 秒）
- [ ] 记录慢 SQL 日志或发送告警
- [ ] 可选：统计 SQL 执行情况

**验收标准**:
- [ ] 慢 SQL 能被检测并记录
- [ ] 日志中包含 SQL、参数、耗时等信息
- [ ] 不影响正常业务性能

---

### 🟡 中优先级

#### 5. 逻辑删除全局配置
**位置**: `application.yml` 或配置类

**功能说明**:
- 统一配置逻辑删除的值
- 避免在每个实体类中重复配置

**需要实现**:
- [ ] 在配置文件中设置全局逻辑删除值
- [ ] 或通过配置类设置
- [ ] 验证全局配置生效

**验收标准**:
- [ ] 所有实体类可以统一使用逻辑删除
- [ ] 删除操作自动设置 deleted 值
- [ ] 查询操作自动过滤已删除数据

---

#### 6. 分页配置增强
**位置**: `com.mms.common.datasource.config.MyBatisPlusConfig`

**功能说明**:
- 支持多数据库类型（当前硬编码 MySQL）
- 分页参数合理化校验
- 最大分页大小限制

**需要实现**:
- [ ] 根据数据源自动识别数据库类型
- [ ] 添加分页大小上限（如最大 1000）
- [ ] 分页参数校验（页码、大小合理性）

**验收标准**:
- [ ] 支持 MySQL、PostgreSQL、Oracle 等
- [ ] 超大分页请求被限制
- [ ] 无效分页参数被拒绝

---

#### 7. 类型处理器（TypeHandler）

##### 7.1 枚举类型处理器
**位置**: `com.mms.common.datasource.handler.EnumTypeHandler`

**功能说明**:
- 统一枚举与数据库值的转换
- 支持枚举的 code/value 与数据库字段映射

**需要实现**:
- [ ] 创建通用枚举接口（如 IEnum）
- [ ] 实现枚举类型处理器
- [ ] 实体类中使用 @TableField(typeHandler = EnumTypeHandler.class)

**验收标准**:
- [ ] 枚举值能正确存储到数据库
- [ ] 数据库值能正确转换为枚举
- [ ] 支持自定义枚举值映射

##### 7.2 JSON 类型处理器
**位置**: `com.mms.common.datasource.handler.JsonTypeHandler`

**功能说明**:
- 对象与 JSON 字符串的自动转换
- 支持复杂对象存储到数据库

**需要实现**:
- [ ] 创建 JSON 类型处理器
- [ ] 使用 Jackson 或 Fastjson 进行序列化
- [ ] 实体类中使用 @TableField(typeHandler = JsonTypeHandler.class)

**验收标准**:
- [ ] 对象能正确序列化为 JSON 存储
- [ ] JSON 字符串能正确反序列化为对象
- [ ] 支持 List、Map 等复杂类型

---

#### 8. 工具类封装

##### 8.1 分页工具类
**位置**: `com.mms.common.datasource.utils.PageUtils`

**功能说明**:
- 分页结果转换工具
- 分页参数构建工具

**需要实现**:
- [ ] Page 转 VO 的工具方法
- [ ] 分页参数校验和转换
- [ ] 分页结果格式化

**验收标准**:
- [ ] 能方便地将 MyBatis Plus Page 转换为前端需要的格式
- [ ] 提供常用的分页工具方法

##### 8.2 实体工具类
**位置**: `com.mms.common.datasource.utils.EntityUtils`

**功能说明**:
- 实体对象复制
- 实体对象转换（Entity <-> DTO <-> VO）

**需要实现**:
- [ ] 实体对象深拷贝
- [ ] 批量对象转换
- [ ] 对象属性复制（忽略 null）

**验收标准**:
- [ ] 能方便地进行对象转换
- [ ] 支持批量转换
- [ ] 性能良好

---

### 🟢 低优先级（按需实现）

#### 9. 多租户插件
**位置**: `com.mms.common.datasource.config.MyBatisPlusConfig`

**功能说明**:
- 支持多租户数据隔离
- 自动在 SQL 中添加租户条件

**需要实现**:
- [ ] 创建多租户处理器
- [ ] 从上下文获取租户ID
- [ ] 配置租户字段名

**验收标准**:
- [ ] 查询自动添加租户条件
- [ ] 插入自动设置租户ID
- [ ] 支持租户数据隔离

---

#### 10. 数据权限拦截器
**位置**: `com.mms.common.datasource.interceptor.DataPermissionInterceptor`

**功能说明**:
- 根据用户角色自动过滤数据
- 支持部门、角色等维度的数据权限

**需要实现**:
- [ ] 创建数据权限拦截器
- [ ] 实现权限规则引擎
- [ ] 自动在 SQL 中添加权限条件

**验收标准**:
- [ ] 能根据用户权限自动过滤数据
- [ ] 支持多种权限规则
- [ ] 不影响性能

---

#### 11. 批量操作优化
**位置**: `com.mms.common.datasource.config.MyBatisPlusConfig`

**功能说明**:
- 批量插入/更新性能优化
- 支持批量操作拦截器

**需要实现**:
- [ ] 批量插入优化（如使用 BatchExecutor）
- [ ] 批量更新优化
- [ ] 批量操作拦截器

**验收标准**:
- [ ] 批量操作性能提升
- [ ] 支持大批量数据处理
- [ ] 内存占用合理

---

#### 12. 自动配置类（Spring Boot Starter）
**位置**: `com.mms.common.datasource.autoconfigure.DataSourceAutoConfiguration`

**功能说明**:
- 提供 Spring Boot 自动配置
- 简化模块使用

**需要实现**:
- [ ] 创建自动配置类
- [ ] 条件化配置（@ConditionalOnClass 等）
- [ ] 配置属性类（@ConfigurationProperties）

**验收标准**:
- [ ] 引入依赖后自动配置
- [ ] 支持通过配置文件自定义
- [ ] 提供配置提示（IDE 支持）

---

## 📝 实现建议

### 实现顺序
1. **BaseEntity** - 基础，其他功能可能依赖
2. **完善 MetaObjectHandler** - 常用功能，影响所有实体
3. **乐观锁插件** - 重要功能，解决并发问题
4. **性能监控插件** - 生产环境必需
5. **类型处理器** - 提升开发体验
6. **工具类** - 提升开发效率
7. **其他功能** - 按需实现

### 注意事项
- 每个功能实现后，更新对应的验收标准
- 添加单元测试验证功能
- 更新相关文档说明
- 考虑向后兼容性

---

## ✅ 验收检查清单

完成所有功能后，对照此清单检查：

### 基础功能
- [ ] BaseEntity 已实现，所有实体类已继承
- [ ] MetaObjectHandler 已完善，能自动填充所有审计字段
- [ ] 乐观锁插件已配置，能防止并发更新问题
- [ ] 性能监控插件已实现，能检测慢 SQL

### 增强功能
- [ ] 逻辑删除全局配置已设置
- [ ] 分页配置已增强，支持多数据库
- [ ] 枚举类型处理器已实现
- [ ] JSON 类型处理器已实现
- [ ] 分页工具类已提供
- [ ] 实体工具类已提供

### 可选功能（按需）
- [ ] 多租户插件已实现
- [ ] 数据权限拦截器已实现
- [ ] 批量操作优化已实现
- [ ] 自动配置类已实现

### 文档和测试
- [ ] 所有功能都有使用示例
- [ ] 关键功能有单元测试
- [ ] README 文档已更新
- [ ] 代码注释完整

---

**最后更新**: 2026-01-07
**维护者**: 开发团队
