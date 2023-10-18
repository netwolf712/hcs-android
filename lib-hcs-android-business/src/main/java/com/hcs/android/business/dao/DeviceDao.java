package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM Device")
    List<Device> getAll();

    @Query("SELECT * FROM Device WHERE uid IN (:deviceIds)")
    List<Device> loadAllByIds(int[] deviceIds);

    /**
     * 根据设备id获取设备
     * @param deviceId 设备id
     * @return
     */
    @Query("SELECT * FROM Device WHERE device_id LIKE :deviceId LIMIT 1")
    Device findOne(String deviceId);

    /**
     * 根据设备型号获取设备列表
     * @param deviceType 设备型号
     * @return 设备列表
     */
    @Query("SELECT * FROM Device WHERE device_type = :deviceType")
    List<Device> getDeviceListByType(int deviceType);

    /**
     * 查找某个主机的所有下级分机
     * @param parentNo 主机号码
     * @return 设备列表
     */
    @Query("SELECT * FROM Device WHERE parent_number = :parentNo")
    List<Device> getDeviceListByParentNo(String parentNo);

    /**
     * 查找某个主机下的所有离线设备
     * @param parentNo 主机号码
     * @param offlineTimePoint 作为离线判断的时间点
     */
    @Query("SELECT * FROM Device WHERE parent_number = :parentNo and heart_beat_time < :offlineTimePoint")
    List<Device> getOfflineDeviceList(String parentNo,long offlineTimePoint);

    /**
     * 更新心跳时间
     */
    @Query("UPDATE Device SET heart_beat_time = :heartBeatTime WHERE device_id LIKE :deviceId")
    void updateHeartBeatTime(String deviceId,long heartBeatTime);

    /**
     * 根据父位置查找下级位置的所有设备
     * @param parentNo 主机号码
     * @param parentPlaceUid 父位置id
     * @return 设备列表
     */
    @Query("SELECT * FROM Device WHERE parent_number = :parentNo AND place_uid in (select uid from Place where parent_uid LIKE :parentPlaceUid)")
    List<Device> getDeviceListByParentPlace(String parentNo,String parentPlaceUid);

    /**
     * 根据位置信息查找此位置下的所有设备
     * @param parentNo 主机号码
     * @param placeUid 位置id
     * @return 设备列表
     */
    @Query("SELECT * FROM Device WHERE parent_number = :parentNo AND place_uid LIKE :placeUid")
    List<Device> getDeviceListByPlace(String parentNo,String placeUid);

    @Insert
    void insertAll(Device... devices);

    @Insert
    void insert(Device device);

    @Delete
    void delete(Device device);

    @Update
    void update(Device device);
}