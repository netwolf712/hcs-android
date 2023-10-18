package com.hcs.android.maintain.entity;

/**
 * 请求时用于验证的内容
 */
public class RequestConfirm {
    /**
     * 登录时的token，不带Bearer字段
     */
    private String token;

    /**
     * 密码
     */
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
