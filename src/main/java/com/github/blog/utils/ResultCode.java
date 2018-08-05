package com.github.blog.utils;

public class ResultCode {
    public static final int SUCCESS = 0;
    public static final int SERVER_ERROR = 110;  //服务端异常
    public static final int AUTH_FAIL = 120;     //身份验证异常
    public static final int ACL_FAIL = 130;       //权限不足
    public static final int INVALID_PARAM = 140;     //参数错误
    public static final int INVALID_VER = 150;     //版本错误
    public static final int LOGICAL_ERROR = 160;     //业务逻辑异常
    public static final int SYS_SUSPEND = 170;     //系统维护中
    public static final int BIZ_OFFLINE = 180;     //业务下线
    public static final int INTERVAL_ERROR=999;    //服务端未知错误
}
