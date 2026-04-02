# seed-workflow 工作流审批模块

基于 **Flowable 7.0.1** 的通用工作流审批模块，可直接集成到任何 Spring Boot 项目中使用。

## 一、快速接入

### 1. 添加 Maven 依赖

在业务项目的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.zyd</groupId>
    <artifactId>seed-workflow</artifactId>
</dependency>
```

### 2. 执行建表 SQL

执行 `sql/workflow/workflow_init.sql` 脚本，创建以下 5 张业务表：

| 表名 | 说明 |
|------|------|
| `wf_instance_group_info` | 流程实例组（同一业务多次审批归组） |
| `wf_instance_info` | 流程实例（每次发起的流程记录） |
| `wf_approval_progress_info` | 审批进度时间线 |
| `wf_user_approval_task_info` | 用户审批任务（镜像Flowable任务） |
| `wf_record_snapshot` | 审批数据快照（保存发起时的业务数据） |

Flowable 引擎自带的 30+ 张系统表会在应用首次启动时自动创建。

### 3. 配置文件

在 `application.yml` 中确保数据库和 MyBatis 配置正确即可（无需额外配置 Flowable）。

如果需要自定义 Flowable 配置，可覆盖以下属性：

```yaml
flowable:
  database-schema-update: true
  async-executor-activate: false
```

### 4. 放置 BPMN 流程文件

将 `.bpmn20.xml` 格式的流程定义文件放到 `resources/processes/` 目录下。

## 二、API 接口

### 流程操作接口 (`/workflow/process`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/workflow/process/start` | 启动流程 |
| POST | `/workflow/process/approve` | 审批通过 |
| POST | `/workflow/process/reject` | 驳回 |
| POST | `/workflow/process/rejectTo` | 驳回至指定节点 |
| POST | `/workflow/process/withdraw` | 撤回流程 |
| POST | `/workflow/process/transfer` | 转交任务 |
| GET | `/workflow/process/history/{processInstanceId}` | 获取审批历史 |
| GET | `/workflow/process/rejectableNodes/{taskId}` | 获取可驳回节点 |

### 审批中心接口 (`/workflow/approval`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/workflow/approval/progress/{instanceGroupId}` | 获取审批进度 |
| GET | `/workflow/approval/snapshot/{processInstanceId}` | 获取数据快照 |
| GET | `/workflow/approval/bizTypes` | 获取所有业务类型 |

## 三、代码中使用

### 1. 启动流程

```java
@Resource
private WorkflowComponent workflowComponent;

public void submitApplication() {
    WorkflowStartRequest request = WorkflowStartRequest.builder()
            .processKey("demo_approval")       // BPMN流程定义ID
            .bizKey("BIZ_001")                  // 业务标识
            .bizId(1L)                          // 业务主键
            .recordId(100L)                     // 申请记录ID
            .bizType(1)                         // 业务类型
            .bizTypeName("示例审批")             // 业务类型名称
            .bizItemId(1)                       // 业务事项ID
            .formSchema("{\"name\":\"张三\"}")   // 数据快照JSON
            .build();

    WorkflowStartResult result = workflowComponent.startProcess(request);
    // result.getProcessInstanceId()  -- Flowable流程实例ID
    // result.getInstanceGroupId()    -- 流程实例组ID
}
```

### 2. 审批通过

```java
WorkflowApproveRequest request = WorkflowApproveRequest.builder()
        .taskId("task-uuid")          // Flowable任务ID
        .comment("同意")              // 审批意见
        .build();
workflowComponent.approve(request);
```

### 3. 驳回

```java
WorkflowApproveRequest request = WorkflowApproveRequest.builder()
        .taskId("task-uuid")
        .comment("不符合要求，请修改")
        .build();
workflowComponent.reject(request);
```

### 4. 驳回至指定节点

```java
WorkflowApproveRequest request = WorkflowApproveRequest.builder()
        .taskId("task-uuid")
        .targetNodeId("firstApproval")  // 目标节点定义ID
        .comment("驳回到初审节点")
        .build();
workflowComponent.rejectTo(request);
```

### 5. 撤回

```java
WorkflowApproveRequest request = WorkflowApproveRequest.builder()
        .taskId("task-uuid")
        .comment("信息填写有误，撤回修改")
        .build();
workflowComponent.withdrawProcess(request);
```

### 6. 转交

```java
WorkflowTransferRequest request = WorkflowTransferRequest.builder()
        .taskId("task-uuid")
        .targetUserId(200L)            // 目标用户ID
        .comment("请代为处理")
        .build();
workflowComponent.transfer(request);
```

## 四、BPMN 流程设计规范

### 必须遵守的约定

