package com.hcs.android.business.entity;

public class RequestBedDetail {
    /**
     * 管理这张床的主机id
     */
    private String deviceId;
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 床序号
     */
    private Integer bedSn;
    public Integer getBedSn() {
        return bedSn;
    }
    public void setBedSn(Integer bedSn) {
        this.bedSn = bedSn;
    }
}
