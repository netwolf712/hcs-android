package com.hcs.android.business.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 呼叫相关的属性
 */
@Entity
public class CallDict {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 呼叫类型
     * call、message
     */
    @ColumnInfo(name = "call_type")
    private String callType;
    public String getCallType(){
        return callType;
    }
    public void setCallType(String callType){
        this.callType = callType;
    }

    /**
     * 字典值
     */
    @ColumnInfo(name = "dict_value")
    private Integer dictValue;
    public Integer getDictValue(){
        return dictValue;
    }
    public void setDictValue(Integer dictValue){
        this.dictValue = dictValue;
    }

    /**
     * 显示名称
     */
    @ColumnInfo(name = "display_name")
    private String displayName;
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    /**
     * 排序，值越小优先级越高
     */
    @ColumnInfo(name = "sort")
    private Integer sort;
    public Integer getSort(){
        return sort;
    }
    public void setSort(Integer sort){
        this.sort = sort;
    }

    /**
     * 颜色，ARGB
     */
    @ColumnInfo(name = "color")
    private Integer color;
    public Integer getColor(){
        return color;
    }
    public void setColor(Integer color){
        this.color = color;
    }

    /**
     * 图标
     */
    @ColumnInfo(name = "icon")
    private String icon;
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon = icon;
    }

    /**
     * 是否紧急呼叫
     */
    @ColumnInfo(name = "emergency")
    private Integer emergency;
    public Integer getEmergency(){
        return emergency;
    }
    public void setEmergency(Integer emergency){
        this.emergency = emergency;
    }
}
