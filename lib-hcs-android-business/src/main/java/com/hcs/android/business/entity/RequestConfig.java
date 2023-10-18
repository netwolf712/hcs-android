package com.hcs.android.business.entity;

import java.util.List;

/**
 * 请求配置
 * 模板id与配置id的关系？
 * 每个设备允许存在多个模板，每个模板根据需求随时都可以进行配置，此时就需要有配置id来标识此为模板哪个时间段的配置信息
 */
public class RequestConfig {
    /**
     * 当前模板配置id，可以考虑直接用配置时间作为id
     * System.currentTimeMillis()
     */
    private String currentTemplateConfigId;
    public String getCurrentTemplateConfigId(){
        return currentTemplateConfigId;
    }
    public void setCurrentTemplateConfigId(String currentTemplateConfigId){
        this.currentTemplateConfigId = currentTemplateConfigId;
    }

    /**
     * 当前模板id
     */
    private String currentTemplateId;
    public String getCurrentTemplateId(){
        return currentTemplateId;
    }
    public void setCurrentTemplateId(String currentTemplateId){
        this.currentTemplateId = currentTemplateId;
    }

    /**
     * 设备信息更新时间
     */
    private Long deviceUpdateTime;
    public Long getDeviceUpdateTime(){
        return deviceUpdateTime;
    }
    public void setDeviceUpdateTime(Long deviceUpdateTime){
        this.deviceUpdateTime = deviceUpdateTime;
    }

    /**
     * 字典数据更新时间
     */
    private Long dictUpdateTime;
    public Long getDictUpdateTime() {
        return dictUpdateTime;
    }
    public void setDictUpdateTime(Long dictUpdateTime) {
        this.dictUpdateTime = dictUpdateTime;
    }

    /**
     * 最后一个数据请求的索引
     */
    private Long lastDataCommandIndex;

    public Long getLastDataCommandIndex() {
        return lastDataCommandIndex;
    }

    public void setLastDataCommandIndex(Long lastDataCommandIndex) {
        this.lastDataCommandIndex = lastDataCommandIndex;
    }

    /**
     * 继任主机的数据更新时间
     */
    private Long stepMasterUpdateTime;

    public Long getStepMasterUpdateTime() {
        return stepMasterUpdateTime;
    }

    public void setStepMasterUpdateTime(Long stepMasterUpdateTime) {
        this.stepMasterUpdateTime = stepMasterUpdateTime;
    }

    /**
     * 时间段的数据更新时间
     */
    private Long timeSlotUpdateTime;

    public Long getTimeSlotUpdateTime() {
        return timeSlotUpdateTime;
    }

    public void setTimeSlotUpdateTime(Long timeSlotUpdateTime) {
        this.timeSlotUpdateTime = timeSlotUpdateTime;
    }
}
