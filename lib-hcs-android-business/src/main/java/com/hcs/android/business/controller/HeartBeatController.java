package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 心跳
 */
@CommandMapping
public class HeartBeatController {
    /**
     * 心跳
     */
    @CommandId("req-heart-beat")
    public AjaxResult handleHeartBeat(String str){
        RequestDTO<Object> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,Object.class});
        WorkManager.getInstance().handleHeartBeat(requestDTO);
        return AjaxResult.success("");
    }
}
