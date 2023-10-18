package com.hcs.android.business.entity;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.databinding.library.baseAdapters.BR;
@Entity
public class Device extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }
    /**
     * 设备类型
     * @see com.hcs.android.business.constant.DeviceTypeEnum
     */
    @ColumnInfo(name = "device_type")
    private Integer deviceType;
    public Integer getDeviceType(){
        return deviceType;
    }
    public void setDeviceType(Integer deviceType){
        this.deviceType = deviceType;
    }

    /**
     * 设备序列号
     */
    @ColumnInfo(name = "device_id",index = true)
    private String deviceId;
    public String getDeviceId(){
        return deviceId;
    }
    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    /**
     * 此设备的直属主机号码
     */
    @ColumnInfo(name = "parent_number")
    private String parentNo;
    @Bindable
    public String getParentNo(){
        return parentNo;
    }
    public void setParentNo(String parentNo){
        this.parentNo = parentNo;
        notifyPropertyChanged(BR.parentNo);

    }

    /**
     * 此设备的自身号码
     */
    @ColumnInfo(name = "phone_number")
    private String phoneNo;
    @Bindable
    public String getPhoneNo(){
        return phoneNo;
    }
    public void setPhoneNo(String phoneNo){
        this.phoneNo = phoneNo;
        notifyPropertyChanged(BR.phoneNo);
    }

    /**
     * IP地址
     */
    @ColumnInfo(name = "ip_address")
    private String ipAddress;
    @Bindable
    public String getIpAddress(){
        return ipAddress;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
        notifyPropertyChanged(BR.ipAddress);
    }

    /**
     * MAC地址
     */
    @ColumnInfo(name = "mac_address")
    private String macAddress;
    @Bindable
    public String getMacAddress(){
        return macAddress;
    }
    public void setMacAddress(String macAddress){
        this.macAddress = macAddress;
        notifyPropertyChanged(BR.macAddress);
    }

    /**
     * 设备型号
     */
    @ColumnInfo(name = "module")
    private String module;
    @Bindable
    public String getModule(){
        return module;
    }
    public void setModule(String module){
        this.module = module;
        notifyPropertyChanged(BR.module);
    }

    /**
     * 内核版本号
     */
    @ColumnInfo(name = "kernel_version")
    private String kernelVersion;
    @Bindable
    public String getKernelVersion(){
        return kernelVersion;
    }
    public void setKernelVersion(String kernelVersion){
        this.kernelVersion = kernelVersion;
        notifyPropertyChanged(BR.kernelVersion);
    }

    /**
     * 系统版本号
     */
    @ColumnInfo(name = "system_version")
    private String systemVersion;
    @Bindable
    public String getSystemVersion(){
        return systemVersion;
    }
    public void setSystemVersion(String systemVersion){
        this.systemVersion = systemVersion;
        notifyPropertyChanged(BR.systemVersion);
    }

    /**
     * uboot版本号
     */
    @ColumnInfo(name = "uboot_version")
    private String ubootVersion;
    @Bindable
    public String getUbootVersion(){
        return ubootVersion;
    }
    public void setUbootVersion(String ubootVersion){
        this.ubootVersion = ubootVersion;
        notifyPropertyChanged(BR.ubootVersion);
    }

    /**
     * 软件版本号
     */
    @ColumnInfo(name = "software_version")
    private String softwareVersion;
    @Bindable
    public String getSoftwareVersion(){
        return softwareVersion;
    }
    public void setSoftwareVersion(String softwareVersion){
        this.softwareVersion = softwareVersion;
        notifyPropertyChanged(BR.softwareVersion);
    }

    /**
     * 硬件版本号
     */
    @ColumnInfo(name = "hardware_version")
    private String hardwareVersion;
    @Bindable
    public String getHardwareVersion(){
        return hardwareVersion;
    }
    public void setHardwareVersion(String hardwareVersion){
        this.hardwareVersion = hardwareVersion;
        notifyPropertyChanged(BR.hardwareVersion);
    }

    /**
     * 设备名称
     */
    @ColumnInfo(name = "device_name")
    private String deviceName;
    @Bindable
    public String getDeviceName(){
        return deviceName;
    }
    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
        notifyPropertyChanged(BR.deviceName);
    }

    /**
     * 升级时间
     */
    @ColumnInfo(name = "upgrade_time")
    private Long upgradeTime;
    public Long getUpgradeTime(){
        return upgradeTime;
    }
    public void setUpgradeTime(Long upgradeTime){
        this.upgradeTime = upgradeTime;
    }

    /**
     * 配置id
     */
    @ColumnInfo(name = "template_config_id")
    private String templateConfigId;
    public String getTemplateConfigId(){
        return templateConfigId;
    }
    public void setTemplateConfigId(String templateConfigId){
        this.templateConfigId = templateConfigId;
    }

    /**
     * 模板id
     */
    @ColumnInfo(name = "template_id")
    private String templateId;
    public String getTemplateId(){
        return templateId;
    }
    public void setTemplateId(String templateId){
        this.templateId = templateId;
    }

    /**
     * 信息更新时间
     * 此可以作为设备信息是否需要更新的判断标准
     */
    @ColumnInfo(name = "update_time")
    private Long updateTime;
    public Long getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Long updateTime){
        this.updateTime = updateTime;
    }

    /**
     * 最后一次心跳的时间
     * 作为设备是否在线的判断标准
     */
    @ColumnInfo(name = "heart_beat_time")
    private Long heartBeatTime;
    public Long getHeartBeatTime(){
        if(heartBeatTime == null){
            heartBeatTime = System.currentTimeMillis();
        }
        return heartBeatTime;
    }
    public void setHeartBeatTime(Long heartBeatTime){
        this.heartBeatTime = heartBeatTime;
    }

    /**
     * 全局标题
     */
    @ColumnInfo(name = "whole_title")
    private String wholeTitle;
    @Bindable
    public String getWholeTitle(){
        return wholeTitle;
    }
    public void setWholeTitle(String wholeTitle){
        this.wholeTitle = wholeTitle;
        notifyPropertyChanged(BR.wholeTitle);
    }

    /**
     * 设备所属的位置
     */
    @ColumnInfo(name = "place_uid")
    private String placeUid;
    @Bindable
    public String getPlaceUid(){
        return placeUid;
    }
    public void setPlaceUid(String placeUid){
        this.placeUid = placeUid;
        notifyPropertyChanged(BR.placeUid);
    }

    /**
     * 最后一套数据命令索引
     */
    @ColumnInfo(name = "last_data_command_index")
    private Long lastDataCommandIndex;
    public Long getLastDataCommandIndex(){
        return lastDataCommandIndex;
    }
    public void setLastDataCommandIndex(Long lastDataCommandIndex){
        this.lastDataCommandIndex = lastDataCommandIndex;
    }

    public void copy(@NonNull Device device){
        setUid(device.getUid());
        setDeviceType(device.getDeviceType());
        setDeviceId(device.getDeviceId());
        setIpAddress(device.getIpAddress());
        setModule(device.getModule());
        setMacAddress(device.getMacAddress());
        setPhoneNo(device.getPhoneNo());
        setParentNo(device.getParentNo());
        setKernelVersion(device.getKernelVersion());
        setSystemVersion(device.getSystemVersion());
        setUbootVersion(device.getUbootVersion());
        setSoftwareVersion(device.getSoftwareVersion());
        setHardwareVersion(device.getHardwareVersion());
        setDeviceName(device.getDeviceName());
        setUpgradeTime(device.getUpgradeTime());
        setTemplateConfigId(device.getTemplateConfigId());
        setTemplateId(device.getTemplateId());
        setUpdateTime(device.getUpdateTime());
        setHeartBeatTime(device.getHeartBeatTime());
        setWholeTitle(device.getWholeTitle());
    }
}
