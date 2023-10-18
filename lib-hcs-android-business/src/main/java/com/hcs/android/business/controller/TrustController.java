package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestTrust;
import com.hcs.android.business.entity.RequestUpdateDict;
import com.hcs.android.business.entity.ResponseTrust;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.service.DictService;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 托管
 */
@CommandMapping
public class TrustController {
    /**
     * 托管请求
     */
    @CommandId("req-trust")
    public AjaxResult reqTrust(String str){
        RequestDTO<RequestTrust> requestDTO = JsonUtils.toObject(str,RequestDTO.class,RequestTrust.class);
        WorkManager.getInstance().handleReqTrust(requestDTO);
        return AjaxResult.success("");
    }
    /**
     * 托管请求回复
     */
    @CommandId("rsp-trust")
    public AjaxResult rspTrust(String str){
        RequestDTO<ResponseTrust> requestDTO = JsonUtils.toObject(str,RequestDTO.class,ResponseTrust.class);
        WorkManager.getInstance().handleRspTrust(requestDTO);
        return AjaxResult.success("");
    }
}
