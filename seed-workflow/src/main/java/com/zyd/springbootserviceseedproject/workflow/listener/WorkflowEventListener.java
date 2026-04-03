package com.zyd.springbootserviceseedproject.workflow.listener;

import com.zyd.springbootserviceseedproject.workflow.constants.WorkflowConstants;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowNodeTypeEnums;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfUserApprovalTaskInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 工作流任务事件监听器
 */
@Slf4j
@Component("workflowEventListener")
public class WorkflowEventListener implements ExecutionListener, TaskListener {

    @Resource
    private IWfInstanceGroupInfoService instanceGroupInfoService;

    @Resource
    private IWfUserApprovalTaskInfoService userApprovalTaskInfoService;

    @Override
    public void notify(DelegateExecution execution) {
        log.info("执行事件: event={}, processInstanceId={}",
                execution.getEventName(), execution.getProcessInstanceId());
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        log.info("任务事件: event={}, taskName={}, assignee={}",
                eventName, delegateTask.getName(), delegateTask.getAssignee());

        switch (eventName) {
            case EVENTNAME_CREATE -> handleTaskCreate(delegateTask);
            case EVENTNAME_COMPLETE -> handleTaskComplete(delegateTask);
            case EVENTNAME_ASSIGNMENT -> log.info("任务分配: taskId={}, assignee={}",
                    delegateTask.getId(), delegateTask.getAssignee());
            default -> log.debug("忽略任务事件: {}", eventName);
        }
    }

    private void handleTaskCreate(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();

        // 从流程变量获取业务信息（不查数据库，避免时序问题）
        Long instanceGroupId = (Long) delegateTask.getVariable(WorkflowConstants.VAR_INSTANCE_GROUP_ID);
        Long recordId = (Long) delegateTask.getVariable(WorkflowConstants.VAR_RECORD_ID);
        String processKey = delegateTask.getProcessDefinitionId().split(":")[0];

        if (instanceGroupId == null) {
            log.warn("流程变量中无instanceGroupId，跳过任务创建: processInstanceId={}", processInstanceId);
            return;
        }

        // 更新流程组的当前节点信息
        WfInstanceGroupInfo group = instanceGroupInfoService.getById(instanceGroupId);
        if (group != null) {
            group.setNodeId(delegateTask.getTaskDefinitionKey());
            group.setNodeName(delegateTask.getName());
            group.setNodeType(parseNodeType(delegateTask));
            group.setStatusInstanceId(processInstanceId);
            instanceGroupInfoService.updateById(group);
        }

        // 创建业务层任务记录
        String assignee = delegateTask.getAssignee();
        Long userId = assignee != null ? Long.parseLong(assignee) : null;

        if (userId != null) {
            userApprovalTaskInfoService.createTask(
                    delegateTask.getId(),
                    recordId,
                    processKey,
                    processInstanceId,
                    delegateTask.getTaskDefinitionKey(),
                    delegateTask.getName(),
                    parseNodeType(delegateTask),
                    userId,
                    null
            );
        }
    }

    private void handleTaskComplete(DelegateTask delegateTask) {
        userApprovalTaskInfoService.updateStatus(
                delegateTask.getId(),
                WorkFlowStatusEnums.PASSED.getCode()
        );
    }

    private Integer parseNodeType(DelegateTask delegateTask) {
        Object approvers = delegateTask.getVariable(WorkflowConstants.VAR_APPROVERS);
        if (approvers instanceof List<?> list && list.size() > 1) {
            return WorkFlowNodeTypeEnums.OR_SIGN.getCode();
        }
        return WorkFlowNodeTypeEnums.SINGLE.getCode();
    }
}
