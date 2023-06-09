package com.rqs.common.result;

import lombok.Getter;

/**
 * 统一结果返回枚举类
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
//    SERVICE_ERROR(2012, "服务异常"),
//    DATA_ERROR(204, "数据异常"),
//
    LOGIN_ERROR(204, "认证失败"),
//    PERMISSION(209, "没有权限")
    ;
    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
