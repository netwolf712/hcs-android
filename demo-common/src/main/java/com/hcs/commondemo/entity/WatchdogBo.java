package com.hcs.commondemo.entity;

import androidx.databinding.BaseObservable;

/**
 * 看门狗管理对象
 */
public class WatchdogBo extends BaseObservable {
    /**
     * 是否开启软件狗
     */
    private boolean enableSoftwareWatchdog;
    public boolean isEnableSoftwareWatchdog(){
        return enableSoftwareWatchdog;
    }
    public void setEnableSoftwareWatchdog(boolean enableSoftwareWatchdog){
        this.enableSoftwareWatchdog = enableSoftwareWatchdog;
    }

    /**
     * 是否开启硬件狗
     */
    private boolean enableHardwareWatchdog;
    public boolean isEnableHardwareWatchdog(){
        return enableHardwareWatchdog;
    }
    public void setEnableHardwareWatchdog(boolean enableHardwareWatchdog){
        this.enableHardwareWatchdog = enableHardwareWatchdog;;
    }

}
