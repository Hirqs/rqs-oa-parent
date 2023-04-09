package com.rqs.common.result;

import lombok.Data;

/**
 * 全局统一返回结果类
 *
 */
@Data
public class Result<T> {

    private Integer code;//操作的状态码
    private String message;//返回的信息
    private T data;//返回的具体数据

    //构造私有化，操作该类中的数据需要提供对外可以调用的静态方法
    private Result(){}

    // 封装返回的具体数据
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    //封装返回状态码和信息
    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(data);
        //操作的状态码
        result.setCode(resultCodeEnum.getCode());
        //返回的信息
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    //操作成功，无返回数据
    public static<T> Result<T> ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }

    //操作成功，有返回数据
    public static<T> Result<T> ok(T data){
        return build(data, ResultCodeEnum.SUCCESS);
    }

    //操作失败，无返回数据
    public static<T> Result<T> fail(){
        return build(null,ResultCodeEnum.FAIL);
    }

    //操作失败，有返回数据
    public static<T> Result<T> fail(T data){
        return build(data, ResultCodeEnum.FAIL);
    }

    //重新设置返回的信息
    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    //重新设置返回的状态码
    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}