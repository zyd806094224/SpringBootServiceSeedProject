package com.zyd.springbootserviceseedproject.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工作流节点类型枚举
 */
@Getter
@AllArgsConstructor
public enum WorkFlowNodeTypeEnums {
    NONE(0, "无"),
    SINGLE(1, "单人审批"),
    SEQUENTIAL_SIGN(2, "顺序会签"),
    PARALLEL_ALL_SIGN(3, "并行会签"),
    OR_SIGN(4, "或签"),
    NOTIFY(5, "知会"),
    ADD_SIGN(6, "加签"),
    ;

    private final Integer code;
    private final String desc;

    public static final WorkFlowNodeTypeEnums COUNTER_SIGN = PARALLEL_ALL_SIGN;

    public static WorkFlowNodeTypeEnums getByCode(Integer code) {
        if (code == null) {
            return NONE;
        }
        for (WorkFlowNodeTypeEnums item : WorkFlowNodeTypeEnums.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return NONE;
    }

    public boolean isOrSign() {
        return this == OR_SIGN;
    }

    public boolean isCounterSign() {
        return this == SEQUENTIAL_SIGN || this == PARALLEL_ALL_SIGN;
    }

    public boolean isMultiInstance() {
        return isCounterSign() || isOrSign();
    }

    public boolean isNotify() {
        return this == NOTIFY;
    }

    public boolean isAddSign() {
        return this == ADD_SIGN;
    }
}
