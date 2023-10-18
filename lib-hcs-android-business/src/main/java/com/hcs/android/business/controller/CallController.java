package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 呼叫相关
 */
@CommandMapping
public class CallController {
    /**
     * 呼叫
     */
    @CommandId("req-send-call")
    public AjaxResult handleSendCall(String str){
        RequestDTO<CallModel> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,CallModel.class});
        WorkManager.getInstance().handleSendCall(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 清理呼叫
     */
    @CommandId("req-clear-call")
    public AjaxResult handleClearCall(String str){
        RequestDTO<CallModel> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,CallModel.class});
        WorkManager.getInstance().handleClearCall(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 通知其它设备更新通话状态-只作显示
     */
    @CommandId("req-update-call-info")
    public AjaxResult handleUpdateCallInfo(String str){
        RequestDTO<CallModel> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,CallModel.class});
        WorkManager.getInstance().handleReqUpdateCallInfo(requestDTO);
        return AjaxResult.success("");
    }
}
