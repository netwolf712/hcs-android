package com.hcs.android.business.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.constant.BedScreenFunctionEnum;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.FunctionBo;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestBedDetail;
import com.hcs.android.business.entity.RequestBedScreenTemplate;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestListBase;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 病床处理相关
 */
@CommandMapping
public class BedController {
    /**
     * 获取病床列表
     */
    @CommandId("req-list-bed")
    public AjaxResult getDeviceList(@NonNull String str){
        RequestDTO<RequestListBase> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestListBase.class});
        if(requestDTO.getData() != null) {
            RequestListBase requestListBase = requestDTO.getData();
            ResponseList<PlaceModel> bedModelList = WorkManager.getInstance().getBedList(requestListBase.getMasterDeviceId(),requestListBase.getCurrentPage(),requestListBase.getPageSize());
            return AjaxResult.success("",bedModelList);
        }else {
            return AjaxResult.error("bad params");
        }
    }

    /**
     * 向床头屏请求更新模板
     */
    @CommandId("req-update-bed-template")
    public AjaxResult updateBedTemplate(String str){
        RequestDTO<BedScreenTemplate> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,BedScreenTemplate.class});
        WorkManager.getInstance().updateBedTemplate(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 获取床头屏模板
     */
    @CommandId("req-get-bed-template")
    public AjaxResult getBedTemplate(String str){
        RequestDTO<RequestBedScreenTemplate> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestBedScreenTemplate.class});
        return AjaxResult.success("",WorkManager.getInstance().getBedTemplate(requestDTO));
    }

    /**
     * 获取床头屏功能列表
     */
    @CommandId("req-list-bed-function")
    public AjaxResult getBedFunctionList(String str){
        List<FunctionBo> functionBoList = new ArrayList<>();
        Context context = BusinessApplication.getAppContext();
        for(BedScreenFunctionEnum functionEnum : BedScreenFunctionEnum.values()){
            FunctionBo functionBo = new FunctionBo();
            functionBo.setFunctionId(functionEnum.getValue());
            functionBo.setFunctionName(functionEnum.getDisplayName(context));
            functionBoList.add(functionBo);
        }
        ResponseList<FunctionBo> responseList = new ResponseList<>(functionBoList);
        return AjaxResult.success("",responseList);
    }

    /**
     * 获取床头屏详情
     */
    @CommandId("req-get-bed-detail")
    public AjaxResult getBedDetail(String str){
        RequestDTO<RequestBedDetail> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestBedDetail.class});
        return AjaxResult.success("",WorkManager.getInstance().handleGetBedDetail(requestDTO));
    }
}
