package com.hcs.app.web;

//import com.hcs.android.client.dto.RequestDTO;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.controller.IWebController;
import com.hcs.android.server.web.AjaxResult;

public class WebController implements IWebController {
    @Override
    public AjaxResult dispatchWebController(String serialData){
//        RequestDTO requstDTO = JsonUtils.toObject(serialData,RequestDTO.class);
//        if(requstDTO != null) {
//            KLog.i("do webcontroler command " + requstDTO.getCommandId());
//            return AjaxResult.success();
//        }else{
//            KLog.i("do webcontroler analyze serialdata  failed");
//            return AjaxResult.error();
//        }
        return AjaxResult.success();
    }
}
