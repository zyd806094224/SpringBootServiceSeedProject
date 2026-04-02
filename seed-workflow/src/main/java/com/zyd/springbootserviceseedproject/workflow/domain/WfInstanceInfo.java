package com.zyd.springbootserviceseedproject.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 流程实例信息
 *
 * 记录每个具体的流程实例，通过 instanceGroupId 关联到流程组
 */
@Data
@TableName("wf_instance_info")
public class WfInstanceInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 业务申请记录ID */
    @TableField("record_id")
    private Long recordId;

    /** 业务类型 */
    @TableField("biz_type")
    private Integer bizType;

    /** 流程定义Key */
    @TableField("process_key")
    private String processKey;

    /** 流程实例组ID */
    @TableField("instance_group_id")
    private Long instanceGroupId;

    /** Flowable流程实例ID */
    @TableField("process_instance_id")
    private String processInstanceId;

    /** 版本号（同一组内终态时递增） */
    @TableField("version")
    private Integer version;

    /** 状态: 1-审批中 2-审批通过 3-已驳回 4-撤回 */
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("delete_status")
    @TableLogic(value = "0", delval = "1")
    private Integer deleteStatus;
}
