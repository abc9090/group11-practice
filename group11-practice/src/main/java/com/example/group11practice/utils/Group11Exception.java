package com.example.group11practice.utils;

import java.io.Serializable;


public class Group11Exception extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public Group11Exception() {

    }

    public Group11Exception(ErrorCode ec) {
        this(ec, ec.getMsg(), null);
    }

    public Group11Exception(ErrorCode ec, String msg) {
        this(ec, msg == null ? ec.getMsg() : msg, null);
    }

    public Group11Exception(ErrorCode ec, Throwable e) {
        this(ec, ec.getMsg(), e);
    }

    public Group11Exception(ErrorCode ec, String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = ec.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static Group11Exception emptyArgument(String msg) {
        return new Group11Exception(ErrorCode.EMPTY_PARAM, msg);
    }

    public static Group11Exception illegalArgument(String msg) {
        return new Group11Exception(ErrorCode.INVALID_PARAM, msg);
    }

    public static Group11Exception userLoginArgument(String msg) {
        return new Group11Exception(ErrorCode.USER_LOGINNAME_PARAM, msg);
    }

    public static Group11Exception recordNotExist(String msg) {
        return new Group11Exception(ErrorCode.NOT_EXIST, msg);
    }

    public static Group11Exception illegalState(String msg, Throwable ex) {
        return new Group11Exception(ErrorCode.INVALID_STATE, msg, ex);
    }

    public static Group11Exception jsonParseException(String msg, Throwable ex) {
        return new Group11Exception(ErrorCode.JSON_PARSE_ERROR, msg, ex);
    }

    public static Group11Exception notSupportedOperation(String msg) {
        return new Group11Exception(ErrorCode.NOT_SUPPORTED_OPERATION, msg);
    }

    public static Group11Exception cryptException(String msg, Throwable ex) {
        return new Group11Exception(ErrorCode.CRYPT_ERROR, msg, ex);
    }

    public static Group11Exception dateParseException(String msg, Throwable ex) {
        return new Group11Exception(ErrorCode.DATE_PARSE_ERROR, msg, ex);
    }

    public static Group11Exception recordExisted(String msg) {
        return new Group11Exception(ErrorCode.RECORD_EXISTED, msg, null);
    }

    public static Group11Exception unauthorized(String msg) {
        return new Group11Exception(ErrorCode.SC_UNAUTHORIZED, msg);
    }

}
