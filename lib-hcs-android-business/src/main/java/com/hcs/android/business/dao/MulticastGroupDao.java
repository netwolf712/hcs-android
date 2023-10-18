package com.hcs.android.business.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.MulticastGroup;
import java.util.List;

@Dao
public interface MulticastGroupDao {
    @Query("SELECT * FROM MulticastGroup")
    List<MulticastGroup> getAll();

    @Query("SELECT * FROM MulticastGroup LIMIT :limit OFFSET :offset")
    LiveData<List<MulticastGroup>> getAll(int limit, int offset);

    @Query("SELECT * FROM MulticastGroup where time_slot_id is null")
    List<MulticastGroup> getBaseGroups();

    @Query("SELECT * FROM MulticastGroup where time_slot_id > 0")
    List<MulticastGroup> getTimeSlotGroups();

    @Query("SELECT count(1) FROM MulticastGroup")
    Integer countAll();

    /**
     * 根据病房号获取病区列表
     * @param groupSn 分区序号
     * @return 病区列表
     */
    @Query("SELECT * FROM MulticastGroup WHERE group_sn = :groupSn and time_slot_id is null LIMIT 1")
    MulticastGroup findOne(Integer groupSn);

    /**
     * 根据时间id获取病区列表
     * @param timeSlotId 时间id
     * @return 病区列表
     */
    @Query("SELECT * FROM MulticastGroup WHERE  time_slot_id = :timeSlotId LIMIT 1")
    MulticastGroup findOneByTimeSlot(int timeSlotId);

    /**
     * 根据病房号获取病区列表
     * @param uid 索引
     * @return 病区列表
     */
    @Query("SELECT * FROM MulticastGroup WHERE uid = :uid LIMIT 1")
    MulticastGroup findOneById(int uid);

    @Insert
    void insertAll(MulticastGroup... multicastGroup);

    @Insert
    void insert(MulticastGroup multicastGroup);

    @Delete
    void delete(MulticastGroup multicastGroup);

    @Update
    void update(MulticastGroup multicastGroup);

    @Query("DELETE FROM MulticastGroup")
    void deleteAll();
}