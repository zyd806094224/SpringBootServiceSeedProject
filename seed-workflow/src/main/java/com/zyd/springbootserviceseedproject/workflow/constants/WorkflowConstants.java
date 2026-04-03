package com.zyd.springbootserviceseedproject.workflow.constants;

/**
 * 工作流常量定义
 *
 * 统一管理所有流程中使用的变量名称，避免硬编码
 * 所有变量以 "wf" 开头，采用驼峰命名
 */
public final class WorkflowConstants {

    private WorkflowConstants() {
    }

    // ==================== 流程核心变量 ====================
    public static final String VAR_INITIATOR = "wfInitiator";
    public static final String VAR_PROCESS_TITLE = "wfProcessTitle";
    public static final String VAR_INSTANCE_GROUP_ID = "wfInstanceGroupId";
    public static final String VAR_RECORD_ID = "wfRecordId";
    public static final String VAR_BIZ_TYPE_CODE = "wfBizTypeCode";
    public static final String VAR_BIZ_KEY = "biz_key";
    public static final String VAR_BIZ_ITEM_CODE = "wfBizItemCode";

    // ==================== 审批结果变量 ====================
    public static final String VAR_APPROVED = "wfApproved";
    public static final String VAR_REJECT_REASON = "wfRejectReason";
    public static final String VAR_WITHDRAW_REASON = "wfWithdrawReason";

    // ==================== 会签/或签变量 ====================
    public static final String VAR_COUNTER_SIGN_REJECTED = "wfCounterSignRejected";
    public static final String VAR_NOTIFY_USERS = "wfNotifyUsers";
    public static final String VAR_ADD_SIGN_INFO_PREFIX = "wfAddSignInfo_";

    // ==================== 审批人变量 ====================
    public static final String VAR_APPROVERS = "wfApprovers";

    // ==================== 按节点指定审批人 ====================
    public static final String VAR_FIRST_APPROVERS = "firstApprovers";
    public static final String VAR_SECOND_APPROVERS = "secondApprovers";
    public static final String VAR_APPROVER = "wfApprover";
    public static final String VAR_APPLICANT_ID = "applicant_id";
    public static final String VAR_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String VAR_NODE_ID = "wfNodeId";
    public static final String VAR_NODE_NAME = "wfNodeName";
    public static final String VAR_NODE_TYPE = "wfNodeType";

    // ==================== 评论变量 ====================
    public static final String COMMENT_APPROVE = "approve";
    public static final String COMMENT_REJECT = "reject";
    public static final String COMMENT_REJECT_TO = "rejectTo";
    public static final String COMMENT_WITHDRAW = "withdraw";

    // ==================== 多实例系统变量 ====================
    public static final String NR_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
    public static final String NR_OF_INSTANCES = "nrOfInstances";
    public static final String NR_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
}
