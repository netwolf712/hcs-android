package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestTimeSlotList;
import com.hcs.android.business.entity.RequestUpdateTimeSlot;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.entity.TimeSlot;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 继任主机
 */
public class TimeSlotRequestModel {

    /**
     * 当前页面，永远是第一页
     */
    private final int CURRENT_PAGE = 1;
    /**
     * 页面大小，足够大
     */
    private final int PAGE_SIZE = 10000;
    @NonNull
    public ObservableData<List<TimeSlot>> getTimeSlotList(Integer timeSlotType){
        ObservableData<List<TimeSlot>> listObservable = new ObservableData<>();
        DeviceModel deviceModel = WorkManager.getInstance().getSelfInfo();
        RequestDTO<RequestTimeSlotList> requestDTO = new RequestDTO<>(deviceModel, CommandEnum.REQ_LIST_TIME_SLOT.getId());
        RequestTimeSlotList requestTimeSlotList = new RequestTimeSlotList();
        requestTimeSlotList.setTimeSlotType(timeSlotType);
        requestTimeSlotList.setCurrentPage(CURRENT_PAGE);
        requestTimeSlotList.setPageSize(PAGE_SIZE);
        requestDTO.setData(requestTimeSlotList);
        ObservableData<AjaxResult> resultObservableData = WorkManager.getInstance().sendInnerRequestObservable(JsonUtils.toJsonString(requestDTO));
        resultObservableData.addObserver((observable, o) -> {
            if(o instanceof AjaxResult){
                AjaxResult res = (AjaxResult) o;
                if(res.getData() != null){
                    ResponseList<TimeSlot> timeSlotResponseList = JsonUtils.toObject(JsonUtils.toJsonString(res.getData()),ResponseList.class,TimeSlot.class);
                    listObservable.setT(timeSlotResponseList.getList());
                }
            }
        });
        return listObservable;
    }

    /**
     * 更新主机信息
     */
    public void updateTimeSlots(Integer timeSlotType,List<TimeSlot> timeSlotList){
        DeviceModel deviceModel = WorkManager.getInstance().getSelfInfo();
        RequestDTO<RequestUpdateTimeSlot> requestDTO = new RequestDTO<>(deviceModel,CommandEnum.REQ_UPDATE_TIME_SLOT.getId());
        RequestUpdateTimeSlot requestUpdateTimeSlot = new RequestUpdateTimeSlot();
        ResponseList<TimeSlot> responseList = new ResponseList<>(timeSlotList);
        requestUpdateTimeSlot.setResponseList(responseList);
        requestUpdateTimeSlot.setTimeSlotType(timeSlotType);
        requestDTO.setData(requestUpdateTimeSlot);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));

    }
}
