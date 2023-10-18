package com.hcs.android.business.entity;

/**
 * 托管请求
 */
public class RequestTrust {
    /**
     * 发送托管请求的主机号码
     */
    private String beTrustNo;

    /**
     * 请求接收托管的主机号码
     */
    private String trustNo;

    /**
     * 托管状态
     * 0取消托管
     * 1请求托管
     */
    private int state;

    /**
     * 是否启用自动托管
     */
    private boolean autoTrust;

    public String getTrustNo() {
        return trustNo;
    }

    public void setTrustNo(String trustNo) {
        this.trustNo = trustNo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getBeTrustNo() {
        return beTrustNo;
    }

    public void setBeTrustNo(String beTrustNo) {
        this.beTrustNo = beTrustNo;
    }

    public boolean isAutoTrust() {
        return autoTrust;
    }

    public void setAutoTrust(boolean autoTrust) {
        this.autoTrust = autoTrust;
    }
}
