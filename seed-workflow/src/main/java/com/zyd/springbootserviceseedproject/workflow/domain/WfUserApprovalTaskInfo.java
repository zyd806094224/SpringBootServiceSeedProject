package com.zyd.springbootserviceseedproject.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.zyd.springbootserviceseedproject.workflow.enums.WorkFlowNodeTypeEnums;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户审批任务信息
 *
 * 业务层镜像Flowable任务，便于查询和展示
 */
@Data
@TableName("wf_user_approval_task_info")
public class WfUserApprovalTaskInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** Flowable工作流任务ID */
    @TableField("task_id")
    private String taskId;

    /** 业务申请记录ID */
    @TableField("record_id")
    private Long recordId;

    /** 流程key */
    @TableField("process_key")
    private String processKey;

    /** Flowable流程实例ID */
    @TableField("process_instance_id")
    private String processInstanceId;

    /** BPMN节点定义ID */
    @TableField("node_id")
    private String nodeId;

    /** 节点名称 */
    @TableField("node_name")
    private String nodeName;

    /** 节点类型 */
    @TableField("node_type")
    private Integer nodeType;

    /** 审批人部门ID */
    @TableField("dept_id")
    private Long deptId;

    /** 审批人ID */
    @TableField("user_id")
    private Long userId;

    /** 审批申请时间 */
    @TableField("approval_apply_time")
    private LocalDateTime approvalApplyTime;

    /** 审批指派时间 */
    @TableField("approval_assigned_time")
    private LocalDateTime approvalAssignedTime;

    /** 审批处理时间 */
    @TableField("approval_deal_time")
    private LocalDateTime approvalDealTime;

    /** 状态 */
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("delete_status")
    @TableLogic(value = "0", delval = "1")
    private Integer deleteStatus;

    /** 是否为或签任务 */
    public boolean isOrSign() {
        return WorkFlowNodeTypeEnums.OR_SIGN.getCode().equals(this.nodeType);
    }

    /** 是否为会签任务 */
    public boolean isCounterSign() {
        WorkFlowNodeTypeEnums type = WorkFlowNodeTypeEnums.getByCode(this.nodeType);
        return type.isCounterSign();
    }
}
