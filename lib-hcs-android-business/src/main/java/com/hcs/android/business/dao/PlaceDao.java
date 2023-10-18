package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.Place;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM Place")
    List<Place> getAll();

    @Query("SELECT * FROM Place LIMIT :limit OFFSET :offset")
    List<Place> getAll(int limit,int offset);

    @Query("SELECT * FROM Place WHERE master_device_id LIKE :masterDeviceId LIMIT :limit OFFSET :offset")
    List<Place> getAll(String masterDeviceId,int limit,int offset);

    @Query("SELECT * FROM Place WHERE master_device_id LIKE :masterDeviceId AND place_type = :placeType LIMIT :limit OFFSET :offset")
    List<Place> getAll(String masterDeviceId,int placeType,int limit,int offset);

    @Query("SELECT * FROM Place WHERE master_device_id LIKE :masterDeviceId")
    List<Place> getAll(String masterDeviceId);

    @Query("SELECT * FROM Place WHERE master_device_id LIKE :masterDeviceId AND place_type = :placeType")
    List<Place> getAll(String masterDeviceId,int placeType);

    @Query("SELECT * FROM Place WHERE master_device_id LIKE :masterDeviceId AND parent_uid LIKE :parentUid")
    List<Place> getAllByParent(String masterDeviceId,String parentUid);

    @Query("SELECT * FROM Place WHERE master_device_id LIKE :masterDeviceId AND group_no LIKE :groupNo")
    List<Place> getAllByGroup(String masterDeviceId,String groupNo);

    @Query("SELECT count(1) FROM Place")
    int countAll();

    @Query("SELECT count(1) FROM Place WHERE master_device_id LIKE :masterDeviceId")
    int countAll(String masterDeviceId);

    @Query("SELECT count(1) FROM Place WHERE master_device_id LIKE :masterDeviceId AND place_type = :placeType")
    int countAll(String masterDeviceId,int placeType);

    @Query("SELECT * FROM Place WHERE uid LIKE :uid LIMIT 1")
    Place findOne(String uid);

    @Insert
    void insertAll(Place... Places);

    @Insert
    void insert(Place Place);

    @Delete
    void delete(Place Place);

    @Update
    void update(Place Place);

    @Query("DELETE FROM Place")
    void deleteAll();

    @Query("DELETE FROM Place WHERE master_device_id LIKE :masterDeviceId")
    void deleteByMasterDeviceId(String masterDeviceId);
}