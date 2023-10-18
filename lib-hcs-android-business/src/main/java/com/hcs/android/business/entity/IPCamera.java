package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 网络摄像头
 */
@Entity
public class IPCamera  extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 位置id
     */
    @ColumnInfo(name = "place_uid")
    private String placeUid;
    public String getPlaceUid(){
        return placeUid;
    }
    public void setPlaceUid(String placeUid){
        this.placeUid = placeUid;
    }

    /**
     * 与位置绑定的主机序列号
     */
    @ColumnInfo(name = "master_device_id")
    private String masterDeviceId;
    public String getMasterDeviceId(){
        return masterDeviceId;
    }
    public void setMasterDeviceId(String masterDeviceId){
        this.masterDeviceId = masterDeviceId;
    }

    /**
     * IP地址
     */
    @ColumnInfo(name = "ip_address")
    private String ipAddress;
    public String getIpAddress(){
        return ipAddress;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    /**
     * onvif协议的监听端口
     */
    @ColumnInfo(name = "onvif_port")
    private Integer onvifPort;
    public Integer getOnvifPort(){
        return onvifPort;
    }
    public void setOnvifPort(Integer onvifPort){
        this.onvifPort = onvifPort;
    }

    /**
     * onvif协议的用户名
     */
    @ColumnInfo(name = "onvif_username")
    private String onvifUsername;
    public String getOnvifUsername(){
        return onvifUsername;
    }
    public void setOnvifUsername(String onvifUsername){
        this.onvifUsername = onvifUsername;
    }

    /**
     * onvif协议的密码
     */
    @ColumnInfo(name = "onvif_password")
    private String onvifPassword;
    public String getOnvifPassword(){
        return onvifPassword;
    }
    public void setOnvifPassword(String onvifPassword){
        this.onvifPassword = onvifPassword;
    }

    /**
     * 视频通道
     * 1、2
     */
    @ColumnInfo(name = "channel")
    private Integer channel;
    public Integer getChannel(){
        return channel;
    }
    public void setChannel(Integer channel){
        this.channel = channel;
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

    /**
     * 摄像头默认的x轴的角度
     * 带PTZ功能的摄像头才支持
     */
    @ColumnInfo(name = "default_position_x")
    private Double defaultPositionX;
    public Double getDefaultPositionX(){
        return defaultPositionX;
    }
    public void setDefaultPositionX(Double defaultPositionX){
        this.defaultPositionX = defaultPositionX;
    }

    /**
     * 摄像头默认的y轴的角度
     * 带PTZ功能的摄像头才支持
     */
    @ColumnInfo(name = "default_position_y")
    private Double defaultPositionY;
    public Double getDefaultPositionY(){
        return defaultPositionY;
    }
    public void setDefaultPositionY(Double defaultPositionY){
        this.defaultPositionY = defaultPositionY;
    }

    /**
     * 摄像头默认的缩放倍数
     * 带PTZ功能的摄像头才支持
     */
    @ColumnInfo(name = "default_zoom")
    private Double defaultZoom;
    public Double getDefaultZoom(){
        return defaultZoom;
    }
    public void setDefaultZoom(Double defaultZoom){
        this.defaultZoom = defaultZoom;
    }
}
