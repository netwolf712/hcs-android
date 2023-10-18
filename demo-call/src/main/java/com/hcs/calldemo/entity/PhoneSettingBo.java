package com.hcs.calldemo.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hcs.calldemo.BR;

/**
 * linphone配置相关
 */
public class PhoneSettingBo extends BaseObservable {
    /**
     * 用户名
     */
    private String username = "hcs";
    @Bindable
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    /**
     * 显示名称
     */
    private String displayName = "hcs";
    @Bindable
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
        notifyPropertyChanged(BR.displayName);
    }

    /**
     * 是否启用自动应答
     */
    private boolean autoAnswer = false;
    public boolean isAutoAnswer(){
        return autoAnswer;
    }
    public void setAutoAnswer(boolean autoAnswer){
        this.autoAnswer = autoAnswer;
    }

    /**
     * 自动应答时间
     * 单位秒
     */
    private String autoAnswerTime = "30";
    public String getAutoAnswerTime(){
        return autoAnswerTime;
    }
    public void setAutoAnswerTime(String autoAnswerTime){
        this.autoAnswerTime = autoAnswerTime;
    }

    /**
     * 是否启用视频
     */
    private boolean videoEnabled = true;
    public boolean isVideoEnabled(){
        return videoEnabled;
    }
    public void setVideoEnabled(boolean videoEnabled){
        this.videoEnabled = videoEnabled;
    }

    /**
     * 是否使用IPv6
     */
    private boolean useIPv6 = false;
    public boolean isUseIPv6(){
        return useIPv6;
    }
    public void setUseIPv6(boolean useIPv6){
        this.useIPv6 = useIPv6;
    }
}
