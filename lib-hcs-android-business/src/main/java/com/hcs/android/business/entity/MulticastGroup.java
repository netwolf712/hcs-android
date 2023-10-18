package com.hcs.android.business.entity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.common.util.StringUtil;

/**
 * 分区广播配置
 */
@Entity
public class MulticastGroup extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 分区序号
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
     * 分区号
     * -1表全区
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
     * 播放模式
     * 0顺序播放，1顺序循环播放，2单曲循环播放，3随机循环播放
     */
    @ColumnInfo(name = "play_type")
    private Integer playType;
    @Bindable
    public Integer getPlayType(){
        return playType;
    }
    public void setPlayType(Integer playType){
        this.playType = playType;
        notifyPropertyChanged(BR.playType);
    }

    /**
     * 文件路径列表
     * 多个文件间用半角;隔开
     */
    @ColumnInfo(name = "file_list")
    private String fileList;
    @Bindable
    public String getFileList(){
        return fileList;
    }
    public void setFileList(String fileList){
        this.fileList = fileList;
        notifyPropertyChanged(BR.fileList);
    }

    /**
     * 播放音量
     */
    @ColumnInfo(name = "volume")
    private Integer volume;
    @Bindable
    public Integer getVolume(){
        if(volume == null) {
            Context context = BusinessApplication.getAppContext();
            return context.getResources().getInteger(R.integer.default_audio_multicast_group_whole_volume);
        }
        return volume;
    }
    public void setVolume(Integer volume){
        this.volume = volume;
        notifyPropertyChanged(BR.volume);
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
     * 管理这个分区的主机
     */
    @ColumnInfo(name = "device_id")
    private String deviceId;
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 自动播放的时间段id
     */
    @ColumnInfo(name = "time_slot_id")
    private Integer timeSlotId;
    public Integer getTimeSlotId() {
        return timeSlotId;
    }
    public void setTimeSlotId(Integer timeSlotId) {
        this.timeSlotId = timeSlotId;
    }
    /**
     * 是否同一个分区
     */
    public boolean isSameGroup(@NonNull MulticastGroup multicastGroup){
        return StringUtil.equalsIgnoreCase(getDeviceId(), multicastGroup.deviceId)
                && getGroupSn().equals(multicastGroup.getGroupSn());
    }
}
