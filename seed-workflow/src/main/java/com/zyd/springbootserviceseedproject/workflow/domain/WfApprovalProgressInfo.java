package com.zyd.springbootserviceseedproject.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批进度信息
 *
 * 用于存储审批进度时间线的节点信息
 */
@Data
@TableName("wf_approval_progress_info")
public class WfApprovalProgressInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 流程实例组ID */
    @TableField("instance_group_id")
    private Long instanceGroupId;

    /** 流程实例ID */
    @TableField("process_instance_id")
    private String processInstanceId;

    /** Flowable任务ID */
    @TableField("task_id")
    private String taskId;

    /** BPMN节点定义ID */
    @TableField("node_id")
    private String nodeId;

    /** 节点名称 */
    @TableField("node_name")
    private String nodeName;

    /** 节点类型: 0-无 1-单人审批 2-顺序会签 3-并行会签 4-或签 */
    @TableField("node_type")
    private Integer nodeType;

    /** 节点类型描述 */
    @TableField("node_type_desc")
    private String nodeTypeDesc;

    /** 操作类型: 1-发起申请 2-审批通过 3-驳回 4-驳回至 5-撤回 */
    @TableField("opt_type")
    private Integer optType;

    /** 操作类型描述 */
    @TableField("opt_type_desc")
    private String optTypeDesc;

    /** 节点状态: 1-待处理 2-审核通过 3-已驳回 4-已撤回 */
    @TableField("status")
    private Integer status;

    /** 状态描述 */
    @TableField("status_desc")
    private String statusDesc;

    /** 审批意见 */
    @TableField("comment")
    private String comment;

    /** 审批人/发起人ID */
    @TableField("user_id")
    private Long userId;

    /** 审批人/发起人姓名 */
    @TableField("user_name")
    private String userName;

    /** 部门ID */
    @TableField("dept_id")
    private Long deptId;

    /** 部门名称 */
    @TableField("dept_name")
    private String deptName;

    /** 节点时间 */
    @TableField("node_time")
    private LocalDateTime nodeTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
