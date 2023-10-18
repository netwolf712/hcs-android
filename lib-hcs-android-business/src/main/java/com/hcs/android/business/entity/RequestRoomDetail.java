package com.hcs.android.business.entity;

public class RequestRoomDetail {
    /**
     * 管理这病房的主机id
     */
    private String deviceId;
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 病房序号
     */
    private Integer roomSn;
    public Integer getRoomSn() {
        return roomSn;
    }
    public void setRoomSn(Integer roomSn) {
        this.roomSn = roomSn;
    }
}
