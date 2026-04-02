package com.zyd.springbootserviceseedproject.workflow.component;

import com.zyd.springbootserviceseedproject.common.utils.SecurityUtils;
import com.zyd.springbootserviceseedproject.workflow.constants.WorkflowConstants;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfRecordSnapshot;
import com.zyd.springbootserviceseedproject.workflow.dto.*;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfRecordSnapshotService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfUserApprovalTaskInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流核心组件
 *
 * 封装所有Flowable操作，提供统一的API供业务层调用
 */
@Slf4j
@Component
public class WorkflowComponent {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private IWfInstanceGroupInfoService instanceGroupInfoService;

    @Resource
    private IWfInstanceInfoService instanceInfoService;

    @Resource
    private IWfUserApprovalTaskInfoService userApprovalTaskInfoService;

    @Resource
    private IWfRecordSnapshotService recordSnapshotService;

    /**
     * 启动流程
     *
     * @param request 启动请求
     * @return 启动结果
     */
    public WorkflowStartResult startProcess(WorkflowStartRequest request) {
        log.info("启动流程: processKey={}, bizKey={}", request.getProcessKey(), request.getBizKey());

        Long operatorId = SecurityUtils.getUserId();
        Long deptId = SecurityUtils.getDeptId();

        // 1. 创建或复用流程组
        WfInstanceGroupInfo group;
        if (request.getInstanceGroupId() != null) {
            group = instanceGroupInfoService.getById(request.getInstanceGroupId());
            group.setStatus(WorkFlowStatusEnums.PENDING.getCode());
            group.setNodeId(null);
            group.setNodeName(null);
            instanceGroupInfoService.updateById(group);
        } else {
            group = new WfInstanceGroupInfo();
            group.setBizId(request.getBizId());
            group.setBizKey(request.getBizKey());
            group.setBizType(request.getBizType());
            group.setProcessKey(request.getProcessKey());
            group.setDeptId(deptId);
            group.setUserId(operatorId);
            group.setBizTypeName(request.getBizTypeName());
            group.setBizItemId(request.getBizItemId());
            group.setRightUserId(request.getRightUserId() != null ? request.getRightUserId() : operatorId);
            group.setStatus(WorkFlowStatusEnums.PENDING.getCode());
            group.setCreateTime(LocalDateTime.now());
            group.setUpdateTime(LocalDateTime.now());
            group.setDeleteStatus(0);
            instanceGroupInfoService.save(group);
        }

        // 2. 构建流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put(WorkflowConstants.VAR_INITIATOR, operatorId);
        variables.put(WorkflowConstants.VAR_PROCESS_TITLE, request.getBizTypeName());
        variables.put(WorkflowConstants.VAR_INSTANCE_GROUP_ID, group.getId());
        variables.put(WorkflowConstants.VAR_RECORD_ID, request.getRecordId());
        variables.put(WorkflowConstants.VAR_BIZ_TYPE_CODE, request.getBizType());
        variables.put(WorkflowConstants.VAR_BIZ_KEY, request.getBizKey());
        if (request.getBizItemId() != null) {
            variables.put(WorkflowConstants.VAR_BIZ_ITEM_CODE, request.getBizItemId());
        }
        // 合并业务扩展变量
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }

        // 3. 启动Flowable流程
        org.flowable.engine.runtime.ProcessInstance processInstance = runtimeService
                .createProcessInstanceBuilder()
                .processDefinitionKey(request.getProcessKey())
                .businessKey(request.getBizKey())
                .variables(variables)
                .start();

        String processInstanceId = processInstance.getId();

        // 4. 创建流程实例记录
        instanceInfoService.createInstance(
                request.getRecordId(), request.getBizType(), request.getProcessKey(),
                group.getId(), processInstanceId);

        // 5. 更新流程组的statusInstanceId
        group.setStatusInstanceId(processInstanceId);
        instanceGroupInfoService.updateById(group);

        // 6. 保存数据快照
        if (request.getFormSchema() != null) {
            recordSnapshotService.createSnapshot(
                    request.getRecordId(), request.getBizId(), request.getBizType(),
                    processInstanceId, request.getFormSchema());
        }

        log.info("流程启动成功: processInstanceId={}, groupId={}", processInstanceId, group.getId());

