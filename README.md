# Personal Manager - 后端服务

个人管理系统的后端服务工程，基于 Spring Boot 3.5.11 和若依（RuoYi）框架构建，提供用户管理、权限控制、多数据源、工作流审批、定时任务、文件管理、系统监控等开箱即用的功能模块。当前主要用于支撑账号密码管理业务，后续将持续扩展其他个人管理功能。

**相关工程：**
- 前端：[Vue3SeedProject](https://github.com/zyd806094224/Vue3SeedProject)
- 移动端：[RNHybrid](https://github.com/zyd806094224/RNHybrid)

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.11 | 核心框架 |
| Java | 17 | 编程语言 |
| MyBatis Plus | 3.5.7 | 持久层框架 |
| Flowable | 7.0.1 | 工作流引擎 |
| Druid | 1.2.28 | 数据库连接池 |
| Spring Security | 6.x | 安全框架 |
| JWT (JJWT) | 0.11.5 | Token 认证 |
| Redis (Lettuce) | - | 缓存中间件 |
| PageHelper | 2.1.1 | MyBatis 分页插件 |
| SpringDoc | 2.8.16 | Swagger API 文档 |
| XXL-JOB | 2.4.1 | 分布式任务调度 |
| FastJSON2 | 2.0.61 | JSON 处理 |
| Apache POI | 4.1.2 | Excel 导入导出 |
| Hutool | 5.8.5 | 工具类库 |
| Log4j2 | - | 日志框架 |
| Kaptcha | 2.3.3 | 验证码生成 |
| Lombok | 1.18.36 | 代码简化 |

## 模块说明

```
SpringBootServiceSeedProject
├── seed-admin          # Web 服务入口（Controller、启动类、配置文件）
├── seed-framework      # 框架核心（Security、数据源、AOP、拦截器）
├── seed-system         # 系统模块（领域模型、Mapper、Service）
├── seed-workflow       # 工作流审批模块（基于 Flowable）
└── seed-common         # 通用工具（注解、异常、工具类、基础实体）
```

模块依赖关系：

```
seed-admin
├── seed-framework
│   ├── seed-system
│   │   └── seed-common
│   └── seed-workflow
│       └── seed-system
│           └── seed-common
└── seed-workflow
```

### seed-admin

Web 层入口模块，包含所有 REST Controller 和 Spring Boot 启动类。

```
web/
├── controller/
│   ├── common/          # 通用接口（验证码、文件上传下载）
│   ├── monitor/         # 监控接口（日志、在线用户、缓存、服务器状态）
│   ├── system/          # 系统管理接口（用户、角色、菜单、部门、字典、配置、通知）
│   ├── tool/            # 测试接口
│   └── workflow/        # 工作流审批接口（流程操作、审批中心）
├── core/config/         # Swagger 配置
└── service/             # 文件上传服务
```

### seed-framework

框架核心层，提供安全认证、数据源管理、AOP 切面等基础设施。

```
framework/
├── aspectj/             # AOP 切面（日志、数据范围、限流、防重提交）
├── config/              # 配置类（Security、Druid、Redis、MyBatis、线程池、Kaptcha）
├── datasource/          # 动态数据源路由
├── interceptor/         # 拦截器（限流、防重提交）
├── manager/             # 异步管理器
├── security/            # Security 组件（过滤器、认证处理器、用户服务）
├── web/
│   ├── domain/          # 服务器监控领域模型
│   ├── exception/       # 全局异常处理器
│   └── service/         # 框架级 Service（Token、权限、登录记录）
```

### seed-system

系统业务模块，包含核心领域模型、数据访问层和业务逻辑层。

```
system/
├── domain/              # 实体类和 VO
├── mapper/              # MyBatis Mapper 接口
└── service/             # 业务 Service 接口及实现
```

### seed-workflow

工作流审批模块，基于 Flowable 7.0.1 实现，提供完整的审批流程管理能力。

```
workflow/
├── component/           # 核心组件（WorkflowComponent 流程编排）
├── config/              # Flowable 引擎配置
├── constants/           # 流程变量常量定义
├── controller/          # REST 接口（流程操作、审批中心）
├── domain/              # 实体类（实例组、实例、任务、进度、快照）
├── dto/                 # 请求/响应 DTO
├── enums/               # 枚举（流程状态、任务状态、节点类型、操作类型）
├── listener/            # 事件监听器（任务事件、流程执行事件）
├── mapper/              # MyBatis Mapper 接口
└── service/             # 业务 Service 接口及实现
```

### seed-common

通用工具模块，被其他所有模块依赖。

```
common/
├── annotation/          # 自定义注解（@Log、@DataScope、@DataSource、@RateLimiter 等）
├── config/              # 通用配置
├── constant/            # 常量定义
├── core/
│   ├── controller/      # BaseController
│   ├── domain/          # 基础实体、AjaxResult、LoginUser 等
│   ├── page/            # 分页封装
│   ├── redis/           # Redis 缓存工具类
│   └── text/            # 文本工具
├── enums/               # 枚举类型
├── exception/           # 自定义异常体系
├── filter/              # 过滤器（XSS、防盗链、可重复读取请求）
├── utils/               # 工具类集合
└── xss/                 # XSS 过滤
```

## 核心功能

### 认证与授权
- JWT 无状态 Token 认证，Token 存储在 Redis 中支持强制失效
- Spring Security 实现 RBAC 权限模型，支持 URL 级和方法级（`@PreAuthorize`）权限控制
- BCrypt 密码加密，密码错误次数限制与账户锁定
- 验证码登录（支持数学计算型和字符型）
- `@Anonymous` 注解免认证访问

### 工作流审批
- 基于 Flowable 7.0.1 引擎，支持 BPMN 2.0 标准流程定义
- **审批节点类型**：单人审批、顺序会签、并行会签、或签、知会、加签
- **流程操作**：发起审批、通过、驳回、驳回到指定节点、撤回、转办
- **实例分组**：支持同一业务的多次审批（驳回后重新发起），通过实例组统一管理
- **审批中心**：我的待办任务查询、审批进度追踪、历史记录查看
- **数据快照**：审批发起时自动保存业务数据快照，支持回溯查看
- **事件驱动**：通过监听器自动同步 Flowable 任务状态到业务层
- 内置示例流程定义（`demo_approval.bpmn20.xml`）

### 多数据源
- 主库（master）/ 从库（slave）/ 日志库（log）三数据源分离
- `@DataSource` 注解动态切换数据源
- Druid 连接池监控台（`/druid/`）

### 系统管理
- 用户管理（增删改查、导入导出 Excel、头像上传、密码重置）
- 角色管理（数据权限分配、菜单权限分配）
- 菜单管理（树形结构）
- 部门管理（树形结构）
- 岗位管理
- 字典管理
- 参数配置
- 通知公告

### 系统监控
- 操作日志（`@Log` 注解自动记录）
- 登录日志
- 在线用户（支持强退）
- 缓存监控（Redis Key 管理）
- 服务器监控（CPU、内存、JVM、磁盘）

### 其他功能
- 文件上传下载（单文件/多文件，类型和大小校验）
- XSS 攻击防护
- 防盗链（Referer 过滤）
- 接口限流（`@RateLimiter`，基于 Redis）
- 防重提交（`@RepeatSubmit`）
- 数据范围过滤（`@DataScope`）
- 敏感数据序列化（`@Sensitive`）
- Swagger API 文档（`/swagger-ui.html`）
- 分布式任务调度（XXL-JOB 集成）

## 数据库

| 文件 | 说明 |
|------|------|
| `sql/ry_20260330.sql` | 主数据库初始化脚本（用户、角色、菜单、部门、字典、配置等全套表结构和初始数据） |
| `sql/sys_log.sql` | 系统日志表（用于 log 数据库） |
| `sql/workflow/workflow_init.sql` | 工作流审批模块建表脚本（5 张业务表，Flowable 系统表启动时自动创建） |

需要创建两个数据库：
- `seed` — 主业务库，导入 `ry_20260330.sql` 和 `workflow/workflow_init.sql`
- `log` — 日志库，导入 `sys_log.sql`

### 工作流业务表

| 表名 | 说明 |
|------|------|
| `wf_instance_group_info` | 流程实例组信息（聚合同一业务的多次审批） |
| `wf_instance_info` | 流程实例信息（每次审批生成一条） |
| `wf_user_approval_task_info` | 用户审批任务（镜像 Flowable 任务到业务层） |
| `wf_approval_progress_info` | 审批进度信息（审批时间线记录） |
| `wf_record_snapshot` | 审批数据快照（发起时保存的业务数据 JSON） |

默认初始角色：超级管理员（super_admin）、管理员（admin）、普通用户（user）

## API 接口

### 认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 用户登录 |
| POST | `/register` | 用户注册 |
| GET | `/getInfo` | 获取当前用户信息及权限 |
| GET | `/getRouters` | 获取当前用户菜单路由 |
| GET | `/captchaImage` | 获取验证码 |

### 工作流审批
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/workflow/process/start` | 发起审批流程 |
| POST | `/workflow/process/approve` | 审批通过 |
| POST | `/workflow/process/reject` | 驳回任务 |
| POST | `/workflow/process/rejectTo` | 驳回到指定节点 |
| POST | `/workflow/process/withdraw` | 撤回流程 |
| POST | `/workflow/process/transfer` | 转办任务 |
| GET | `/workflow/process/history/{processInstanceId}` | 获取流程历史 |
| GET | `/workflow/process/rejectableNodes/{taskId}` | 获取可驳回节点 |
| GET | `/workflow/approval/progress/{instanceGroupId}` | 获取审批进度 |
| GET | `/workflow/approval/snapshot/{processInstanceId}` | 获取数据快照 |
| GET | `/workflow/approval/bizTypes` | 获取所有业务类型 |
| GET | `/workflow/approval/pendingTasks/{processInstanceId}` | 获取待办任务 |
| GET | `/workflow/approval/myPendingTasks` | 获取我的待办任务 |

### 系统管理
| 方法 | 路径 | 说明 |
|------|------|------|
| * | `/system/user/**` | 用户管理 |
| * | `/system/role/**` | 角色管理 |
| * | `/system/menu/**` | 菜单管理 |
| * | `/system/dept/**` | 部门管理 |
| * | `/system/post/**` | 岗位管理 |
| * | `/system/dict/**` | 字典管理 |
| * | `/system/config/**` | 参数配置 |
| * | `/system/notice/**` | 通知公告 |

### 监控
| 方法 | 路径 | 说明 |
|------|------|------|
| * | `/monitor/operlog/**` | 操作日志 |
| * | `/monitor/logininfor/**` | 登录日志 |
| * | `/monitor/online/**` | 在线用户 |
| * | `/monitor/cache/**` | 缓存监控 |
| * | `/monitor/server` | 服务器状态 |

### 文件操作
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/file/upload` | 单文件上传 |
| POST | `/file/uploadMultiple` | 多文件上传 |
| DELETE | `/file/delete/{fileName}` | 删除文件 |
| GET | `/file/info/{fileName}` | 获取文件信息 |
| GET | `/file/exists/{fileName}` | 检查文件是否存在 |

### 文档
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Druid 监控台: `http://localhost:8080/druid/`（账号: seed / 123456）

## 配置文件

所有配置文件位于 `seed-admin/src/main/resources/`：

| 文件 | 说明 |
|------|------|
| `application.yml` | 主配置（端口、JWT、Redis、MyBatis、Swagger、XXL-JOB、文件上传等） |
| `application-druid.yml` | 数据源配置（master/slave/log 三库连接信息、Druid 连接池参数） |
| `log4j2.xml` | Log4j2 日志配置 |
| `mybatis/mybatis-config.xml` | MyBatis 全局配置 |

### 关键配置项

```yaml
# 服务端口
server.port: 8080

# JWT Token
token.header: Authorization
token.secret: abcdefghijklmnopqrstuvwxyz
token.expireTime: 30  # 分钟

# 文件上传
spring.servlet.multipart.max-file-size: 10MB
spring.servlet.multipart.max-request-size: 100MB

# XSS 防护
xss.enabled: true
```

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis
- Maven 3.6+
- （可选）XXL-JOB 调度中心 — 使用定时任务功能时需要

### 启动步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd SpringBootServiceSeedProject
   ```

2. **初始化数据库**
   - 创建 `seed` 数据库，依次导入 `sql/ry_20260330.sql` 和 `sql/workflow/workflow_init.sql`
   - 创建 `log` 数据库，导入 `sql/sys_log.sql`

3. **修改配置**
   - 编辑 `seed-admin/src/main/resources/application-druid.yml`，修改数据库连接地址和密码
   - 编辑 `seed-admin/src/main/resources/application.yml`，修改 Redis 连接信息（如有需要）

4. **启动 Redis 服务**

5. **构建并启动项目**
   ```bash
   mvn clean package -DskipTests
   cd seed-admin
   mvn spring-boot:run
   ```

   或直接运行启动类 `com.zyd.springbootserviceseedproject.SeedApplication`

6. **访问**
   - API 服务：`http://localhost:8080`
   - Swagger 文档：`http://localhost:8080/swagger-ui.html`

### 使用示例

**登录获取 Token**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**访问受保护接口**
```bash
curl http://localhost:8080/system/user/list \
  -H "Authorization: Bearer <your_token>"
```

**发起审批流程**
```bash
curl -X POST http://localhost:8080/workflow/process/start \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"bizType": 1, "processKey": "demo_approval", "bizKey": "ORDER_001", "bizId": 100, "recordId": 1}'
```

**查看我的待办任务**
```bash
curl http://localhost:8080/workflow/approval/myPendingTasks \
  -H "Authorization: Bearer <your_token>"
```

## 扩展指南

### 系统功能扩展
- **新增业务模块**：按照 `seed-system` 的分层结构（domain -> mapper -> service）开发，Controller 放在 `seed-admin` 中
- **自定义数据源切换**：在 Service 方法上使用 `@DataSource(DataSourceType.SLAVE)` 注解
- **操作日志记录**：在 Controller 方法上添加 `@Log(title = "模块名", businessType = BusinessType.INSERT)` 注解
- **接口限流**：在 Controller 方法上添加 `@RateLimiter(count = 10, time = 60)` 注解
- **API 文档**：Controller 类和方法上使用 SpringDoc 注解（`@Tag`、`@Operation`）自动生成文档

### 工作流扩展
- **新增流程定义**：在 `seed-workflow/src/main/resources/processes/` 目录下添加 `.bpmn20.xml` 文件
- **自定义审批节点**：支持单人审批、会签（顺序/并行）、或签、知会、加签等节点类型
- **业务接入**：通过 `WorkflowComponent` 统一编排流程，业务模块只需关注发起和结果回调
- **流程监听**：实现 `WorkflowEventListener` 或 `WorkflowGlobalExecutionListener` 处理流程事件
