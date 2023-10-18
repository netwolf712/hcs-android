package com.hcs.android.business.util;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.common.util.StringUtil;

import org.jetbrains.annotations.Contract;

/**
 * 位置帮助类
 */
public class PlaceUtil {
    /**
     * 根据相关信息生成位置的唯一编号
     * @param masterDeviceId 主机序列号
     * @param typeName 位置类型名称
     * @param sn 位置序号
     * @return 位置唯一编号
     */
    @NonNull
    @Contract(pure = true)
    public static String genPlaceUid(String masterDeviceId, String typeName, int sn){
        return masterDeviceId + "-" + typeName + "-" + sn;
    }

    /**
     * 获取位置的文字描述
     */
    public static String getDisplayNo(@NonNull PlaceModel placeModel){
        if(placeModel.getPlace() == null || placeModel.getPlace().getPlaceType() == null ){
            return "";
        }
        if(StringUtil.isEmpty(placeModel.getPlace().getPlaceNo())){
            return PlaceTypeEnum.findById(placeModel.getPlace().getPlaceType()).getDisplayName(BusinessApplication.getAppContext());
        }
        return placeModel.getPlace().getPlaceNo() + PlaceTypeEnum.findById(placeModel.getPlace().getPlaceType()).getDisplayName(BusinessApplication.getAppContext());
    }

    /**
     * 从位置中获取第一个病员
     */
    public static PatientModel getFirstPatient(PlaceModel placeModel){
        if(placeModel == null){
            return null;
        }
        if(StringUtil.isEmpty(placeModel.getPatientModelList())){
            return null;
        }
        //取第一个病人
        return placeModel.getPatientModelList().get(0);
    }
}
