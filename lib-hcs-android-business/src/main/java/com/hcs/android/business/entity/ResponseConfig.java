package com.hcs.android.business.entity;

import java.util.Date;

/**
 * 请求配置回复
 */
public class ResponseConfig {
    /**
     * 确认类型
     * 对应ConfirmTypeEnum
     */
    private Integer confirmId;
    public Integer getConfirmId(){
        return confirmId;
    }
    public void setConfirmId(Integer confirmId){
        this.confirmId = confirmId;
    }

    /**
     * 医院标题
     */
    private String hospitalTitle;

    public String getHospitalTitle() {
        return hospitalTitle;
    }

    public void setHospitalTitle(String hospitalTitle) {
        this.hospitalTitle = hospitalTitle;
    }
}
