package com.hcs.android.business.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.HandoverLog;

import java.util.List;

@Dao
public interface HandoverLogDao {
    @Query("SELECT * FROM HandoverLog")
    List<HandoverLog> getAll();

    @Query("SELECT * FROM HandoverLog order by uid desc LIMIT :limit OFFSET :offset")
    List<HandoverLog> getAll(int limit,int offset);

    @Query("SELECT count(1) FROM HandoverLog")
    int countAll();

    @Query("SELECT * FROM HandoverLog WHERE uid = :uid")
    HandoverLog findOne(int uid);

    /**
     * 根据主被叫号码获取操作日志
     */
    @Query("SELECT * FROM HandoverLog where (:state IS NOT NULL AND state = :state OR 1=1) order by uid desc LIMIT :limit OFFSET :offset")
    List<HandoverLog> getHandoverLogList(Integer state,int limit,int offset);

    @Query("SELECT count(1) FROM HandoverLog " +
            "where (:state IS NOT NULL AND state = :state OR 1=1) "
    )
    Integer count(Integer state);

    @Insert
    void insertAll(HandoverLog... HandoverLogs);

    @Insert
    void insert(HandoverLog HandoverLog);

    @Delete
    void delete(HandoverLog HandoverLog);

    @Update
    void update(HandoverLog HandoverLog);

    @Query("DELETE FROM HandoverLog")
    void deleteAll();
}