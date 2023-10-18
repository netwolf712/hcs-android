package com.hcs.android.maintain.entity;

import com.hcs.android.common.network.NetConfig;

/**
 * 设备基本信息
 */
public class DeviceBaseInfo {
    /**
     * 产品名称
     */
    private String productName;

    /**
     * 设备序列号
     */
    private String deviceId;

    /**
     * 系统版本号
     */
    private String systemVersion;

    /**
     * 设备型号
     */
    private String module;

    /**
     * 内核版本号
     */
    private String kernelVersion;

    /**
     * uboot版本号
     */
    private String ubootVersion;

    /**
     * 网络配置
     */
    private NetConfig netConfig;

    /**
     * app运行时间
     * 单位：毫秒
     */
    private Long appRunTime;

    /**
     * 系统运行时间
     * 单位：毫秒
     */
    private Long systemRunTime;

    /**
     * 时间配置
     */
    private TimeConfig timeConfig;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getUbootVersion() {
        return ubootVersion;
    }

    public void setUbootVersion(String ubootVersion) {
        this.ubootVersion = ubootVersion;
    }

    public NetConfig getNetConfig() {
        return netConfig;
    }

    public void setNetConfig(NetConfig netConfig) {
        this.netConfig = netConfig;
    }

    public Long getAppRunTime() {
        return appRunTime;
    }

    public void setAppRunTime(Long appRunTime) {
        this.appRunTime = appRunTime;
    }

    public Long getSystemRunTime() {
        return systemRunTime;
    }

    public void setSystemRunTime(Long systemRunTime) {
        this.systemRunTime = systemRunTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public TimeConfig getTimeConfig() {
        return timeConfig;
    }

    public void setTimeConfig(TimeConfig timeConfig) {
        this.timeConfig = timeConfig;
    }
}
