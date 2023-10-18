package com.hcs.android.business.controller;

import androidx.annotation.NonNull;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestPlaceList;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 位置处理相关
 */
@CommandMapping
public class PlaceController {
    /**
     * 获取病房列表
     */
    @CommandId("req-list-place")
    public AjaxResult getDeviceList(@NonNull String str){
        RequestDTO<RequestPlaceList> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestPlaceList.class});
        if(requestDTO.getData() != null) {
            RequestPlaceList requestListBase = requestDTO.getData();
            ResponseList<PlaceModel> placeModelList = new ResponseList<>(WorkManager.getInstance().getPlaceListFromCache(requestListBase.getPlaceType(),requestListBase.getMasterDeviceId(),(requestListBase.getCurrentPage() - 1) * requestListBase.getPageSize(),requestListBase.getPageSize()));
            return AjaxResult.success("",placeModelList);
        }else {
            return AjaxResult.error("bad params");
        }
    }

    /**
     * 请求更新病房信息
     */
    @CommandId("req-update-place-info")
    public AjaxResult updatePlaceInfo(String str){
        RequestDTO<ResponseList<Place>> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,ResponseList.class,Place.class});
        WorkManager.getInstance().updatePlaceInfo(requestDTO);
        return AjaxResult.success("");
    }
}
