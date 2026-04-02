package com.zyd.springbootserviceseedproject.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * 启动流程请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStartRequest {
    /** 业务类型code */
    private Integer bizType;

    /** 流程定义Key（与BPMN文件中的process id一致） */
    @NotBlank(message = "流程定义Key不能为空")
    private String processKey;

    /** 业务标识 */
    @NotBlank(message = "业务标识不能为空")
    private String bizKey;

    /** 业务主表ID */
    @NotNull(message = "bizId不能为空")
    private Long bizId;

    /** 申请记录ID */
    @NotNull(message = "recordId不能为空")
    private Long recordId;

    /** 流程实例组ID（驳回后重新发起时复用，首次发起传null） */
    private Long instanceGroupId;

    /** 业务类型名称 */
    @NotBlank(message = "业务类型名称不能为空")
    private String bizTypeName;

    /** 业务事项ID */
    private Integer bizItemId;

    /** 数据快照内容（JSON） */
    private String formSchema;

    /** 数据权益人ID */
    private Long rightUserId;

    /** 业务扩展变量 */
    private Map<String, Object> variables;
}
