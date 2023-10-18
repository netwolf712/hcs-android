package com.hcs.android.business.entity;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hcs.android.common.util.StringUtil;

/**
 * 位置信息
 */
@Entity
public class Place extends BaseObservable {
    /**
     * 自定义的全局主键
     */
    @PrimaryKey
    @NonNull
    private String uid;
    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    /**
     * 父级主键
     */
    @ColumnInfo(name = "parent_uid")
    private String parentUid;
    public String getParentUid(){
        return parentUid;
    }
    public void setParentUid(String parentUid){
        this.parentUid = parentUid;
    }

    /**
     * 位置序号，从1开始
     * 这个只能针对某个主机来说的
     */
    @ColumnInfo(name = "place_sn")
    private Integer placeSn;
    public Integer getPlaceSn(){
        return placeSn;
    }
    public void setPlaceSn(Integer placeSn){
        this.placeSn = placeSn;
    }

    /**
     * 位置号
     */
    @ColumnInfo(name = "place_no")
    private String placeNo;
    @Bindable
    public String getPlaceNo(){
        return placeNo;
    }
    public void setPlaceNo(String placeNo){
        this.placeNo = placeNo;
        notifyPropertyChanged(BR.placeNo);
    }

    /**
     * 位置所属的分组
     */
    @ColumnInfo(name = "group_no")
    private String groupNo;
    @Bindable
    public String getGroupNo(){
        return groupNo;
    }
    public void setGroupNo(String groupNo){
        this.groupNo = groupNo;
        notifyPropertyChanged(BR.groupNo);
    }


    /**
     * 位置所属的分组的序号
     * 这个才具有唯一性
     */
    @ColumnInfo(name = "group_sn")
    private Integer groupSn;
    @Bindable
    public Integer getGroupSn(){
        return groupSn;
    }
    public void setGroupSn(Integer groupSn){
        this.groupSn = groupSn;
        notifyPropertyChanged(BR.groupSn);
    }


    /**
     * 位置名称
     */
    @ColumnInfo(name = "place_name")
    private String placeName;
    @Bindable
    public String getPlaceName(){
        return placeName;
    }
    public void setPlaceName(String placeName){
        this.placeName = placeName;
        notifyPropertyChanged(BR.placeName);
    }

    /**
     * 管理这个位置的主机
     */
    @ColumnInfo(name = "master_device_id")
    private String masterDeviceId;
    public String getMasterDeviceId() {
        return masterDeviceId;
    }
    public void setMasterDeviceId(String masterDeviceId) {
        this.masterDeviceId = masterDeviceId;
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
     * 位置类型
     * 0病区、1病房、2病床、3走廊、4卫生间
     */
    @ColumnInfo(name = "place_type")
    private Integer placeType;
    @Bindable
    public Integer getPlaceType(){
        return placeType;
    }
    public void setPlaceType(Integer placeType){
        this.placeType = placeType;
        notifyPropertyChanged(BR.placeType);
    }

    /**
     * 是否同一个位置
     */
    public boolean isSamePlace(@NonNull Place place){
        return StringUtil.equalsIgnoreCase(getMasterDeviceId(), place.getMasterDeviceId())
                && getPlaceSn().equals(place.getPlaceSn())
                && getPlaceType().equals(place.getPlaceType())
                && StringUtil.equalsIgnoreCase(getPlaceNo(),place.getPlaceNo())
                && StringUtil.equalsIgnoreCase(getPlaceName(),place.getPlaceName());
    }

    public boolean equals(@NonNull Place place){
        return isSamePlace(place);
    }
}
