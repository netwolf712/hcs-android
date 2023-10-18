package com.hcs.android.server.controller;

import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.service.UserService;
import com.hcs.android.server.web.WebServer;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

/**
 * 运维相关的请求
 */
@RestController
@RequestMapping(path = "/maintain")
public class MaintainController extends BaseController {

    public MaintainController() {
        KLog.v("MaintainController init");
    }
    /**
     * 自定义请求
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(value = "/custom",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String custom( @RequestBody String requestData) {
        try {
            KLog.w("custom !! " + requestData);
            if(WebServer.getInstance().getMaintainController()!= null){
               return WebServer.getInstance().getMaintainController().dispatchWebController(requestData).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toAjax(true).toString();
    }
}