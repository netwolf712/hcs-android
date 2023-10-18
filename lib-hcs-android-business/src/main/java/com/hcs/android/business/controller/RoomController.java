package com.hcs.android.business.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.constant.RoomScreenFunctionEnum;
import com.hcs.android.business.entity.FunctionBo;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestListBase;
import com.hcs.android.business.entity.RequestRoomDetail;
import com.hcs.android.business.entity.RequestRoomScreenTemplate;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.RoomScreenTemplate;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 病房处理相关
 */
@CommandMapping
public class RoomController {
    /**
     * 获取病房列表
     */
    @CommandId("req-list-room")
    public AjaxResult getDeviceList(@NonNull String str){
        RequestDTO<RequestListBase> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestListBase.class});
        if(requestDTO.getData() != null) {
            RequestListBase requestListBase = requestDTO.getData();
            ResponseList<PlaceModel> roomModelList = WorkManager.getInstance().getRoomList(requestListBase.getMasterDeviceId(),requestListBase.getCurrentPage(),requestListBase.getPageSize());
            return AjaxResult.success("",roomModelList);
        }else {
            return AjaxResult.error("bad params");
        }
    }

    /**
     * 向门口屏请求更新模板
     */
    @CommandId("req-update-room-template")
    public AjaxResult updateRoomTemplate(String str){
        RequestDTO<RoomScreenTemplate> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class, RoomScreenTemplate.class});
        WorkManager.getInstance().updateRoomTemplate(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 获取病房门口屏模板
     */
    @CommandId("req-get-room-template")
    public AjaxResult getRoomTemplate(String str){
        RequestDTO<RequestRoomScreenTemplate> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestRoomScreenTemplate.class});
        return AjaxResult.success("",WorkManager.getInstance().getRoomTemplate(requestDTO));
    }

    /**
     * 获取门口屏详情
     */
    @CommandId("req-get-room-detail")
    public AjaxResult getRoomDetail(String str){
        RequestDTO<RequestRoomDetail> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestRoomDetail.class});
        return AjaxResult.success("",WorkManager.getInstance().handleGetRoomDetail(requestDTO));
    }

    /**
     * 获取门口屏功能列表
     */
    @CommandId("req-list-room-function")
    public AjaxResult getRoomFunctionList(String str){
        List<FunctionBo> functionBoList = new ArrayList<>();
        Context context = BusinessApplication.getAppContext();
        for(RoomScreenFunctionEnum functionEnum : RoomScreenFunctionEnum.values()){
            FunctionBo functionBo = new FunctionBo();
            functionBo.setFunctionId(functionEnum.getValue());
            functionBo.setFunctionName(functionEnum.getDisplayName(context));
            functionBoList.add(functionBo);
        }
        ResponseList<FunctionBo> responseList = new ResponseList<>(functionBoList);
        return AjaxResult.success("",responseList);
    }
}
