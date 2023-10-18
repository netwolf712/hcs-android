package com.hcs.android.business.entity;

/**
 * 操作日志，方便显示
 */
public class OperationLogModel {
    /**
     * 操作日志实体
     */
    private OperationLog operationLog;

    public OperationLog getOperationLog() {
        return operationLog;
    }

    public void setOperationLog(OperationLog operationLog) {
        this.operationLog = operationLog;
    }
    /**
     * 远端设备
     */
    private DeviceModel remoteDevice;

    public DeviceModel getRemoteDevice() {
        return remoteDevice;
    }

    public void setRemoteDevice(DeviceModel remoteDevice) {
        this.remoteDevice = remoteDevice;
    }
}
