package com.hcs.android.business.request.model;

import android.os.Build;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.service.PatientService;
import com.hcs.android.common.util.JsonUtils;

import java.util.function.Consumer;

/**
 * 病员数据请求接口
 */
public class PatientRequestModel {
    public void changePatientInfo(PatientModel patientModel){
        RequestDTO<ResponseList<Patient>> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(), CommandEnum.REQ_UPDATE_PATIENT_INFO.getId());
        ResponseList<Patient> patientList = new ResponseList<>(patientModel.getPatient());
        requestDTO.setData(patientList);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }

    /**
     * 直接请求数据库获取病员信息
     * （通过异步形式调用）
     * @param patientSn 病员序列号
     */
    public void getPatientInfo(String patientSn,Consumer<PatientModel> consumer){
        new Thread(()->{
            PatientModel patientModel = PatientService.getInstance().getPatient(patientSn);
            if(consumer != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    consumer.accept(patientModel);
                }
            }
        }).start();
    }
}
