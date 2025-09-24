package com.zyd.springbootserviceseedproject.common;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String msg;
    private Long total;
    private Object data;

    public static Result fail() {
        return result(400, "失败", 0L, null);
    }

    public static Result fail(int code, String msg) {
        return result(code, msg, 0L, null);
    }

    public static Result success() {
        return result(200, "成功", 0L, null);
    }

    public static Result success(Object data) {
        return result(200, "成功", 0L, data);
    }

    public static Result success(Object data, Long total) {
        return result(200, "成功", total, data);
    }

    private static Result result(int code, String msg, Long total, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setTotal(total);
        result.setData(data);
        return result;
    }
}
