package com.hcs.android.business.entity;

import java.util.List;

/**
 * 门口屏配置模板
 */
public class RoomScreenTemplate extends SlaveTemplate{
    /**
     * 屏保提示语
     */
    private String screenSaver;
    public String getScreenSaver(){
        return screenSaver;
    }
    public void setScreenSaver(String screenSaver){
        this.screenSaver = screenSaver;
    }

    /**
     * 屏保触发时间，单位：秒
     */
    private Integer screenSaverTime;
    public Integer getScreenSaverTime(){
        return screenSaverTime;
    }
    public void setScreenSaverTime(Integer screenSaverTime){
        this.screenSaverTime = screenSaverTime;
    }

    /**
     * 息屏时间，"auto"表示自动，"xx:xx--xx:xx"表示具体时间段
     */
    private String screenShutTime;
    public String getScreenShutTime(){
        return screenShutTime;
    }
    public void setScreenShutTime(String screenShutTime){
        this.screenShutTime = screenShutTime;
    }

    /**
     * 功能列表
     */
    private List<FunctionBo> functionList;
    public List<FunctionBo> getFunctionList(){
        return functionList;
    }
    public void setFunctionList(List<FunctionBo> functionList){
        this.functionList = functionList;
    }
}
