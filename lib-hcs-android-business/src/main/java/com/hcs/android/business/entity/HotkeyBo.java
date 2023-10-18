package com.hcs.android.business.entity;

/**
 * 快捷按键对象
 */
public class HotkeyBo {
    /**
     * 按键id（分机上的外接按键、手持按键）
     */
    private String buttonId;
    public String getButtonId(){
        return buttonId;
    }
    public void setButtonId(String buttonId){
        this.buttonId = buttonId;
    }

    /**
     * 呼叫时显示的名称
     */
    private String callName;
    public String getCallName(){
        return callName;
    }
    public void setCallName(String callName){
        this.callName = callName;
    }

    /**
     * 是否为紧急呼叫
     */
    private boolean emergencyCall;
    public boolean isEmergencyCall(){
        return emergencyCall;
    }
    public void setEmergencyCall(boolean emergencyCall){
        this.emergencyCall = emergencyCall;
    }
}
