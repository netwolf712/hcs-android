package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 医疗相关的属性
 */
@Entity
public class Dict extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 字典类型
     */
    @ColumnInfo(name = "dict_type")
    private String dictType;
    public String getDictType(){
        return dictType;
    }
    public void setDictType(String dictType){
        this.dictType = dictType;
    }

    /**
     * 字典值
     */
    @ColumnInfo(name = "dict_value")
    private Integer dictValue;
    @Bindable
    public Integer getDictValue(){
        return dictValue;
    }
    public void setDictValue(Integer dictValue){
        this.dictValue = dictValue;
        notifyPropertyChanged(BR.dictValue);
    }

    /**
     * 显示名称
     */
    @ColumnInfo(name = "display_name")
    private String displayName;
    @Bindable
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
        notifyPropertyChanged(BR.displayName);
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
     * 背景颜色，ARGB
     */
    @ColumnInfo(name = "background_color")
    private Long backgroundColor;
    @Bindable
    public Long getBackgroundColor(){
        return backgroundColor;
    }
    public void setBackgroundColor(Long backgroundColor){
        this.backgroundColor = backgroundColor;
        notifyPropertyChanged(BR.backgroundColor);
    }

    /**
     * 字体颜色，ARGB
     */
    @ColumnInfo(name = "text_color")
    private Long textColor;
    @Bindable
    public Long getTextColor(){
        return textColor;
    }
    public void setTextColor(Long textColor){
        this.textColor = textColor;
        notifyPropertyChanged(BR.textColor);
    }


    /**
     * 图标
     */
    @ColumnInfo(name = "icon")
    private String icon;
    @Bindable
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon = icon;
        notifyPropertyChanged(BR.icon);
    }
}