        return WorkflowStartResult.builder()
                .processInstanceId(processInstanceId)
                .bizKey(request.getBizKey())
                .instanceGroupId(group.getId())
                .build();
    }

    /**
     * 审批通过
     */
    public void approve(WorkflowApproveRequest request) {
        log.info("审批通过: taskId={}", request.getTaskId());
        Task task = getAndValidateTask(request.getTaskId());
        Map<String, Object> variables = new HashMap<>();
        variables.put(WorkflowConstants.VAR_APPROVED, true);
        if (request.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(),
                    WorkflowConstants.COMMENT_APPROVE, request.getComment());
        }
        taskService.complete(task.getId(), variables);
    }

    /**
     * 驳回
     */
    public void reject(WorkflowApproveRequest request) {
        log.info("驳回: taskId={}", request.getTaskId());
        Task task = getAndValidateTask(request.getTaskId());
        Map<String, Object> variables = new HashMap<>();
        variables.put(WorkflowConstants.VAR_APPROVED, false);
        variables.put(WorkflowConstants.VAR_REJECT_REASON, request.getComment());
        if (request.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(),
                    WorkflowConstants.COMMENT_REJECT, request.getComment());
        }
        taskService.complete(task.getId(), variables);
    }

    /**
     * 驳回至指定节点
     */
    public void rejectTo(WorkflowApproveRequest request) {
        log.info("驳回至: taskId={}, targetNodeId={}", request.getTaskId(), request.getTargetNodeId());
        Task task = getAndValidateTask(request.getTaskId());

        if (request.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(),
                    WorkflowConstants.COMMENT_REJECT_TO, request.getComment());
        }

        // 使用changeActivityState跳转到指定节点
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), request.getTargetNodeId())
                .changeState();
    }

    /**
     * 撤回流程
     */
    public void withdrawProcess(WorkflowApproveRequest request) {
        log.info("撤回流程: taskId={}", request.getTaskId());
        Task task = getAndValidateTask(request.getTaskId());

        // 设置撤回原因变量
        runtimeService.setVariable(task.getProcessInstanceId(),
                WorkflowConstants.VAR_WITHDRAW_REASON,
                request.getComment() != null ? request.getComment() : "发起人撤回");

        // 跳转到结束节点（withdrawEndEvent）
        // BPMN 中必须定义 id="withdrawEndEvent" 的结束事件
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), "withdrawEndEvent")
                .changeState();
    }

    /**
     * 转交任务
     */
    public void transfer(WorkflowTransferRequest request) {
        log.info("转交任务: taskId={}, targetUserId={}", request.getTaskId(), request.getTargetUserId());
        Task task = getAndValidateTask(request.getTaskId());
        taskService.setAssignee(task.getId(), request.getTargetUserId().toString());
        if (request.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), "transfer", request.getComment());
        }
    }

    /**
     * 查询任务审批人
     */
    public List<String> queryTaskIdentityLink(String taskId) {
        return taskService.getIdentityLinksForTask(taskId).stream()
                .filter(link -> link.getUserId() != null)
                .map(link -> link.getUserId())
                .distinct()
                .toList();
    }

    /**
     * 获取并校验任务
     */
    private Task getAndValidateTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("任务不存在或已完成: taskId=" + taskId);
        }
        // 如果任务未分配，自动认领
        if (task.getAssignee() == null) {
            taskService.claim(taskId, SecurityUtils.getUserId().toString());
            task = taskService.createTaskQuery().taskId(taskId).singleResult();
        }
        return task;
    }

    /**
     * 获取流程历史记录
     */
    public List<Map<String, Object>> getProcessHistory(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list()
                .stream()
                .map(hai -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("activityId", hai.getActivityId());
                    record.put("activityName", hai.getActivityName());
                    record.put("assignee", hai.getAssignee());
                    record.put("startTime", hai.getStartTime());
                    record.put("endTime", hai.getEndTime());
                    record.put("durationInMillis", hai.getDurationInMillis());
                    return record;
                })
                .toList();
    }

    /**
     * 获取可驳回的节点列表
     */
    public List<Map<String, Object>> getRejectableNodes(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return List.of();
        }

        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list()
                .stream()
                .map(hai -> {
                    Map<String, Object> node = new HashMap<>();
                    node.put("activityId", hai.getActivityId());
                    node.put("activityName", hai.getActivityName());
                    return node;
                })
                .toList();
    }
}
