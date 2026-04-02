package com.zyd.springbootserviceseedproject.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程启动结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStartResult {
    /** Flowable流程实例ID */
    private String processInstanceId;
    /** 业务标识 */
    private String bizKey;
    /** 流程实例组ID */
    private Long instanceGroupId;
}
