package com.zyd.springbootserviceseedproject.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * 工作流回调上下文
 *
 * 流程结束时传递给业务方的上下文信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowCallbackContext {
    /** 业务记录ID */
    private Long recordId;
    /** 业务类型 */
    private Integer bizType;
    /** 任务ID */
    private String taskId;
    /** 流程实例组ID */
    private Long instanceGroupId;
    /** 流程实例ID */
    private String processInstanceId;
    /** 流程Key */
    private String processKey;
    /** 处理人ID */
    private Long assigneeId;
    /** 业务标识 */
    private String businessKey;
    /** 发起人ID */
    private Long initiatorId;
    /** 原因/意见 */
    private String reason;
    /** 流程变量 */
    private Map<String, Object> variables;
}
