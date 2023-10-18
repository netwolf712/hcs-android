package com.hcs.app.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.constant.AttachmentUseEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.app.R;

import java.util.List;

/**
 * 显示转换工具
 */
public class DisplayConvertUtil {
    /**
     * 将该房间的病床号列表已字符串的形式展现
     * @return 病床号列表，多个病床号间以半角,隔开
     */
    public static String roomBedList(@NonNull PlaceModel roomModel){
        String bedNoList = "";
        if(!StringUtil.isEmpty(roomModel.getChildren())){
            for(PlaceModel bedModel : roomModel.getChildren()){
                if(!StringUtil.isEmpty(bedNoList)){
                    bedNoList += ",";
                }
                bedNoList += bedModel.getPlace().getPlaceSn();
            }
        }
        return bedNoList;
    }

    @Nullable
    public static DeviceModel getFirstDevice(@NonNull PlaceModel placeModel){
        if(StringUtil.isEmpty(placeModel.getDeviceModelList())){
            return null;
        }
        return placeModel.getDeviceModelList().get(0);
    }
    public static String getDeviceAddress(@NonNull PlaceModel placeModel){
        DeviceModel deviceModel = getFirstDevice(placeModel);
        if(deviceModel != null){
            return deviceModel.getDevice().getIpAddress();
        }
        return "";
    }

    @Nullable
    private static PatientModel getFirstPatient(@NonNull PlaceModel placeModel){
        if(StringUtil.isEmpty(placeModel.getPatientModelList())){
            return null;
        }
        return placeModel.getPatientModelList().get(0);
    }

    public static String getPatientName(@NonNull PlaceModel placeModel){
        PatientModel patientModel = getFirstPatient(placeModel);
        if(patientModel == null){
            return "";
        }
        return patientModel.getPatient().getName();
    }

    /**
     * 将位置转换成字符串
     */
    public static String getNo(@NonNull PlaceModel placeModel){
        if(placeModel == null
                || placeModel.getPlace() == null
                || placeModel.getPlace().getPlaceType() == null
                || StringUtil.isEmpty(placeModel.getPlace().getPlaceNo())){
            return "";
        }

        return placeModel.getPlace().getPlaceNo();
    }

    /**
     * 设备状态
     */
    public static String getDeviceState(@NonNull PlaceModel placeModel){
        DeviceModel deviceModel = getFirstDevice(placeModel);
        if(deviceModel == null){
            return "";
        }
        return DeviceModel.convertState(deviceModel.getState());
    }

    public static String getDeviceId(@NonNull PlaceModel placeModel){
        DeviceModel deviceModel = getFirstDevice(placeModel);
        if(deviceModel == null){
            return "";
        }
        return deviceModel.getDevice().getDeviceId();
    }

    public static String getPatientSn(@NonNull PlaceModel placeModel){
        PatientModel patientModel = getFirstPatient(placeModel);
        if(patientModel == null){
            return "";
        }
        return patientModel.getPatient().getSerialNumber();
    }

    /**
     * 获取设备型号
     */
    public static String getDeviceType(DeviceModel deviceModel){
        if(deviceModel != null){
            return DeviceTypeEnum.findById(deviceModel.getDevice().getDeviceType()).getDisplayName(BusinessApplication.getAppContext());
        }
        return "";
    }

    /**
     * 获取呼叫状态
     */
    public static String getCallState(OperationLogModel operationLogModel){
        if(operationLogModel == null){
            return "";
        }
        return StateEnum.findById(operationLogModel.getOperationLog().getState()).getDisplayName(BusinessApplication.getAppContext());
    }

    /**
     * 时间相减并转换成秒级，保留小数点后两位
     * @param dstTime
     * @param srcTime
     * @return
     */
    @NonNull
    @SuppressLint("DefaultLocale")
    private static String millisecondToSecond(Long dstTime,Long srcTime){
        return String.format("%.2f",(float) (dstTime - srcTime) /1000);
    }
    /**
     * 获取呼叫时振铃持续的时间
     */
    @NonNull
    public static String getRingTime(OperationLogModel operationLogModel){
        if(operationLogModel == null){
            return "";
        }
        if(operationLogModel.getOperationLog().getConnectTime() != null && operationLogModel.getOperationLog().getStartTime() != null){
            return millisecondToSecond (operationLogModel.getOperationLog().getConnectTime(),operationLogModel.getOperationLog().getStartTime());
        }
        return "";
    }

    @NonNull
    public static String getTalkTime(OperationLogModel operationLogModel){
        if(operationLogModel == null){
            return "";
        }
        if(operationLogModel.getOperationLog().getConnectTime() != null && operationLogModel.getOperationLog().getStopTime() != null){
            return millisecondToSecond (operationLogModel.getOperationLog().getStopTime(),operationLogModel.getOperationLog().getConnectTime());
        }
        return "";
    }

    /**
     * 将附件用途转换为可视形式
     */
    public static String formatAttachmentUse(String use){
        if(StringUtil.isEmpty(use)){
            return "";
        }

        try{
            Context context = BusinessApplication.getAppContext();
            String strUse = "";
            List<Object> objList = StringUtil.CutStringWithChar(use,',');
            if(!StringUtil.isEmpty(objList)){
                for(Object obj : objList){
                    if(!StringUtil.isEmpty(strUse)){
                        strUse += ",";
                    }
                    AttachmentUseEnum useEnum = AttachmentUseEnum.findById(Integer.parseInt(obj.toString()));
                    switch (useEnum){
                        case RING:
                            strUse += context.getString(R.string.attachment_ring);
                            break;
                        case GroupMulticast:
                            strUse += context.getString(R.string.attachment_audio_multicast);
                            break;
                    }
                }
            }
            return strUse;
        }catch (Exception e){
            KLog.e(e);
        }
        return "";
    }
}
