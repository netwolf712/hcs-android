package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.request.viewmodel.CallLogic;

import org.simple.eventbus.EventBus;

import java.util.List;

public class PlaceRequestModel {

    private Integer mCurrentPage = 0;
    public void setCurrentPage(Integer currentPage) {
        this.mCurrentPage = currentPage;
    }
    public Integer getCurrentPage() {
        return mCurrentPage;
    }

    private Integer mPageSize = 30;
    public void setPageSize(Integer pageSize) {
        this.mPageSize = pageSize;
    }
    public Integer getPageSize() {
        return mPageSize;
    }

    public String getMasterIdByPhoneNo(String phoneNo){
        DeviceModel deviceModel = WorkManager.getInstance().getDeviceByPhoneNo(phoneNo);
        if(deviceModel != null){
            return deviceModel.getDevice().getDeviceId();
        }
        return null;
    }
    public PlaceRequestModel() {
        //注册事件管道
        EventBus.getDefault().register(this);
    }

    public List<PlaceModel>  getList(int placeType,String masterDeviceId) {
        return WorkManager.getInstance().getPlaceListFromCache(placeType,masterDeviceId,mPageSize *  mCurrentPage,mPageSize);
    }

    public List<PlaceModel>  getListAll(int placeType,String masterDeviceId) {
        return WorkManager.getInstance().getPlaceListFromCache(placeType,masterDeviceId,0, Constant.INVALID_LIMIT);
    }

    /**
     * 普通呼叫
     */
    public void handleCall(@NonNull DeviceModel deviceModel){
        CallLogic.handleCall(deviceModel);
    }
}

