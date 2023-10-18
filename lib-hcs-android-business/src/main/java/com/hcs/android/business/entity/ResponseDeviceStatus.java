package com.hcs.android.business.entity;

/**
 * 设备状态回复
 */
public class ResponseDeviceStatus {
    /**
     * 托管状态
     */
    private int trustState;

    /**
     * 工作状态
     */
    private int workState;

    /**
     * 是否使能了自动托管工作
     */
    private boolean autoTrust;

    public int getTrustState() {
        return trustState;
    }

    public void setTrustState(int trustState) {
        this.trustState = trustState;
    }

    public int getWorkState() {
        return workState;
    }

    public void setWorkState(int workState) {
        this.workState = workState;
    }

    public boolean isAutoTrust() {
        return autoTrust;
    }

    public void setAutoTrust(boolean autoTrust) {
        this.autoTrust = autoTrust;
    }
}
