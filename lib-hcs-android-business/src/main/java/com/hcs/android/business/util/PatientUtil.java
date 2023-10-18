package com.hcs.android.business.util;

import android.content.Context;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.manager.WorkManager;

/**
 * 病员帮助类
 */
public class PatientUtil {
    /**
     * 获取病员的全名
     */
    public static String getPatientFullName(PatientModel patientModel){
        if(patientModel == null || patientModel.getPatient() == null){
            return "";
        }
        return patientModel.getPatient().getName();
    }

    /**
     * 获取病员姓名
     * （带隐私处理）
     */
    public static String getPatientName(PatientModel patientModel){
        return WorkManager.getInstance().getPatientName(patientModel);
    }

    /**
     * 获取年龄
     * （带年龄处理逻辑）
     */
    public static String getPatientAge(PatientModel patientModel){
        return WorkManager.getInstance().getPatientAge(patientModel);
    }
}
