package com.hcs.android.business.entity;

/**
 * 登录成功后的回复
 */
public class ResponseLogin {
    /**
     * 访问令牌
     */
    private String token;
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    /**
     * 证书
     */
    private String certificate;
    public String getCertificate(){
        return certificate;
    }
    public void setCertificate(String certificate){
        this.certificate = certificate;
    }
}
