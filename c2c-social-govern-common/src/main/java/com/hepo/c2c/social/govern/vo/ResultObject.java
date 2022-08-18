package com.hepo.c2c.social.govern.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResultObject<T> {

    private Integer code;
    private String msg;
    private T data;

    enum ResultObjectCodeEnum {


        SUCCESS(1, "成功"),
        FAIL(2, "失败");;

        ResultObjectCodeEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private Integer code;
        private String msg;
    }

    public ResultObject(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultObject(T data) {
        this.data = data;
    }

    public static <T> ResultObject<T> success(T data) {
        return new ResultObject(data);
    }

    public static ResultObject<Void> success() {
        return new ResultObject(ResultObjectCodeEnum.SUCCESS);
    }

    public static ResultObject<Void> success(Integer code, String msg) {
        return new ResultObject(code, msg);
    }

    public static <T> ResultObject<T> error(ResultObjectCodeEnum ResultObjectCodeEnum) {
        return new ResultObject(ResultObjectCodeEnum);
    }

    public static <T> ResultObject<T> error(Integer code, String msg) {
        return new ResultObject(code, msg);
    }
}
