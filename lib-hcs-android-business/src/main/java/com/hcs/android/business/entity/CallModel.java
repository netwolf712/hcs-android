package com.hcs.android.business.entity;


import com.alibaba.fastjson.annotation.JSONField;

public class CallModel {
    /**
     * 主叫号码
     * 绑定号码，通过DeviceModel.getBindPhoneNo获取
     */
    private String caller;
    public String getCaller() {
        return caller;
    }
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /**
     * 主叫设备类型
     */
    private int callerType;
    public int getCallerType() {
        return callerType;
    }
    public void setCallerType(int callerType) {
        this.callerType = callerType;
    }

    /**
     * 主叫设备序列号
     */
    private String callerDeviceId;
    public String getCallerDeviceId() {
        return callerDeviceId;
    }
    public void setCallerDeviceId(String callerDeviceId) {
        this.callerDeviceId = callerDeviceId;
    }

    /**
     * 被叫号码
     * 绑定号码，通过DeviceModel.getBindPhoneNo获取
     */
    private String callee;
    public String getCallee() {
        return callee;
    }
    public void setCallee(String callee) {
        this.callee = callee;
    }

    /**
     * 被叫设备序列号
     */
    private String calleeDeviceId;
    public String getCalleeDeviceId() {
        return calleeDeviceId;
    }
    public void setCalleeDeviceId(String calleeDeviceId) {
        this.calleeDeviceId = calleeDeviceId;
    }

    /**
     * 被叫设备类型
     */
    private int calleeType;
    public int getCalleeType() {
        return calleeType;
    }
    public void setCalleeType(int calleeType) {
        this.calleeType = calleeType;
    }

    /**
     * 呼叫类型
     * 对应CallTypeEnum
     */
    private int callType;
    public int getCallType() {
        return callType;
    }
    public void setCallType(int callType) {
        this.callType = callType;
    }


    /**
     * 呼叫原因
     */
    private String cause;
    public String getCause() {
        return cause;
    }
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * 呼叫状态
     */
    private Integer state;
    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 是否紧急呼叫
     */
    private boolean emergencyCall;
    public boolean isEmergencyCall() {
        return emergencyCall;
    }
    public void setEmergencyCall(boolean emergencyCall) {
        this.emergencyCall = emergencyCall;
    }

    /**
     * 呼叫结果
     */
    private String result;
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * 附加文件路径
     * 录音、录像
     */
    private String appendPath;
    public String getAppendPath(){
        return appendPath;
    }
    public void setAppendPath(String appendPath){
        this.appendPath = appendPath;
    }

    /**
     * SIP呼叫的索引
     */
    private String callRef;
    public String getCallRef(){
        return callRef;
    }
    public void setCallRef(String callRef){
        this.callRef = callRef;
    }

    /**
     * 开始时间
     */
    private Long startTime;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * 结束时间
     */
    private Long stopTime;

    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    /**
     * 接通时间
     */
    private Long connectTime;

    public Long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Long connectTime) {
        this.connectTime = connectTime;
    }

    /**
     * 是否带视频
     */
    private boolean withVideo;

    public boolean isWithVideo() {
        return withVideo;
    }

    public void setWithVideo(boolean withVideo) {
        this.withVideo = withVideo;
    }

    /**
     * 远端设备
     */
    @JSONField(serialize = false)
    private DeviceModel remoteDevice;

    public DeviceModel getRemoteDevice() {
        return remoteDevice;
    }

    public void setRemoteDevice(DeviceModel remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    /**
     * 远端设备所在的位置
     */
    @JSONField(serialize = false)
    private PlaceModel remotePlace;

    public PlaceModel getRemotePlace() {
        return remotePlace;
    }

    public void setRemotePlace(PlaceModel remotePlace) {
        this.remotePlace = remotePlace;
    }
}
