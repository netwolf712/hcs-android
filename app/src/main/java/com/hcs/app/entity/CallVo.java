package com.hcs.app.entity;

import androidx.databinding.Bindable;

import com.hcs.app.BR;

/**
 * 呼叫实体
 * 包含界面控制部分
 */
public class CallVo extends CallBo {
    /**
     * 是否开始呼叫
     */
    private Boolean canStartCall;
    @Bindable
    public Boolean getCanStartCall(){
        return canStartCall;
    }
    public void setCanStartCall(Boolean canStartCall){
        this.canStartCall = canStartCall;
        notifyPropertyChanged(BR.canStartCall);
    }

    /**
     * 是否停止呼叫
     */
    private Boolean canStopCall;
    @Bindable
    public Boolean getCanStopCall(){
        return canStopCall;
    }
    public void setCanStopCall(Boolean canStopCall){
        this.canStopCall = canStopCall;
        notifyPropertyChanged(BR.canStopCall);
    }

    /**
     * 是否开始视频
     */
    private Boolean canStartVideo;
    @Bindable
    public Boolean getCanStartVideo(){
        return canStartVideo;
    }
    public void setCanStartVideo(Boolean canStartVideo){
        this.canStartVideo = canStartVideo;
        notifyPropertyChanged(BR.canStartVideo);
    }

    /**
     * 是否停止视频
     */
    private Boolean canStopVideo;
    @Bindable
    public Boolean getCanStopVideo(){
        return canStopVideo;
    }
    public void setCanStopVideo(Boolean canStopVideo){
        this.canStopVideo = canStopVideo;
        notifyPropertyChanged(BR.canStopVideo);
    }

    /**
     * 是否暂停呼叫
     */
    private Boolean canPauseCall;
    @Bindable
    public Boolean getCanPauseCall(){
        return canPauseCall;
    }
    public void setCanPauseCall(Boolean canPauseCall){
        this.canPauseCall = canPauseCall;
        notifyPropertyChanged(BR.canPauseCall);
    }

    /**
     * 是否恢复呼叫
     */
    private Boolean canResumeCall;
    @Bindable
    public Boolean getCanResumeCall(){
        return canResumeCall;
    }
    public void setCanResumeCall(Boolean canResumeCall){
        this.canResumeCall = canResumeCall;
        notifyPropertyChanged(BR.canResumeCall);
    }

    /**
     * 是否可以接收呼叫
     */
    private Boolean canAcceptCall;
    @Bindable
    public Boolean getCanAcceptCall(){
        return canAcceptCall;
    }
    public void setCanAcceptCall(Boolean canAcceptCall){
        this.canAcceptCall = canAcceptCall;
        notifyPropertyChanged(BR.canAcceptCall);
    }

    /**
     * 是否可以拒绝呼叫
     */
    private Boolean canRejectCall;
    @Bindable
    public Boolean getCanRejectCall(){
        return canRejectCall;
    }
    public void setCanRejectCall(Boolean canRejectCall){
        this.canRejectCall = canRejectCall;
        notifyPropertyChanged(BR.canRejectCall);
    }
}
