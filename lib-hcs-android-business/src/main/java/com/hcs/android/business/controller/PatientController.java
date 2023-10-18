package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 病员处理相关
 */
@CommandMapping
public class PatientController {
    /**
     * 请求更新病员信息
     */
    @CommandId("req-update-patient-info")
    public AjaxResult updatePatientInfo(String str){
        RequestDTO<ResponseList<Patient>> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,ResponseList.class,Patient.class});
        WorkManager.getInstance().updatePatientInfo(requestDTO);
        return AjaxResult.success("");
    }
}
