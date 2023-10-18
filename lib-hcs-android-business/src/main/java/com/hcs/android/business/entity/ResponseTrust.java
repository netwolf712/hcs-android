package com.hcs.android.business.entity;

/**
 * 托管请求回复
 */
public class ResponseTrust {
    /**
     * 是否答应请求
     */
    private boolean ok;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    /**
     * 发送托管请求的主机号码
     */
    private String beTrustNo;

    /**
     * 请求接收托管的主机号码
     */
    private String trustNo;

    public String getBeTrustNo() {
        return beTrustNo;
    }

    public void setBeTrustNo(String beTrustNo) {
        this.beTrustNo = beTrustNo;
    }

    public String getTrustNo() {
        return trustNo;
    }

    public void setTrustNo(String trustNo) {
        this.trustNo = trustNo;
    }

    /**
     * 托管状态
     * 0取消托管
     * 1请求托管
     */
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
