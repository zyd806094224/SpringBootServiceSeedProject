package com.zyd.springbootserviceseedproject.im.enums;

/**
 * IM 消息类型
 *
 * @author zhaoyudong
 */
public enum ImMsgType {

    /** 文本 */
    TEXT(1),
    /** 图片 */
    IMAGE(2);

    private final int value;

    ImMsgType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImMsgType of(Integer value) {
        if (value == null) {
            return TEXT;
        }
        for (ImMsgType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return TEXT;
    }
}
