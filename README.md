# SpringBoot服务端种子工程项目

这是一个基于Spring Boot 3.3.4构建的企业级后端服务种子工程，包含了常用的企业级开发组件和最佳实践配置。

## 项目概述

本项目是一个完整的企业级后端服务模板，提供了用户管理、权限控制、多数据源支持、定时任务等常见功能模块，可作为新项目的起始模板快速搭建企业级应用。

## 技术栈

- **核心框架**: Spring Boot 3.3.4
- **编程语言**: Java 17
- **数据库**: MySQL 8.0+
- **持久层框架**: MyBatis Plus 3.5.6
- **数据库连接池**: Alibaba Druid 1.2.5
- **安全框架**: Spring Security + JWT
- **缓存中间件**: Redis
- **定时任务**: XXL-JOB 2.4.1
- **日志框架**: Log4j2
- **JSON处理**: Fastjson 1.2.83
- **工具类库**: Hutool 5.8.5
- **构建工具**: Maven

## 核心功能

### 1. 用户认证与授权
- 基于JWT的无状态认证机制
- Spring Security实现RBAC权限模型
- 支持多角色权限控制
- 密码加密存储（BCrypt）

### 2. 多数据源支持
- 主从数据源分离（master/log）
- 基于注解的动态数据源切换
- 使用Druid连接池进行连接管理

### 3. 缓存管理
- Redis集成用于会话管理和业务缓存
- 自定义Redis缓存工具类

### 4. 定时任务
- 集成XXL-JOB分布式任务调度平台
- 支持可视化任务管理

### 5. 统一响应格式
- 所有API接口返回统一的[Result](src/main/java/com/zyd/springbootserviceseedproject/common/Result.java)对象
- 包含成功/失败状态、消息和数据体

### 6. 异常处理
- 全局异常处理器
- 自定义认证入口点和权限拒绝处理器

## 项目结构

```
src/main/java/com/zyd/springbootserviceseedproject/
├── bean/              # 数据传输对象（DTO）
├── cache/             # 缓存相关组件
├── common/            # 通用类（如统一响应结果）
├── config/            # 配置类
│   ├── db/            # 多数据源配置
│   └── ...            # 其他配置类
├── controller/        # 控制器层
├── entity/            # 实体类
├── filter/            # 过滤器
├── handler/           # 处理器（异常、认证等）
├── manager/           # 业务管理层
├── mapper/            # MyBatis Mapper接口
├── service/           # 服务层
│   └── impl/          # 服务实现类
└── utils/             # 工具类
```

## 数据库设计

项目包含以下核心数据表：

1. [用户表(user)](sql/user.sql) - 存储用户基本信息
2. [角色表(role)](sql/role.sql) - 角色定义
3. [用户角色关系表(user_role)](sql/user_role.sql) - 用户与角色的关联关系
4. [系统日志表(sys_log)](sql/sys_log.sql) - 系统操作日志

初始化数据包含三种角色：
- 超级管理员(super_admin)
- 管理员(admin)
- 普通用户(user)

默认用户：
- admin/密码加密 - 超级管理员
- lisi/密码加密 - 管理员
- zhangsan/123456 - 普通用户

## API接口

### 用户相关
- `POST /user/login` - 用户登录
- `GET /user/list` - 获取用户列表（需ADMIN或SUPER_ADMIN权限）
- `GET /user/test` - 权限测试接口（需USER权限）

### 系统日志相关
- `GET /syslog/list` - 获取系统日志列表（需SUPER_ADMIN权限）

## 配置说明

### 环境配置
- [application.yml](src/main/resources/application.yml) - 主配置文件
- [application-dev.yml](src/main/resources/application-dev.yml) - 开发环境配置
- [application-prod.yml](src/main/resources/application-prod.yml) - 生产环境配置

### 核心配置项
1. **数据库配置**：支持主从两个MySQL数据源
2. **Redis配置**：用于会话管理和缓存
3. **XXL-JOB配置**：定时任务调度中心地址及执行器配置
4. **JWT配置**：token过期时间等安全设置

## 安全机制

1. **JWT Token管理**：
   - 登录成功后生成JWT Token返回给客户端
   - Token存储在Redis中，支持强制失效
   - Token黑名单机制防止重复使用

2. **权限控制**：
   - URL级别访问控制
   - 方法级别注解权限控制（@PreAuthorize）
   - RBAC角色权限模型

## 快速开始

1. 克隆项目到本地
2. 创建MySQL数据库并导入SQL脚本
3. 修改[application-dev.yml](src/main/resources/application-dev.yml)中的数据库和Redis连接配置
4. 启动Redis服务
5. 运行[SpringBootServiceSeedProjectApplication](src/main/java/com/zyd/springbootserviceseedproject/SpringBootServiceSeedProjectApplication.java)主类

## 使用示例

### 用户登录
```bash
curl -X POST http://localhost:8060/user/login \
-H "Content-Type: application/json" \
-d '{"no":"admin","password":"password"}'
```

### 获取用户列表
```bash
curl -X GET http://localhost:8060/user/list \
-H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 扩展性

该种子工程具备良好的扩展性：
- 新增业务模块只需按照现有结构添加相应组件
- 可轻松集成其他第三方服务
- 支持微服务拆分改造
- 配置化管理便于不同环境部署

## 注意事项

1. 项目使用Java 17，请确保运行环境兼容
2. 需要Redis服务支持
3. 需要MySQL数据库并导入初始化SQL脚本
4. 如需使用定时任务功能，需要部署XXL-JOB管理平台