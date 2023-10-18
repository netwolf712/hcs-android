package com.hcs.android.maintain.controller;

import com.hcs.android.annotation.annotation.HcsApp;
import com.hcs.android.annotation.api.HandlerManager;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.constant.CommandEnum;
import com.hcs.android.maintain.entity.RequestDTO;
import com.hcs.android.maintain.entity.RequestData;
import com.hcs.android.server.controller.IWebController;
import com.hcs.android.server.web.AjaxResult;
@HcsApp
public class WebController implements IWebController {
    /**
     * web控制器
     */
    protected final HandlerManager mWebHandlerManager;
    private final static String HANDLER_PACKAGE_NAME = "com.hcs.android.maintain.controller";
    public WebController(){
        mWebHandlerManager = new HandlerManager(HANDLER_PACKAGE_NAME);
    }
    @Override
    public AjaxResult dispatchWebController(String serialData){
        return dispatchMessage(serialData);
    }

    public AjaxResult dispatchMessage(String serialData){
        CommandEnum commandEnum = getCommand(serialData);
        if(commandEnum != CommandEnum.UNKNOWN) {
            KLog.i("handle command " + commandEnum.getName(BaseApplication.getAppContext()));
            RequestData requestData = new RequestData(commandEnum,serialData);
            return (AjaxResult) mWebHandlerManager.handleCommand(requestData.getCommandEnum().getId(),requestData.getRawData());
        }else{
            KLog.i("do analyze serialData  failed");
            return AjaxResult.error();
        }
    }

    public CommandEnum getCommand(String serialData){
        RequestDTO<Object> requestDTO = JsonUtils.toObject(serialData,new Class[]{RequestDTO.class,Object.class});
        if(requestDTO != null) {
            return CommandEnum.findById(requestDTO.getCommandId());
        }
        return CommandEnum.UNKNOWN;
    }
}