package com.zyd.springbootserviceseedproject.workflow.listener;

import com.zyd.springbootserviceseedproject.workflow.constants.WorkflowConstants;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceGroupInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowNodeTypeEnums;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceGroupInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
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
 *
 * 监听每个节点的创建、完成、分配事件
 */
@Slf4j
@Component("workflowEventListener")
public class WorkflowEventListener implements ExecutionListener, TaskListener {

    @Resource
    private IWfInstanceInfoService instanceInfoService;

    @Resource
    private IWfInstanceGroupInfoService instanceGroupInfoService;

    @Resource
    private IWfUserApprovalTaskInfoService userApprovalTaskInfoService;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        log.info("执行事件: event={}, processInstanceId={}",
                eventName, execution.getProcessInstanceId());
        // take事件：节点流转时触发，可用于动态设置审批人
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String processInstanceId = delegateTask.getProcessInstanceId();

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

        // 查询流程实例获取业务信息
        WfInstanceInfo instanceInfo = instanceInfoService.getByProcessInstanceId(processInstanceId);
        if (instanceInfo == null) {
            log.warn("流程实例不存在，跳过任务创建: processInstanceId={}", processInstanceId);
            return;
        }

        // 更新流程组的当前节点信息
        Long instanceGroupId = instanceInfo.getInstanceGroupId();
        if (instanceGroupId != null) {
            WfInstanceGroupInfo group = instanceGroupInfoService.getById(instanceGroupId);
            if (group != null) {
                group.setNodeId(delegateTask.getTaskDefinitionKey());
                group.setNodeName(delegateTask.getName());
                // 解析节点类型
                Integer nodeType = parseNodeType(delegateTask);
                group.setNodeType(nodeType);
                instanceGroupInfoService.updateById(group);
            }
        }

        // 创建业务层任务记录
        String assignee = delegateTask.getAssignee();
        Long userId = assignee != null ? Long.parseLong(assignee) : null;

        if (userId != null) {
            userApprovalTaskInfoService.createTask(
                    delegateTask.getId(),
                    instanceInfo.getRecordId(),
                    instanceInfo.getProcessKey(),
                    processInstanceId,
                    delegateTask.getTaskDefinitionKey(),
                    delegateTask.getName(),
                    parseNodeType(delegateTask),
                    userId,
                    null // deptId 由业务方自行填充
            );
        }
    }

    private void handleTaskComplete(DelegateTask delegateTask) {
        // 更新业务层任务状态为已完成
        userApprovalTaskInfoService.updateStatus(
                delegateTask.getId(),
                WorkFlowStatusEnums.PASSED.getCode()
        );
    }

    /**
     * 解析节点类型
     * 通过多实例特征判断：有 collection 变量为多人审批节点
     */
    private Integer parseNodeType(DelegateTask delegateTask) {
        Object approvers = delegateTask.getVariable(WorkflowConstants.VAR_APPROVERS);
        if (approvers instanceof List<?> list && list.size() > 1) {
            // 有多个审批人：检查完成条件判断是或签还是会签
            // 默认返回或签，业务方可通过扩展覆盖
            return WorkFlowNodeTypeEnums.OR_SIGN.getCode();
        }
        return WorkFlowNodeTypeEnums.SINGLE.getCode();
    }
}
