package com.hcs.android.business.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.IPCamera;

import java.util.List;

@Dao
public interface IPCameraDao {
    @Query("SELECT * FROM IPCamera")
    List<IPCamera> getAll();

    @Query("SELECT * FROM IPCamera LIMIT :limit OFFSET :offset")
    LiveData<List<IPCamera>> getAll(int limit, int offset);


    @Query("SELECT count(1) FROM IPCamera")
    Integer countAll();

    /**
     * 根据绑定的设备id获取摄像头信息列表
     * @param masterDeviceId 位置对应的主机id
     * @param placeUid 位置id
     * @return 摄像头
     */
    @Query("SELECT * FROM IPCamera WHERE master_device_id LIKE :masterDeviceId AND place_uid LIKE :placeUid LIMIT 1")
    IPCamera findOne(String masterDeviceId,String placeUid);

    /**
     * 根据序号号获取摄像头信息
     * @param uid 索引
     * @return 摄像头
     */
    @Query("SELECT * FROM IPCamera WHERE uid = :uid LIMIT 1")
    IPCamera findOneById(int uid);

    @Insert
    void insertAll(IPCamera... ipCameras);

    @Insert
    void insert(IPCamera ipCamera);

    @Delete
    void delete(IPCamera ipCamera);

    @Update
    void update(IPCamera ipCamera);

    @Query("DELETE FROM IPCamera")
    void deleteAll();
}