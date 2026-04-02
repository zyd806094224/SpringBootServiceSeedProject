package com.zyd.springbootserviceseedproject.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批流任务状态枚举
 */
@Getter
@AllArgsConstructor
public enum WorkFlowTaskStatusEnums {
    PENDING(1, "审批中"),
    PASSED(2, "审批通过"),
    REJECT(3, "已驳回"),
    WITHDRAW(4, "已撤回"),
    AUTO_PASS(5, "自动通过"),
    AUTO_REJECT(6, "自动驳回"),
    ;

    private final Integer code;
    private final String desc;

    public static WorkFlowTaskStatusEnums getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WorkFlowTaskStatusEnums item : WorkFlowTaskStatusEnums.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public boolean isFinal() {
        return this != PENDING;
    }

    public boolean isPassedType() {
        return this == PASSED || this == AUTO_PASS;
    }

    public boolean isRejectType() {
        return this == REJECT || this == AUTO_REJECT;
    }

    public boolean isAutoType() {
        return this == AUTO_PASS || this == AUTO_REJECT;
    }
}
