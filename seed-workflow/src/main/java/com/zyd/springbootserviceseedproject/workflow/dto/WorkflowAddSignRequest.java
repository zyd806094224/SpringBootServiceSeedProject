package com.zyd.springbootserviceseedproject.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 加签请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowAddSignRequest {
    @NotBlank(message = "taskId不能为空")
    private String taskId;

    @NotEmpty(message = "加签用户ID列表不能为空")
    private List<Long> addUserIds;

    private String comment;
}
