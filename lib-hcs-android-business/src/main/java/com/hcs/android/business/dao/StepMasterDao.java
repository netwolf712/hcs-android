package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.StepMaster;

import java.util.List;

@Dao
public interface StepMasterDao {
    @Query("SELECT * FROM StepMaster")
    List<StepMaster> getAll();

    @Query("SELECT * FROM StepMaster LIMIT :limit OFFSET :offset")
    List<StepMaster> getAll(int limit, int offset);


    @Query("SELECT count(1) FROM StepMaster")
    Integer countAll();


    /**
     * 根据主机类型查询附件列表
     * @param masterType 主机类型
     */
    @Query("SELECT * FROM StepMaster where master_type = :masterType  LIMIT :limit OFFSET :offset")
    List<StepMaster> getListByType(int masterType,int limit, int offset);

    @Query("SELECT count(1) FROM StepMaster where master_type = :masterType")
    Integer countByType(int masterType);


    @Query("SELECT * FROM StepMaster WHERE master_no = :masterNo LIMIT 1")
    StepMaster findOneByMasterNo(String masterNo);

    /**
     * 根据主机类型查询附件列表，不带分页查询功能
     * @param masterType 主机类型
     */
    @Query("SELECT * FROM StepMaster where master_type = :masterType")
    List<StepMaster> getListByType(int masterType);

    @Insert
    void insertAll(StepMaster... stepMasters);

    @Insert
    void insert(StepMaster stepMaster);

    @Delete
    void delete(StepMaster stepMaster);

    @Update
    void update(StepMaster stepMaster);

    @Query("DELETE FROM StepMaster")
    void deleteAll();

    @Query("DELETE FROM StepMaster WHERE master_type = :masterType")
    void deleteByType(int masterType);
}