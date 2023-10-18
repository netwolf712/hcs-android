package com.hcs.android.business.entity;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.service.DeviceService;
import com.hcs.android.common.util.log.KLog;

import java.util.ArrayList;

/**
 * 请求参数
 */
public class RequestDTO<T> {
    public RequestDTO(){

    }
    /**
     * 根据deviceModel创建请求头
     */
    public RequestDTO(@NonNull DeviceModel deviceModel, String commandId){
        setCommandId(commandId);
        setDeviceId(deviceModel.getDevice().getDeviceId());
        setDeviceType(String.valueOf(deviceModel.getDevice().getDeviceType()));
        setIpAddress(deviceModel.getDevice().getIpAddress());
        setMacAddress(deviceModel.getDevice().getMacAddress());
        setPhoneNo(deviceModel.getDevice().getPhoneNo());
        setParentNo(deviceModel.getDevice().getParentNo());
    }

    /**
     * 为带计数功能的请求提供封装
     * @param fromDevice 数据发送方
     * @param toDevice 数据接收方
     * @param commandEnum 命令
     */
    public RequestDTO(@NonNull DeviceModel fromDevice,@NonNull DeviceModel toDevice, @NonNull CommandEnum commandEnum){
        setCommandId(commandEnum.getId());
        setDeviceId(fromDevice.getDevice().getDeviceId());
        setDeviceType(String.valueOf(fromDevice.getDevice().getDeviceType()));
        setIpAddress(fromDevice.getDevice().getIpAddress());
        setMacAddress(fromDevice.getDevice().getMacAddress());
        setPhoneNo(fromDevice.getDevice().getPhoneNo());
        setParentNo(fromDevice.getDevice().getParentNo());
        //当是数据命令时自动计算命令索引
        if(commandEnum.getType() == Constant.COMMAND_TYPE_DATA){
            if(toDevice.getDevice().getLastDataCommandIndex() == null){
                KLog.e("===============> deviceId " + toDevice.getDevice().getDeviceId() + " last data command index = 0 <=============");
                toDevice.getDevice().setLastDataCommandIndex(0L);
            }else{
                toDevice.getDevice().setLastDataCommandIndex(toDevice.getDevice().getLastDataCommandIndex() + 1);
            }
            setDataCommandIndex(toDevice.getDevice().getLastDataCommandIndex());
            DeviceService.getInstance().updateDevice(toDevice);
        }
    }
    /**
     * 消息发送方的设备类型，0服务器，1护士站主机，2床旁分机，3病房门口机，4病区门口机，5病员信息一览表
     */
    private String deviceType;
    public String getDeviceType(){
        return deviceType;
    }
    public void setDeviceType(String deviceType){
        this.deviceType = deviceType;
    }

    /**
     * 消息发送方的设备序列号
     */
    private String deviceId;
    public String getDeviceId(){
        return deviceId;
    }
    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    /**
     * 主机号码，服务器时此信息可为空，主机时此信息为设备本身号码
     */
    private String parentNo;
    public String getParentNo(){
        return parentNo;
    }
    public void setParentNo(String parentNo){
        this.parentNo = parentNo;
    }

    /**
     * 自己的号码，服务器时此信息可为空，主机时此信息为设备本身号码
     */
    private String phoneNo;
    public String getPhoneNo(){
        return phoneNo;
    }
    public void setPhoneNo(String phoneNo){
        this.phoneNo = phoneNo;
    }

    /**
     * 命令id，详见《护理对讲系统命令列表》
     */
    private String commandId;
    public String getCommandId(){
        return commandId;
    }
    public void setCommandId(String commandId){
        this.commandId = commandId;
    }

    /**
     * 消息发送方的IP地址
     */
    private String ipAddress;
    public String getIpAddress(){
        return ipAddress;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    /**
     * 消息发送方的MAC地址
     */
    private String macAddress;
    public String getMacAddress(){
        return macAddress;
    }
    public void setMacAddress(String macAddress){
        this.macAddress = macAddress;
    }

    /**
     * 详细数据内容
     * 根据不同的信令需要进行定义
     */
    private T data;
    public T getData(){
        return data;
    }
    public void setData(T data){
        this.data = data;
    }


    /**
     * 通过eventbus处理内部请求时作为异步返回的tag
     */
    private String eventBusId;
    public String getEventBusId(){
        return eventBusId;
    }
    public void setEventBusId(String eventBusId){
        this.eventBusId = eventBusId;
    }

    /**
     * 数据命令索引
     * 主机发送给分机的数据更新命令才有用
     * 其它命令忽略
     */
    private Long dataCommandIndex;

    public Long getDataCommandIndex() {
        return dataCommandIndex;
    }

    public void setDataCommandIndex(Long dataCommandIndex) {
        this.dataCommandIndex = dataCommandIndex;
    }

    /**
     * 数据命令子索引
     * 主机发送给分机的数据更新命令才有用
     * 其它命令忽略
     * （在数据太大分段发送时才需要，此时此值从0开始计数，若无分组，则此值为-1）
     */
//    private Integer dataCommandSubIndex;
//
//    public Integer getDataCommandSubIndex() {
//        return dataCommandSubIndex;
//    }
//
//    public void setDataCommandSubIndex(Integer dataCommandSubIndex) {
//        this.dataCommandSubIndex = dataCommandSubIndex;
//    }

    public DeviceModel asDeviceModel(){
        Device device = new Device();
        device.setDeviceId(getDeviceId());
        device.setDeviceType(Integer.valueOf(getDeviceType()));
        device.setIpAddress(getIpAddress());
        device.setMacAddress(getMacAddress());
        device.setPhoneNo(getPhoneNo());
        device.setParentNo(getParentNo());
        return new DeviceModel(device);
    }
}
