package com.hcs.android.business.entity;

/**
 * 请求登录到服务器
 */
public class RequestLoginService {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用于服务器访问设备的token
     */
    private String tokenForService;

    public RequestLoginService() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenForService() {
        return tokenForService;
    }

    public void setTokenForService(String tokenForService) {
        this.tokenForService = tokenForService;
    }
}
