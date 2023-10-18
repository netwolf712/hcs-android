package com.hcs.calldemo.entity;

import androidx.annotation.NonNull;

import com.hcs.android.call.util.LinphoneUtils;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.DateUtil;

import org.linphone.core.CallLog;

import java.util.Date;

/**
 * 呼叫日志
 */
public class CallLogBo {
    /**
     * 呼叫id
     */
    private String callId;
    public String getCallId(){
        return callId;
    }
    public void setCallId(String callId){
        this.callId = callId;
    }

    /**
     * 呼叫索引
     */
    private String callRef;
    public String getCallRef(){
        return callRef;
    }
    public void setCallRef(String callRef){
        this.callRef = callRef;
    }

    /**
     * 呼叫方向，对enum Dir的文字翻译
     */
    private String callDir;
    public String getCallDir(){
        return callDir;
    }
    public void setCallDir(String callDir){
        this.callDir = callDir;
    }

    /**
     * 呼叫状态，对enum Status的文字翻译
     */
    private String callStatus;
    public String getCallStatus(){
        return callStatus;
    }
    public void setCallStatus(String callStatus){
        this.callStatus = callStatus;
    }

    /**
     * 本端地址
     */
    public String localAddress;
    public String getLocalAddress(){
        return localAddress;
    }
    public void setLocalAddress(String localAddress){
        this.localAddress = localAddress;
    }

    /**
     * 远端地址
     */
    public String remoteAddress;
    public String getRemoteAddress(){
        return remoteAddress;
    }
    public void setRemoteAddress(String remoteAddress){
        this.remoteAddress = remoteAddress;
    }

    /**
     * 呼叫时长
     * HH:mm:ss
     */
    public String duration;
    public String getDuration(){
        return duration;
    }
    public void setDuration(String duration){
        this.duration = duration;
    }

    /**
     * 开始时间
     * yyyy-MM-dd HH:mm:ss
     */
    public String startTime;
    public String getStartTime(){
        return startTime;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    /**
     * 错误信息
     */
    public String errorInfo;
    public String getErrorInfo(){
        return errorInfo;
    }
    public void setErrorInfo(String errorInfo){
        this.errorInfo = errorInfo;
    }

    public CallLogBo(){

    }
    public CallLogBo(@NonNull CallLog callLog){
        this.callId = callLog.getCallId();
        this.callRef = callLog.getRefKey();
        this.callDir = LinphoneUtils.convertCallDirToString(BaseApplication.getAppContext(),callLog.getDir());
        this.callStatus = LinphoneUtils.convertCallStatusToString(BaseApplication.getAppContext(),callLog.getStatus());
        this.localAddress = callLog.getLocalAddress().asString();
        this.remoteAddress = callLog.getRemoteAddress().asString();
        this.errorInfo = callLog.getErrorInfo() == null ? "" : callLog.getErrorInfo().getPhrase();
        this.duration = DateUtil.formatSecondToHourMinuteSecond(callLog.getDuration());
        this.startTime = DateUtil.formatDate(new Date(callLog.getStartDate() * 1000),DateUtil.FormatType.yyyyMMddHHmmss);
    }
}
