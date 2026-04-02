package com.zyd.springbootserviceseedproject.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 流程实例组信息
 *
 * 用于将同一业务的多次审批归组（如驳回后重新发起）
 * 同一 instanceGroupId 下可能有多个流程实例
 */
@Data
@TableName("wf_instance_group_info")
public class WfInstanceGroupInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 业务主键ID */
    @TableField("biz_id")
    private Long bizId;

    /** 业务编码 */
    @TableField("biz_key")
    private String bizKey;

    /** 业务类型 */
    @TableField("biz_type")
    private Integer bizType;

    /** 流程定义Key */
    @TableField("process_key")
    private String processKey;

    /** 申请人部门ID */
    @TableField("dept_id")
    private Long deptId;

    /** 申请用户ID */
    @TableField("user_id")
    private Long userId;

    /** 业务类型名称 */
    @TableField("biz_type_name")
    private String bizTypeName;

    /** 业务事项ID */
    @TableField("biz_item_id")
    private Integer bizItemId;

    /** 数据权益人ID */
    @TableField("right_user_id")
    private Long rightUserId;

    /** 数据权益人部门ID */
    @TableField("right_dept_id")
    private Long rightDeptId;

    /** 状态: 1-审批中 2-审批通过 3-已驳回 4-撤回 */
    @TableField("status")
    private Integer status;

    /** 当前审批节点ID */
    @TableField("node_id")
    private String nodeId;

    /** 节点名称 */
    @TableField("node_name")
    private String nodeName;

    /** 节点类型 */
    @TableField("node_type")
    private Integer nodeType;

    /** 导致状态更新的流程实例ID */
    @TableField("status_instance_id")
    private String statusInstanceId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("delete_status")
    @TableLogic(value = "0", delval = "1")
    private Integer deleteStatus;
}
