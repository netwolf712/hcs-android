package com.hcs.android.business.request.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.request.model.PlaceRequestModel;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;

import org.simple.eventbus.EventBus;

import java.util.List;

public class PlaceViewModel extends BaseRefreshViewModel<PlaceModel, CommonViewModel> {

    private Integer mCurrentPage = 0;
    public void setCurrentPage(Integer currentPage) {
        this.mCurrentPage = currentPage;
        mPlaceRequestModel.setCurrentPage(currentPage);
    }
    public Integer getCurrentPage() {
        return mCurrentPage;
    }

    private Integer mPageSize = 30;
    public void setPageSize(Integer pageSize) {
        this.mPageSize = pageSize;
        mPlaceRequestModel.setPageSize(pageSize);
    }
    public Integer getPageSize() {
        return mPageSize;
    }


    private Integer placeType;

    public Integer getPlaceType() {
        return placeType;
    }

    public void setPlaceType(Integer placeType) {
        this.placeType = placeType;
    }

    private String masterDeviceId;
    public String getMasterDeviceId(){
        return masterDeviceId;
    }
    public void setMasterDeviceId(String masterDeviceId){
        this.masterDeviceId = masterDeviceId;
    }

    private PlaceRequestModel mPlaceRequestModel;
    //private Integer mTotalSize;

    public PlaceViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        //注册事件管道
        EventBus.getDefault().register(this);
        mPlaceRequestModel = new PlaceRequestModel();
    }

    public void refreshData() {
        postShowNoDataViewEvent(false);
//        if (!NetUtil.checkNetToast()) {
//            postShowNetWorkErrViewEvent(true);
//            return;
//        }
        List<PlaceModel> placeModelList = mPlaceRequestModel.getList(placeType,masterDeviceId);
        if(placeModelList != null) {
            mList.addAll(placeModelList);
        }
    }

    /**
     * 根据主机号码找到主机设备id
     * @param phoneNo 主机号码
     */
    public void setMasterIdByPhoneNo(String phoneNo){
        DeviceModel deviceModel = WorkManager.getInstance().getDeviceByPhoneNo(phoneNo);
        if(deviceModel != null){
            setMasterDeviceId(deviceModel.getDevice().getDeviceId());
        }
    }

    @Override
    public void loadMore() {
        refreshData();
    }

    /**
     * 普通呼叫
     */
    public void handleCall(DeviceModel deviceModel){
        if(deviceModel != null) {
            CallLogic.handleCall(deviceModel);
        }
    }
}

