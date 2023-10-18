package com.hcs.calldemo.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hcs.calldemo.BR;

/**
 * 广播测试视图
 */
public class MulticastBo extends BaseObservable {
    /**
     * 文件路径
     */
    private String filePath;
    @Bindable
    public String getFilePath(){
        return filePath;
    }
    public void setFilePath(String filePath){
        this.filePath = filePath;
        notifyPropertyChanged(BR.filePath);
    }

    /**
     * 广播地址
     */
    private String address;
    @Bindable
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    /**
     * 是否有视频
     */
    private boolean withVideo = false;
    @Bindable
    public boolean isWithVideo(){
        return withVideo;
    }
    public void setWithVideo(boolean withVideo){
        this.withVideo = withVideo;
        notifyPropertyChanged(BR.withVideo);
    }

    /**
     * 是否循环播放
     */
    private boolean loopPlay = false;
    @Bindable
    public boolean isLoopPlay(){
        return loopPlay;
    }
    public void setLoopPlay(boolean loopPlay){
        this.loopPlay = loopPlay;
        notifyPropertyChanged(BR.loopPlay);
    }
}
