package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.Patient;

import java.util.List;

@Dao
public interface PatientDao {
    @Query("SELECT * FROM Patient WHERE deleted = 0")
    List<Patient> getAll();

    @Query("SELECT * FROM Patient WHERE deleted = 0 LIMIT :limit OFFSET :offset")
    List<Patient> getAll(int limit,int offset);

    @Query("SELECT count(1) FROM Patient WHERE deleted = 0")
    int countAll();

    @Query("SELECT * FROM Patient WHERE uid IN (:patientIds)  and deleted = 0")
    List<Patient> loadAllByIds(int[] patientIds);

    /**
     * 根据id获取
     * @param patientId 病员id
     */
    @Query("SELECT * FROM Patient WHERE uid = :patientId and deleted = 0 LIMIT 1")
    Patient findOne(int patientId);

    /**
     * 根据病员编号
     * @param serialNumber 病员编号
     */
    @Query("SELECT * FROM Patient WHERE serial_number like :serialNumber and deleted = 0 LIMIT 1")
    Patient findOne(String serialNumber);


    /**
     * 根据病床号获取人员
     * @param bedSn 病床序号
     */
    @Query("SELECT * FROM Patient WHERE master_device_id LIKE :masterDeviceId and bed_sn = :bedSn")
    Patient getPatient(String masterDeviceId,int bedSn);

    @Insert
    void insertAll(Patient... patients);

    @Insert
    void insert(Patient patient);

    @Delete
    void delete(Patient patient);

    @Update
    void update(Patient patient);

    @Query("DELETE FROM Patient")
    void deleteAll();
}