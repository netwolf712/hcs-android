package com.hcs.android.business.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.hcs.android.business.entity.OperationLog;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

@Dao
public interface OperationLogDao {
    @Query("SELECT * FROM OperationLog")
    List<OperationLog> getAll();

    @Query("SELECT * FROM OperationLog LIMIT :limit OFFSET :offset")
    List<OperationLog> getAll(int limit,int offset);

    @Query("SELECT count(1) FROM OperationLog")
    int countAll();

    @Query("SELECT * FROM OperationLog WHERE uid = :uid")
    OperationLog findOne(int uid);

    @Query("SELECT * FROM OperationLog WHERE call_ref LIKE :callRef")
    OperationLog findOne(String callRef);

    /**
     * 根据主被叫号码获取操作日志
     */
    @Query("SELECT * FROM OperationLog WHERE caller LIKE :caller and callee LIKE :callee")
    List<OperationLog> getOperationLogList(String caller,String callee);


    /**
     * 多条件组合查询
     */
    @Query("SELECT * FROM OperationLog " +
            "where (:deviceName IS NOT NULL AND (caller LIKE :deviceName or callee LIKE :deviceName) OR 1=1) " +
            "AND (:deviceType IS NOT NULL AND (caller_type = :deviceType or callee_type = :deviceType) OR 1=1) " +
            "AND (:deviceId IS NOT NULL AND (callee_device_id LIKE :deviceId or caller_device_id LIKE :deviceId) OR 1=1) " +
            "AND (:dateStart IS NOT NULL AND update_time >=  :dateStart OR 1=1) " +
            "AND (:dateEnd IS NOT NULL AND update_time <=  :dateEnd OR 1=1) " +
            "ORDER BY uid desc LIMIT :limit OFFSET :offset"
    )
    List<OperationLog> getOperationLogList(Integer deviceType, String deviceName, String deviceId, String dateStart, String dateEnd, int limit, int offset);

    @Query("SELECT count(1) FROM OperationLog " +
            "where (:deviceName IS NOT NULL AND (caller LIKE :deviceName or callee LIKE :deviceName) OR 1=1) " +
            "AND (:deviceType IS NOT NULL AND (caller_type = :deviceType or callee_type = :deviceType) OR 1=1) " +
            "AND (:deviceId IS NOT NULL AND (callee_device_id LIKE :deviceId or caller_device_id LIKE :deviceId) OR 1=1) " +
            "AND (:dateStart IS NOT NULL AND update_time >=  :dateStart OR 1=1) " +
            "AND (:dateStart IS NOT NULL AND update_time <=  :dateEnd OR 1=1) "
    )
    Integer count(Integer deviceType,String deviceName,String deviceId,String dateStart,String dateEnd);
    @Insert
    void insertAll(OperationLog... OperationLogs);

    @Insert
    void insert(OperationLog OperationLog);

    @Delete
    void delete(OperationLog OperationLog);

    @Update
    void update(OperationLog OperationLog);

    @Query("DELETE FROM OperationLog")
    void deleteAll();
}