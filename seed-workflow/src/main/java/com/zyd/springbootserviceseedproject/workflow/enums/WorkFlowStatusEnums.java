package com.zyd.springbootserviceseedproject.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批流状态枚举
 */
@Getter
@AllArgsConstructor
public enum WorkFlowStatusEnums {
    PENDING(1, "审批中"),
    PASSED(2, "审批通过"),
    REJECT(3, "已驳回"),
    WITHDRAW(4, "已撤回"),
    ;

    private final Integer code;
    private final String desc;

    public static WorkFlowStatusEnums getByCode(Integer code) {
        for (WorkFlowStatusEnums item : WorkFlowStatusEnums.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
