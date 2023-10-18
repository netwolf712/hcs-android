package com.hcs.android.client.constant;

/**
 * http 请求返回码状态类
 */
public class HttpConstants {
    //请求成功
    public final static int HTTP_OK = 200;
    //授权码不正确或者已经失效
    public final static int UNAUTHORIZED = 401;
    //服务端错误
    public final static int ERROR = 500;
    //没有权限返回
    public final static int FORBIDDEN = 403;
    //访问的资源找不到
    public final static int NOT_FOUND = 404;
}
