package com.example.group11practice.utils;

public enum ErrorCode {

    // 小于1000的状态码为系统保留状态码
    SC_UNAUTHORIZED(401, "未认证"),
    IP_UNAUTHORIZED(402, "未认证"),

    IS_EXIST(9999,"已存在此记录"),
    NOT_EXIST(9998,"不存在此记录"),


    EMPTY_PARAM(1000, "参数不可为空"),
    INVALID_PARAM(1001, "参数非法"),
    INVALID_STATE(1002, "内部状态异常"),
    NOT_SUPPORTED_OPERATION(1003, "尚未支持该操作"),
    EMPTY_RESULT(1003, "查询结果为空"),
    USER_LOGINNAME_PARAM(1004,"用户登录异常"),
    RECORD_EXISTED(1005, "记录已存在"),
    USER_PASSWORD_EXPIRED(1006,"用户密码已过期"),

    JOB_EXECUTION_ERROR(2000, "定时任务执行异常"),
    KAPTCHA_VALID_ERROR(2001, "验证码校验异常"),
    JSON_PARSE_ERROR(2002, "JSON解析出错"),
    CRYPT_ERROR(2003, "加密解密出错"),
    DATE_PARSE_ERROR(2004, "日期解析出错"),


    COMMON_ERROR(0001,"通用错误"),
    FILE_UPLOAD_FAIL(7000,"文件上传失败");

    private int code;
    private String msg;

    private ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
