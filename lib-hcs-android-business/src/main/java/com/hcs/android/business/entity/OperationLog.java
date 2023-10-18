package com.hcs.android.business.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 处理日志
 */
@Entity
public class OperationLog {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 呼叫类型
     */
    @ColumnInfo(name = "type")
    private Integer type;
    public Integer getType(){
        return type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    /**
     * 呼叫状态
     */
    @ColumnInfo(name = "state")
    private Integer state;
    public Integer getState(){
        return state;
    }
    public void setState(Integer state){
        this.state = state;
    }

    /**
     * 主叫号码
     */
    @ColumnInfo(name = "caller")
    private String caller;
    public String getCaller() {
        return caller;
    }
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /**
     * 主叫位置名称
     */
    @ColumnInfo(name = "caller_place_name")
    private String callerPlaceName;
    public String getCallerPlaceName() {
        return callerPlaceName;
    }
    public void setCallerPlaceName(String callerPlaceName) {
        this.callerPlaceName = callerPlaceName;
    }

    /**
     * 主叫设备序列号
     */
    @ColumnInfo(name = "caller_device_id")
    private String callerDeviceId;
    public String getCallerDeviceId() {
        return callerDeviceId;
    }
    public void setCallerDeviceId(String callerDeviceId) {
        this.callerDeviceId = callerDeviceId;
    }
    /**
     * 主叫设备类型
     */
    @ColumnInfo(name = "caller_type")
    private int callerType;
    public int getCallerType() {
        return callerType;
    }
    public void setCallerType(int callerType) {
        this.callerType = callerType;
    }

    /**
     * 被叫号码
     */
    @ColumnInfo(name = "callee")
    private String callee;
    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    /**
     * 被叫叫位置名称
     */
    @ColumnInfo(name = "callee_place_name")
    private String calleePlaceName;
    public String getCalleePlaceName() {
        return calleePlaceName;
    }
    public void setCalleePlaceName(String calleePlaceName) {
        this.calleePlaceName = calleePlaceName;
    }

    /**
     * 被叫设备序列号
     */
    @ColumnInfo(name = "callee_device_id")
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
    @ColumnInfo(name = "callee_type")
    private int calleeType;
    public int getCalleeType() {
        return calleeType;
    }
    public void setCalleeType(int calleeType) {
        this.calleeType = calleeType;
    }
    /**
     * 呼叫原因
     */
    @ColumnInfo(name = "cause")
    private String cause;
    public String getCause() {
        return cause;
    }
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * 呼叫结果
     */
    @ColumnInfo(name = "result")
    private String result;
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 是否紧急呼叫
     */
    @ColumnInfo(name = "emergency")
    private Integer emergency;
    public Integer getEmergency() {
        return emergency;
    }
    public void setEmergency(Integer emergency) {
        this.emergency = emergency;
    }

    /**
     * 记录插入时间
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
     * 接通时间
     */
    @ColumnInfo(name = "connect_time")
    private Long connectTime;
    public Long getConnectTime(){
        return connectTime;
    }
    public void setConnectTime(Long connectTime){
        this.connectTime = connectTime;
    }

    /**
     * 附加文件路径
     * 录音、录像
     */
    @ColumnInfo(name = "append_path")
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
    @ColumnInfo(name = "call_ref")
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
    @ColumnInfo(name = "start_time")
    private Long startTime;
    public Long getStartTime(){
        return startTime;
    }
    public void setStartTime(Long startTime){
        this.startTime = startTime;
    }

    /**
     * 结束时间
     */
    @ColumnInfo(name = "stop_time")
    private Long stopTime;
    public Long getStopTime(){
        return stopTime;
    }
    public void setStopTime(Long stopTime){
        this.stopTime = stopTime;
    }
}
