package com.zyd.springbootserviceseedproject.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工作流操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum WorkFlowOperateTypeEnums {
    APPLY(1, "发起申请"),
    PASSED(2, "审批通过"),
    REJECT(3, "驳回"),
    REJECT_TO(4, "驳回至"),
    WITH_DRAW(5, "撤回"),
    ;

    private final Integer code;
    private final String desc;

    public static WorkFlowOperateTypeEnums determineOptType(WorkFlowTaskStatusEnums status) {
        return switch (status) {
            case PASSED, AUTO_PASS -> WorkFlowOperateTypeEnums.PASSED;
            case REJECT, AUTO_REJECT -> WorkFlowOperateTypeEnums.REJECT;
            case WITHDRAW -> WorkFlowOperateTypeEnums.WITH_DRAW;
            default -> WorkFlowOperateTypeEnums.PASSED;
        };
    }
}
