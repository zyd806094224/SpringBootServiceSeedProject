package com.zyd.springbootserviceseedproject.workflow.listener;

import com.zyd.springbootserviceseedproject.workflow.constants.WorkflowConstants;
import com.zyd.springbootserviceseedproject.workflow.domain.WfInstanceInfo;
import com.zyd.springbootserviceseedproject.workflow.domain.WfUserApprovalTaskInfo;
import com.zyd.springbootserviceseedproject.workflow.dto.WorkflowCallbackContext;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowTaskStatusEnums;
import com.zyd.springbootserviceseedproject.workflow.service.IWfInstanceInfoService;
import com.zyd.springbootserviceseedproject.workflow.service.IWfUserApprovalTaskInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 全局工作流执行监听器
 *
 * 监听整个流程的启动和结束事件
 */
@Slf4j
@Component("workflowGlobalExecutionListener")
public class WorkflowGlobalExecutionListener implements ExecutionListener {

    @Resource
    private IWfInstanceInfoService instanceInfoService;

    @Resource
    private IWfUserApprovalTaskInfoService userApprovalTaskInfoService;

    @Resource
    private TaskService taskService;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processKey = extractProcessKey(execution.getProcessDefinitionId());

        log.info("流程执行事件: event={}, processKey={}", eventName, processKey);

        switch (eventName) {
            case EVENTNAME_START -> handleProcessStart(execution);
            case EVENTNAME_END -> handleProcessEnd(execution, processKey);
            default -> log.debug("忽略事件: {}", eventName);
        }
    }

    private void handleProcessStart(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        // 检查是否已存在实例记录（changeActivityState会重复触发start事件）
        WfInstanceInfo existing = instanceInfoService.getByProcessInstanceId(processInstanceId);
        if (existing != null) {
            log.info("流程实例已存在，跳过: processInstanceId={}", processInstanceId);
            return;
        }
        log.info("流程启动: processInstanceId={}", processInstanceId);
    }

    private void handleProcessEnd(DelegateExecution execution, String processKey) {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessInstanceBusinessKey();

        String rejectReason = (String) execution.getVariable(WorkflowConstants.VAR_REJECT_REASON);
        String withdrawReason = (String) execution.getVariable(WorkflowConstants.VAR_WITHDRAW_REASON);

        WfInstanceInfo instanceInfo = instanceInfoService.getByProcessInstanceId(processInstanceId);
        if (instanceInfo == null) {
            log.warn("流程实例不存在: processInstanceId={}", processInstanceId);
            return;
        }

        // 根据结束原因更新实例状态
        if (rejectReason != null) {
            instanceInfoService.updateToFinalStatus(processInstanceId, WorkFlowStatusEnums.REJECT.getCode());
            log.info("流程驳回结束: processKey={}, processInstanceId={}", processKey, processInstanceId);
        } else if (withdrawReason != null) {
            instanceInfoService.updateToFinalStatus(processInstanceId, WorkFlowStatusEnums.WITHDRAW.getCode());
            log.info("流程撤回结束: processKey={}, processInstanceId={}", processKey, processInstanceId);
        } else {
            instanceInfoService.updateToFinalStatus(processInstanceId, WorkFlowStatusEnums.PASSED.getCode());
            log.info("流程正常完成: processKey={}, processInstanceId={}", processKey, processInstanceId);
        }
    }

    private String extractProcessKey(String processDefinitionId) {
        if (processDefinitionId != null && processDefinitionId.contains(":")) {
            return processDefinitionId.split(":")[0];
        }
        return processDefinitionId;
    }

    private Long getLongVariable(DelegateExecution execution, String name) {
        Object value = execution.getVariable(name);
        if (value instanceof Long l) {
            return l;
        } else if (value instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
