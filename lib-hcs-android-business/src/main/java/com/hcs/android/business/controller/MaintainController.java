package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.RequestBindGroup;
import com.hcs.android.business.entity.RequestBindPlace;
import com.hcs.android.business.entity.RequestBindPlaceParent;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 运维相关
 */
@CommandMapping
public class MaintainController {

    /**
     * 绑定分区
     */
    @CommandId("req-bind-group")
    public AjaxResult handleBindGroup(String str){
        RequestDTO<RequestBindGroup> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class, RequestBindGroup.class});
        WorkManager.getInstance().handleBindGroup(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 绑定位置
     */
    @CommandId("req-bind-place")
    public AjaxResult handleBindPlace(String str){
        RequestDTO<RequestBindPlace> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class, RequestBindPlace.class});
        WorkManager.getInstance().handleBindPlace(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 绑定父位置
     */
    @CommandId("req-bind-place-parent")
    public AjaxResult handleBindPlaceParent(String str){
        RequestDTO<RequestBindPlaceParent> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class, RequestBindPlaceParent.class});
        WorkManager.getInstance().handleBindPlaceParent(requestDTO);
        return AjaxResult.success("");
    }
}
