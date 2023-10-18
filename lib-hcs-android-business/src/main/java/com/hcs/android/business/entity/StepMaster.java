package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 继任的主机
 * 上级主机、附加主机、呼叫增援的主机、托管的主机
 */
@Entity
public class StepMaster extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 主机类型
     * 1 上级主机
     * 2 附加主机
     * 3 增援主机
     * 4 托管主机
     */
    @ColumnInfo(name = "master_type")
    private Integer masterType;
    public Integer getMasterType(){
        return masterType;
    }
    public void setMasterType(Integer masterType){
        this.masterType = masterType;
    }

    /**
     * 主机级别
     */
    @ColumnInfo(name = "master_level")
    private Integer masterLevel;
    public Integer getMasterLevel(){
        return masterLevel;
    }
    public void setMasterLevel(Integer masterLevel){
        this.masterLevel = masterLevel;
    }

    /**
     * 主机号码
     */
    @ColumnInfo(name = "master_no")
    private String masterNo;
    public String getMasterNo(){
        return masterNo;
    }
    public void setMasterNo(String masterNo){
        this.masterNo = masterNo;
    }

    /**
     * 等待呼叫的时间
     * 超时后呼叫此主机
     * 0表示不等待立即呼叫
     */
    @ColumnInfo(name = "call_wait_time")
    private Integer callWaitTime;
    @Bindable
    public Integer getCallWaitTime(){
        return callWaitTime;
    }
    public void setCallWaitTime(Integer callWaitTime){
        this.callWaitTime = callWaitTime;

    }

    /**
     * 信息更新时间
     * 此可以作为设备信息是否需要更新的判断标准
     */
    @ColumnInfo(name = "update_time")
    private Long updateTime;
    public Long getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Long updateTime){
        this.updateTime = updateTime;
    }

}
