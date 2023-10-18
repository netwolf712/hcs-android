package com.hcs.android.business.controller;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestData;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.controller.IWebController;
import com.hcs.android.server.web.AjaxResult;

public class WebController implements IWebController {
    @Override
    public AjaxResult dispatchWebController(String serialData){
        return dispatchMessage(serialData);
    }

    public AjaxResult dispatchMessage(String serialData){
        RequestDTO<Object> requestDTO = JsonUtils.toObject(serialData,new Class[]{RequestDTO.class,Object.class});
        if(requestDTO != null) {
            DeviceModel deviceModel = requestDTO.asDeviceModel();
            if(deviceModel.getDevice().getDeviceType() != DeviceTypeEnum.WEB.getValue()) {
                //通过web发送的请求不用做预处理
                WorkManager.getInstance().handleRequestFirst(requestDTO);
            }
            //return (AjaxResult)HandlerManager.getInstance().handleCommand(requestDTO.getCommandId(),serialData);
            RequestData requestData = new RequestData(CommandEnum.findById(requestDTO.getCommandId()),requestDTO.getDataCommandIndex(),serialData);
            return WorkManager.getInstance().handleRequest(requestData);
        }else{
            KLog.i("do analyze serialData  failed");
            return AjaxResult.error();
        }
    }
}