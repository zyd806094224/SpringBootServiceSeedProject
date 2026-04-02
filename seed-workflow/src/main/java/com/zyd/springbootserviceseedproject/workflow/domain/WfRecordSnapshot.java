package com.zyd.springbootserviceseedproject.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批数据快照
 *
 * 保存每次审批发起时的业务数据快照
 */
@Data
@TableName("wf_record_snapshot")
public class WfRecordSnapshot {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 业务申请记录ID */
    @TableField("record_id")
    private Long recordId;

    /** 业务主键ID */
    @TableField("biz_id")
    private Long bizId;

    /** 业务类型 */
    @TableField("biz_type")
    private Integer bizType;

    /** Flowable流程实例ID */
    @TableField("process_instance_id")
    private String processInstanceId;

    /** 表单数据快照（JSON） */
    @TableField("form_schema")
    private String formSchema;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