1. **流程监听器**：每个流程的 `startEvent` 上必须配置全局执行监听器：
   ```xml
   <extensionElements>
       <flowable:executionListener event="start" delegateExpression="${workflowGlobalExecutionListener}"/>
       <flowable:executionListener event="end" delegateExpression="${workflowGlobalExecutionListener}"/>
   </extensionElements>
   ```

2. **任务监听器**：每个 `userTask` 上必须配置任务监听器：
   ```xml
   <extensionElements>
       <flowable:taskListener event="create" delegateExpression="${workflowEventListener}"/>
       <flowable:taskListener event="complete" delegateExpression="${workflowEventListener}"/>
       <flowable:taskListener event="assignment" delegateExpression="${workflowEventListener}"/>
   </extensionElements>
   ```

3. **三个结束事件**：流程必须定义三个结束事件（用于区分通过/驳回/撤回）：
   - `endEvent` -- 正常结束（审批通过）
   - `rejectEndEvent` -- 驳回结束
   - `withdrawEndEvent` -- 撤回结束

4. **审批人变量**：
   - 审批人列表使用 `${wfApprovers}` 变量
   - 多实例当前审批人使用 `${wfApprover}` 变量
   - 在启动流程时通过 `variables` 参数传入

5. **网关条件**：
   - 审批通过：`${wfApproved == true}`
   - 审批驳回：`${wfApproved == false}`

### 多实例审批配置

**或签**（一人通过即可）：
```xml
<multiInstanceLoopCharacteristics isSequential="false"
    flowable:collection="${wfApprovers}" flowable:elementVariable="wfApprover">
    <completionCondition>${nrOfCompletedInstances >= 1}</completionCondition>
</multiInstanceLoopCharacteristics>
```

**会签**（全部通过）：
```xml
<multiInstanceLoopCharacteristics isSequential="false"
    flowable:collection="${wfApprovers}" flowable:elementVariable="wfApprover">
    <completionCondition>${nrOfCompletedInstances == nrOfInstances}</completionCondition>
</multiInstanceLoopCharacteristics>
```

### 流程变量说明

| 变量名 | 类型 | 说明 |
|--------|------|------|
| `wfInitiator` | Long | 发起人ID |
| `wfProcessTitle` | String | 流程标题 |
| `wfInstanceGroupId` | Long | 流程实例组ID |
| `wfRecordId` | Long | 业务记录ID |
| `wfBizTypeCode` | Integer | 业务类型 |
| `wfApproved` | Boolean | 审批结果 |
| `wfRejectReason` | String | 驳回原因 |
| `wfWithdrawReason` | String | 撤回原因 |
| `wfApprovers` | List | 审批人ID列表 |
| `wfApprover` | String | 当前审批人ID |

## 五、模块架构

```
seed-workflow/
├── config/              # Flowable引擎配置
│   └── FlowableConfig.java
├── constants/           # 常量定义
│   └── WorkflowConstants.java
├── domain/              # 实体类（5张业务表）
│   ├── WfInstanceGroupInfo.java
│   ├── WfInstanceInfo.java
│   ├── WfApprovalProgressInfo.java
│   ├── WfUserApprovalTaskInfo.java
│   └── WfRecordSnapshot.java
├── dto/                 # 数据传输对象
│   ├── WorkflowStartRequest.java
│   ├── WorkflowStartResult.java
│   ├── WorkflowApproveRequest.java
│   ├── WorkflowTransferRequest.java
│   ├── WorkflowAddSignRequest.java
│   └── WorkflowCallbackContext.java
├── enums/               # 枚举
│   ├── WorkFlowStatusEnums.java
│   ├── WorkFlowTaskStatusEnums.java
│   ├── WorkFlowNodeTypeEnums.java
│   └── WorkFlowOperateTypeEnums.java
├── mapper/              # MyBatis Mapper接口
├── service/             # 服务层接口和实现
│   └── impl/
├── component/           # 核心组件
│   └── WorkflowComponent.java
├── controller/          # REST API
│   ├── WorkflowProcessController.java
│   └── WorkflowApprovalCenterController.java
└── listener/            # 流程监听器
    ├── WorkflowGlobalExecutionListener.java
    └── WorkflowEventListener.java
```

## 六、扩展指南

### 自定义流程回调

实现 `WorkflowCallbackContext` 感知业务结果，可以监听 Spring 事件或继承 `WorkflowGlobalExecutionListener`。

### 自定义审批人策略

在 BPMN 的 `take` 事件中配置自定义的执行监听器，根据业务规则动态设置 `wfApprovers` 变量。

### 多业务类型扩展

通过 `bizType` 和 `bizItemId` 字段区分不同业务场景，配合不同的 BPMN 流程定义实现差异化审批。
