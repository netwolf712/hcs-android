package com.hcs.commondemo.entity;

import androidx.databinding.BaseObservable;

/**
 * 日志管理对象
 */
public class LogBo extends BaseObservable {
    /**
     * 是否开启dmesg功能
     */
    private boolean enableDmesg;
    public boolean isEnableDmesg(){
        return enableDmesg;
    }
    public void setEnableDmesg(boolean enableDmesg){
        this.enableDmesg = enableDmesg;
    }

    /**
     * dmesg保存目录
     */
    private String dmesgDir;
    public String getDmesgDir(){
        return dmesgDir;
    }
    public void setDmesgDir(String dmesgDir){
        this.dmesgDir = dmesgDir;
    }

    /**
     * 是否开启logcat
     */
    private boolean enableLogcat;
    public boolean isEnableLogcat(){
        return enableLogcat;
    }
    public void setEnableLogcat(boolean enableLogcat){
        this.enableLogcat = enableLogcat;
    }

    /**
     * logcat保存目录
     */
    private String logcatDir;
    public String getLogcatDir(){
        return logcatDir;
    }
    public void setLogcatDir(String logcatDir){
        this.logcatDir = logcatDir;
    }

    /**
     * 是否开启抓包
     */
    private boolean enablePcap;
    public boolean isEnablePcap(){
        return enablePcap;
    }
    public void setEnablePcap(boolean enablePcap){
        this.enablePcap = enablePcap;
    }

    /**
     * pcap文件保存目录
     */
    private String pcapDir;
    public String getPcapDir(){
        return pcapDir;
    }
    public void setPcapDir(String pcapDir){
        this.pcapDir = pcapDir;
    }
}
