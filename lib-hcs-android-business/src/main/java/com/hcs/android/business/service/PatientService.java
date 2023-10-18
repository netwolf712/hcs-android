package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.BooleanConstant;
import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.PatientDao;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 病员服务
 */
public class PatientService implements IDbService<Patient>{

    private final PatientDao mPatientDao = DataBaseHelper.getInstance().patientDao();

    private static PatientService mInstance = null;
    public static PatientService getInstance(){
        if(mInstance == null){
            synchronized (PatientService.class){
                if(mInstance == null) {
                    mInstance = new PatientService();
                }
            }
        }
        return mInstance;
    }
    private PatientModel getPatient(Patient patient){
        if(patient != null){
            return new PatientModel(patient);
        }
        return null;
    }

    public PatientModel getPatient(String masterDeviceId,Integer bedSn){
        if(StringUtil.isEmpty(masterDeviceId) || bedSn == null){
            return null;
        }
        return getPatient(mPatientDao.getPatient(masterDeviceId,bedSn));
    }

    public PatientModel getPatient(String patientSN){
        return getPatient(mPatientDao.findOne(patientSN));
    }

    /**
     * 更新或新增病员信息
     * 已存在则更新，否则新增
     */
    public void updatePatient(@NonNull Patient patient){
        //根据序列号找到病员
        PatientModel oldPatient = getPatient(patient.getSerialNumber());
        patient.setUpdateTime(System.currentTimeMillis());
        //若存在则更新
        if(oldPatient != null || patient.getUid() != null){
            if(patient.getUid() == null) {
                patient.setUid(oldPatient.getPatient().getUid());
            }
            mPatientDao.update(patient);
        }else {
            //否则新增
            mPatientDao.insert(patient);
        }
    }

    /**
     * 移除病员
     * deleted置1
     */
    public void removePatient(@NonNull Patient patient){
        patient.setDeleted(BooleanConstant.INTEGER_TRUE);
        mPatientDao.update(patient);
    }


    @Override
    public void deleteAll(){
        mPatientDao.deleteAll();
    }
    @Override
    public void insertAll(List<Patient> patientList){
        if(StringUtil.isEmpty(patientList)){
            return;
        }
        Patient[] patients = patientList.toArray(new Patient[0]);
        mPatientDao.insertAll(patients);
    }

    public List<PatientModel> getPatientList(int offset,int limit){
        List<Patient> patientList = mPatientDao.getAll(limit, offset);
        if(!StringUtil.isEmpty(patientList)){
            List<PatientModel> patientModelList = new ArrayList<>();
            for(Patient patient : patientList){
                PatientModel patientModel = new PatientModel(patient);
                patientModelList.add(patientModel);
            }
            return patientModelList;
        }
        return null;
    }

    public int getPatientCount(){
        return mPatientDao.countAll();
    }
}
