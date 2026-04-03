package com.zyd.springbootserviceseedproject.workflow.component;

import com.zyd.springbootserviceseedproject.common.utils.SecurityUtils;
import com.zyd.springbootserviceseedproject.workflow.constants.WorkflowConstants;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.dto.*;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfUserApprovalTaskInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfRecordSnapshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 2. 先创建流程实例记录（必须在Flowable start之前）
        //    因为Flowable启动后会立即触发task create监听器，监听器需要查询此记录
        WfInstanceInfo instanceInfo = instanceInfoService.createInstance(
                request.getRecordId(), request.getBizType(), request.getProcessKey(),
                group.getId(), "PENDING_" + group.getId());

        // 3. 构建流程变量
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
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }

        // 4. 启动Flowable流程
        org.flowable.engine.runtime.ProcessInstance processInstance = runtimeService
                .createProcessInstanceBuilder()
                .processDefinitionKey(request.getProcessKey())
                .businessKey(request.getBizKey())
                .variables(variables)
                .start();

        String processInstanceId = processInstance.getId();

        // 5. 回填真实的processInstanceId
        instanceInfo.setProcessInstanceId(processInstanceId);
        instanceInfoService.updateById(instanceInfo);

        // 6. 更新流程组
        group.setStatusInstanceId(processInstanceId);
        instanceGroupInfoService.updateById(group);

        // 7. 保存数据快照
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

    public void rejectTo(WorkflowApproveRequest request) {
        log.info("驳回至: taskId={}, targetNodeId={}", request.getTaskId(), request.getTargetNodeId());
        Task task = getAndValidateTask(request.getTaskId());
        if (request.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(),
                    WorkflowConstants.COMMENT_REJECT_TO, request.getComment());
        }
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), request.getTargetNodeId())
                .changeState();
    }

    public void withdrawProcess(WorkflowApproveRequest request) {
        log.info("撤回流程: taskId={}", request.getTaskId());
        Task task = getAndValidateTask(request.getTaskId());
        runtimeService.setVariable(task.getProcessInstanceId(),
                WorkflowConstants.VAR_WITHDRAW_REASON,
                request.getComment() != null ? request.getComment() : "发起人撤回");
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), "withdrawEndEvent")
                .changeState();
    }

    public void transfer(WorkflowTransferRequest request) {
        log.info("转交任务: taskId={}, targetUserId={}", request.getTaskId(), request.getTargetUserId());
        Task task = getAndValidateTask(request.getTaskId());
        taskService.setAssignee(task.getId(), request.getTargetUserId().toString());
        if (request.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), "transfer", request.getComment());
        }
    }

    public List<String> queryTaskIdentityLink(String taskId) {
        return taskService.getIdentityLinksForTask(taskId).stream()
                .filter(link -> link.getUserId() != null)
                .map(link -> link.getUserId())
                .distinct()
                .toList();
    }

    private Task getAndValidateTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("任务不存在或已完成: taskId=" + taskId);
        }
        if (task.getAssignee() == null) {
            taskService.claim(taskId, SecurityUtils.getUserId().toString());
            task = taskService.createTaskQuery().taskId(taskId).singleResult();
        }
        return task;
    }

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
