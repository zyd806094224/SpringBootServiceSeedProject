package com.zyd.springbootserviceseedproject.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务转交请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTransferRequest {
    @NotBlank(message = "taskId不能为空")
    private String taskId;

    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;

    private String comment;
}
