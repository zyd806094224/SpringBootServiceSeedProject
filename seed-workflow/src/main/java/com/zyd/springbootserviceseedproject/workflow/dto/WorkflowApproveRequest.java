package com.zyd.springbootserviceseedproject.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审批操作请求DTO（通过/驳回/撤回共用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowApproveRequest {
    /** Flowable任务ID */
    @NotBlank(message = "taskId不能为空")
    private String taskId;

    /** 审批意见 */
    private String comment;

    /** 驳回至的目标节点ID（仅驳回至时使用） */
    private String targetNodeId;

    /** 操作人ID（默认取当前登录用户） */
    private Long operatorId;
}
