package com.example.group11practice.utils;

import lombok.Data;
import java.io.Serializable;

@Data
public class RestResult<T> implements Serializable {
    private static final String EC_SYSERROR = "500";
    private static final String EC_OK = "200";

    private String code;
    private String msg;
    private T data;

    public static <T> RestResult<T> ok(T data) {
        RestResult<T> ret = new RestResult<>();
        ret.setCode(EC_OK);
        ret.setMsg("OK");
        ret.setData(data);
        return ret;
    }

    public static <T> RestResult<T> fail(String code, String msg) {
        RestResult<T> ret = new RestResult<>();
        ret.setCode(code);
        ret.setMsg(msg);
        return ret;
    }

    public static <T> RestResult<T> fail(String msg) {
        RestResult<T> ret = new RestResult<>();
        ret.setCode(EC_SYSERROR);
        ret.setMsg(msg);
        return ret;
    }

    public static <T> RestResult<T> ok() {
        RestResult<T> ret = new RestResult<>();
        ret.setCode(EC_OK);
        ret.setMsg("OK");
        return ret;
    }

    /**
     * 返回前端服务端时间戳，单位毫秒。
     * @return
     */
    public Long getServerTimestamp() {
        return System.currentTimeMillis();
    }

}
