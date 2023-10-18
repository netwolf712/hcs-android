package com.hcs.android.business.controller;


import androidx.annotation.NonNull;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.StepMasterTypeEnum;
import com.hcs.android.business.constant.TimeSlotTypeEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestConfig;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestPrivacy;
import com.hcs.android.business.entity.RequestStepMasterList;
import com.hcs.android.business.entity.RequestTimeSlotList;
import com.hcs.android.business.entity.RequestUpdateCapability;
import com.hcs.android.business.entity.RequestUpdateStepMaster;
import com.hcs.android.business.entity.RequestUpdateTimeSlot;
import com.hcs.android.business.entity.RequestVolumeSet;
import com.hcs.android.business.entity.ResponseConfig;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.ResponseStepMaster;
import com.hcs.android.business.entity.TimeSlot;
import com.hcs.android.business.manager.StepMasterManager;
import com.hcs.android.business.manager.TimeSlotManager;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.service.StepMasterService;
import com.hcs.android.business.service.TimeSlotService;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 设备管理、初始化相关
 */
@CommandMapping
public class DeviceController {
    /**
     * 获取设备列表
     */
    @CommandId("req-list-device")
    public AjaxResult getDeviceList(String str){
        List<DeviceModel> businessFriendList = WorkManager.getInstance().getDeviceList();
        return AjaxResult.success("",businessFriendList);
    }

    /**
     * 向所有其它设备通知设备自身信息变更
     */
    @CommandId("req-change-device-info")
    public AjaxResult changeDeviceInfo(String str){
        RequestDTO<DeviceModel> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,DeviceModel.class});
        WorkManager.getInstance().updateDeviceInfo(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 向所有其它设备通知设备自身已经起来
     */
    @CommandId("notice-device-started")
    public AjaxResult noticeDeviceStarted(String str){
        RequestDTO<DeviceModel> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,DeviceModel.class});
        WorkManager.getInstance().updateDeviceInfo(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 获取设备信息
     */
    @CommandId("req-get-device-info")
    public AjaxResult getDeviceInfo(String str){
        return AjaxResult.success("",WorkManager.getInstance().getSelfInfo());
    }

    /**
     * 向主机请求获取配置
     */
    @CommandId("req-get-config")
    public AjaxResult getConfig(String str){
        RequestDTO<RequestConfig> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestConfig.class});
        WorkManager.getInstance().handleReqGetConfig(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 主机向分机回复配置请求
     */
    @CommandId("rsp-get-config")
    public AjaxResult rspGetConfig(String str){
        RequestDTO<ResponseConfig> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,ResponseConfig.class});
        WorkManager.getInstance().handleRspGetConfig(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 更新设备参数
     */
    @CommandId("req-update-capability")
    public AjaxResult reqUpdateCapability(String str){
        RequestDTO<RequestUpdateCapability> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestUpdateCapability.class});
        WorkManager.getInstance().handleUpdateCapability(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 更新继任主机
     */
    @CommandId("req-update-step-master")
    public AjaxResult reqUpdateStepMaster(String str){
        RequestDTO<RequestUpdateStepMaster> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestUpdateStepMaster.class});
        RequestUpdateStepMaster requestUpdateStepMaster = requestDTO.getData();
        if(requestUpdateStepMaster != null){
            StepMasterManager.getInstance().updateStepMasters(requestUpdateStepMaster.getResponseList().getList()
                    , requestUpdateStepMaster.getStepMasterType() == null ? null : StepMasterTypeEnum.findById(requestUpdateStepMaster.getStepMasterType())
                    , requestUpdateStepMaster.getLastUpdateTime());
        }
        return AjaxResult.success("");
    }

    /**
     * 获取继任主机列表
     */
    @CommandId("req-list-step-master")
    public AjaxResult getStepMasterList(@NonNull String str){
        RequestDTO<RequestStepMasterList> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestStepMasterList.class});
        if(requestDTO.getData() != null) {
            RequestStepMasterList requestListBase = requestDTO.getData();
            ResponseStepMaster responseStepMaster = new ResponseStepMaster();
            responseStepMaster.setList(StepMasterService.getInstance().getStepMasterList(requestListBase.getStepMasterType(),(requestListBase.getCurrentPage() - 1) * requestListBase.getPageSize(),requestListBase.getPageSize()));
            DeviceModel selfModel = WorkManager.getInstance().getSelfInfo();
            if(selfModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
                //如果是主机，则主机号码是自己
                responseStepMaster.setMasterNo(selfModel.getDevice().getPhoneNo());
            }else {
                responseStepMaster.setMasterNo(WorkManager.getInstance().getSelfInfo().getDevice().getParentNo());
            }
            return AjaxResult.success("",responseStepMaster);
        }else {
            return AjaxResult.error("bad params");
        }
    }

    /**
     * 更新时间段
     */
    @CommandId("req-update-time-slot")
    public AjaxResult reqTimeSlot(String str){
        RequestDTO<RequestUpdateTimeSlot> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestUpdateTimeSlot.class});
        RequestUpdateTimeSlot requestUpdateTimeSlot = requestDTO.getData();
        if(requestUpdateTimeSlot != null){
            TimeSlotManager.getInstance().updateTimeSlots(requestUpdateTimeSlot.getResponseList().getList()
                    , requestUpdateTimeSlot.getTimeSlotType() == null ? null : TimeSlotTypeEnum.findById(requestUpdateTimeSlot.getTimeSlotType())
                    , requestUpdateTimeSlot.getLastUpdateTime());
        }
        return AjaxResult.success("");
    }

    /**
     * 获取时间段列表
     */
    @CommandId("req-list-time-slot")
    public AjaxResult getTimeSlotList(@NonNull String str){
        RequestDTO<RequestTimeSlotList> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestTimeSlotList.class});
        if(requestDTO.getData() != null) {
            RequestTimeSlotList requestListBase = requestDTO.getData();
            ResponseList<TimeSlot> timeSlotResponseList = new ResponseList<>(TimeSlotService.getInstance().getTimeSlotList(requestListBase.getTimeSlotType(),(requestListBase.getCurrentPage() - 1) * requestListBase.getPageSize(),requestListBase.getPageSize()));
            return AjaxResult.success("",timeSlotResponseList);
        }else {
            return AjaxResult.error("bad params");
        }
    }

    /**
     * 获取设备状态
     */
    @CommandId("req-get-status")
    public AjaxResult getDeviceStatus(String str){
        return AjaxResult.success("",WorkManager.getInstance().getSelfState());
    }

    /**
     * 获取隐私设置
     */
    @CommandId("req-get-privacy")
    public AjaxResult getPrivacySet(String str){
        return AjaxResult.success("",WorkManager.getInstance().getPrivacySet());
    }

    /**
     * 更新隐私设置
     */
    @CommandId("req-update-privacy")
    public AjaxResult updatePrivacySet(String str){
        RequestDTO<RequestPrivacy> requestDTO = JsonUtils.toObject(str,RequestDTO.class,RequestPrivacy.class);
        WorkManager.getInstance().handleReqPrivacySet(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 获取音量设置
     */
    @CommandId("req-get-volume")
    public AjaxResult getVolumeSet(String str){
        return AjaxResult.success("",WorkManager.getInstance().getVolumeSet());
    }

    /**
     * 更新音量设置
     */
    @CommandId("req-update-volume")
    public AjaxResult updateVolumeSet(String str){
        RequestDTO<RequestVolumeSet> requestDTO = JsonUtils.toObject(str,RequestDTO.class,RequestVolumeSet.class);
        WorkManager.getInstance().handleReqVolumeSet(requestDTO);
        return AjaxResult.success("");
    }
}
