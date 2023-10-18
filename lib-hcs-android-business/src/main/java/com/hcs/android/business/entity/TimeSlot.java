package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 时间段
 */
@Entity
public class TimeSlot extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 某一种工作类型的时间段设置
     * 禁止呼叫上传
     * 托管
     * 门禁常开
     */
    @ColumnInfo(name = "type")
    private Integer type;
    public Integer getType(){
        return type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    /**
     * 周期
     * 星期日——星期六
     * 由0-6表示
     * 多个日期间由半角,隔开
     */
    @ColumnInfo(name = "period")
    private String period;
    public String getPeriod(){
        return period;
    }
    public void setPeriod(String period){
        this.period = period;
    }

    /**
     * 开始时间
     * 由小时:分钟转换成的毫秒数
     */
    @ColumnInfo(name = "start_time")
    private Long startTime;
    public Long getStartTime(){
        return startTime;
    }
    public void setStartTime(Long startTime){
        this.startTime = startTime;
    }

    /**
     * 结束时间
     * 由小时:分钟转换成的毫秒数
     */
    @ColumnInfo(name = "end_time")
    private Long endTime;
    public Long getEndTime(){
        return endTime;
    }
    public void setEndTime(Long endTime){
        this.endTime = endTime;
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

    /**
     * 状态
     * 0 停用
     * 1 启用
     */
    @ColumnInfo(name = "state")
    private Integer state;
    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }
}
