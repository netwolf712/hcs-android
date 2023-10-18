package com.hcs.android.business.request.service;

import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestDeviceList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestService {

    /**
     * 获取设备列表
     * data=>RequestDeviceList
     */
    @POST("/device/list")
    Observable<RequestDTO<DeviceModel>> getDeviceList(@Body RequestDTO<RequestDeviceList> requestDTO);

    /**
     * 更新设备信息
     */
    @POST("/device/updateDeviceInfo")
    void updateDeviceInfo(@Body RequestDTO<DeviceModel> requestDTO);
}
