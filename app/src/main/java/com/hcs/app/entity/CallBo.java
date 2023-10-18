package com.hcs.app.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hcs.app.BR;

/**
 * 呼叫实体
 * 业务相关
 */
public class CallBo extends BaseObservable {
    /**
     * 本地号码
     */
    private String localName;
    @Bindable
    public String getLocalName(){
        return localName;
    }
    public void setLocalName(String localName){
        this.localName = localName;
        notifyPropertyChanged(BR.localName);
    }

    /**
     * 远端号码
     */
    private String remoteName;
    @Bindable
    public String getRemoteName(){
        return remoteName;
    }
    public void setRemoteName(String remoteName){
        this.remoteName = remoteName;
        notifyPropertyChanged(BR.remoteName);
    }

    /**
     * 远端地址
     */
    private String remoteAddress;
    @Bindable
    public String getRemoteAddress(){
        return remoteAddress;
    }
    public void setRemoteAddress(String remoteAddress){
        this.remoteAddress = remoteAddress;
        notifyPropertyChanged(BR.remoteAddress);
    }

    /**
     * 呼叫状态
     */
    private String callStatus;
    @Bindable
    public String getCallStatus(){
        return callStatus;
    }
    public void setCallStatus(String callStatus){
        this.callStatus = callStatus;
        notifyPropertyChanged(BR.callStatus);
    }
}
