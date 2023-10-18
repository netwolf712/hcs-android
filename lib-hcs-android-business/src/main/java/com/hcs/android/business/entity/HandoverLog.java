package com.hcs.android.business.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 交班留言
 */
@Entity
public class HandoverLog {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 类型
     * 0音频、1视频
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
     * 留言人姓名
     */
    @ColumnInfo(name = "user_name")
    private String userName;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 记录插入时间
     * 留言时间
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
     * 留言时长，单位毫秒
     */
    @ColumnInfo(name = "duration")
    private Integer duration;
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }



    /**
     * 附加文件路径
     * 录音、录像
     */
    @ColumnInfo(name = "append_path")
    private String appendPath;
    public String getAppendPath(){
        return appendPath;
    }
    public void setAppendPath(String appendPath){
        this.appendPath = appendPath;
    }

    /**
     * 描述
     */
    @ColumnInfo(name = "description")
    private String description;
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * 状态
     * 0未读
     * 1已读
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
