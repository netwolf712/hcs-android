package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.entity.TimeSlot;

import java.util.List;

@Dao
public interface TimeSlotDao {
    @Query("SELECT * FROM TimeSlot")
    List<TimeSlot> getAll();

    @Query("SELECT * FROM TimeSlot LIMIT :limit OFFSET :offset")
    List<TimeSlot> getAll(int limit, int offset);


    @Query("SELECT count(1) FROM TimeSlot")
    Integer countAll();


    /**
     * 根据主机类型查询附件列表
     * @param type 主机类型
     */
    @Query("SELECT * FROM TimeSlot where type = :type  LIMIT :limit OFFSET :offset")
    List<TimeSlot> getListByType(int type,int limit, int offset);

    @Query("SELECT * FROM TimeSlot where type = :type")
    List<TimeSlot> getListByType(int type);

    @Query("SELECT count(1) FROM TimeSlot where type = :type")
    Integer countByType(int type);

    @Query("SELECT * FROM TimeSlot WHERE uid = :uid LIMIT 1")
    TimeSlot findOneById(int uid);

    @Insert
    void insertAll(TimeSlot... timeSlots);

    @Insert
    void insert(TimeSlot timeSlot);

    @Delete
    void delete(TimeSlot timeSlot);

    @Update
    void update(TimeSlot timeSlot);

    @Query("DELETE FROM TimeSlot")
    void deleteAll();

    @Query("DELETE FROM TimeSlot WHERE type = :type")
    void deleteByType(int type);
}