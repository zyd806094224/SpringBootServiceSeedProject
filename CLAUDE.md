# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

基于 Spring Boot 3.5.11 和若依（RuoYi）框架的企业级后端服务多模块种子工程。Java 17，Maven 多模块架构。提供用户管理、RBAC 权限控制、多数据源、Flowable 工作流审批、定时任务、文件管理、系统监控等开箱即用的功能。

## 构建与运行命令

```bash
# 完整构建（跳过测试）
mvn clean package -DskipTests

# 构建单个模块
mvn clean package -pl seed-system -am -DskipTests

# 启动应用（入口模块是 seed-admin）
cd seed-admin && mvn spring-boot:run

# 运行全部测试
mvn test

# 运行指定模块测试
mvn test -pl seed-system
```

应用启动端口 **8061**，本地运行需要 MySQL 8.0+ 和 Redis。

## 模块架构

```
seed-admin       → Web 层（Controller、启动类、配置文件）
seed-framework   → 基础设施（Spring Security、数据源路由、AOP 切面、JWT 过滤器）
seed-system      → 领域模型、MyBatis Mapper、业务 Service
seed-workflow    → Flowable 7.0.1 工作流审批引擎集成
seed-common      → 通用工具、自定义注解、基础类、异常体系
```

依赖链：`seed-admin` → `seed-framework` → `seed-system` → `seed-common`，`seed-workflow` 也依赖 `seed-system`。

## 关键约定

**包根路径**：`com.zyd.springbootserviceseedproject`

**分层模式**（新增业务模块时遵循）：
- 实体类 → `seed-system` 的 `domain/` 包
- Mapper 接口 → `seed-system` 的 `mapper/` 包（XML 在 `resources/mapper/`）
- Service → `seed-system` 的 `service/` 包（接口 + impl 实现类）
- Controller → `seed-admin` 的 `web/controller/` 包

**Controller 常用注解**：
- `@Log(title, businessType)` — AOP 操作日志记录
- `@PreAuthorize("@ss.hasPermi('xxx')")` — 方法级权限控制
- `@Anonymous` — 免认证公开接口
- `@DataSource(DataSourceType.SLAVE)` — 切换数据源
- `@RateLimiter(count, time)` — Redis 接口限流
- `@RepeatSubmit` — 防重复提交
- `@DataScope` — 数据范围过滤（按部门/角色）

**响应格式**：Controller 返回 `AjaxResult`（来自 `seed-common`），分页使用 `TableDataInfo`，通过 `BaseController.startPage()` 开启分页。

**认证流程**：JWT Token 放在 `Authorization` 请求头 → `JwtAuthenticationTokenFilter` → Redis 校验 → Spring Security 上下文。密码使用 BCrypt 加密，Token 有效期 30 分钟。

## 数据库

需要创建两个数据库：
- **seed** — 主业务库，依次导入 `sql/ry_20260330.sql` 和 `sql/workflow/workflow_init.sql`
- **log** — 日志库，导入 `sql/sys_log.sql`

数据源配置在 `seed-admin/src/main/resources/application-druid.yml`（master/slave/log 三数据源）。MyBatis Plus 通过 `delFlag` 字段实现逻辑删除（"0" = 正常，"2" = 已删除）。

## 工作流模块

Flowable 流程定义文件放在 `seed-workflow/src/main/resources/processes/` 目录，格式为 `.bpmn20.xml`。Flowable 系统表启动时自动创建。业务接入通过 `WorkflowComponent` 统一编排，支持单人审批、顺序会签、并行会签、或签、知会、加签等节点类型。

## 配置文件

所有配置文件位于 `seed-admin/src/main/resources/`：
- `application.yml` — 主配置（端口、JWT、Redis、MyBatis Plus、Swagger、文件上传、XSS 防护）
- `application-druid.yml` — 数据源连接（修改本地数据库密码用这个）
- `log4j2.xml` — 日志配置
- `mybatis/mybatis-config.xml` — MyBatis 全局配置
