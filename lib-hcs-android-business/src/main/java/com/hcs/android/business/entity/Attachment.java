package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 附件
 */
@Entity
public class Attachment extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 存放的位置
     */
    @ColumnInfo(name = "path")
    private String path;
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path = path;
    }

    /**
     * 文件名
     */
    @ColumnInfo(name = "name")
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    /**
     * 文件大小，单位byte
     */
    @ColumnInfo(name = "size")
    private Long size;
    public Long getSize(){
        return size;
    }
    public void setSize(Long size){
        this.size = size;
    }

    /**
     * 文件类型
     */
    @ColumnInfo(name = "type")
    private String type;
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    /**
     * 用途，多个用途间用半角,隔开
     * eg：1,2,3
     */
    @ColumnInfo(name = "use")
    private String use;
    public String getUse(){
        return use;
    }
    public void setUse(String use){
        this.use = use;
    }
    /**
     * 信息更新时间
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
