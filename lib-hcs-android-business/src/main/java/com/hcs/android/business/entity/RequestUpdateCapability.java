package com.hcs.android.business.entity;

/**
 * 请求更新设备参数
 */
public class RequestUpdateCapability {

    /**
     * 节点对应的主机id
     */
    private String masterDeviceId;

    public String getMasterDeviceId() {
        return masterDeviceId;
    }

    public void setMasterDeviceId(String masterDeviceId) {
        this.masterDeviceId = masterDeviceId;
    }

    /**
     * 节点id
     */
    private String placeUid;

    public String getPlaceUid() {
        return placeUid;
    }

    public void setPlaceUid(String placeUid) {
        this.placeUid = placeUid;
    }


    /**
     * 是否有IPC
     */
    private boolean hasIPC;

    /**
     * IPC
     */
    private IPCamera ipCamera;

    public boolean isHasIPC() {
        return hasIPC;
    }

    public void setHasIPC(boolean hasIPC) {
        this.hasIPC = hasIPC;
    }

    public IPCamera getIpCamera() {
        return ipCamera;
    }

    public void setIpCamera(IPCamera ipCamera) {
        this.ipCamera = ipCamera;
    }
}
