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
 * 业务层的web请求
 */
@RestController
@RequestMapping(path = "/business")
public class BusinessController extends BaseController {


    private UserService userService = new UserService();

    public BusinessController() {
        KLog.v("BusinessController init");
    }

    /**
     * 自定义请求
     *
     * @return
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(value = "/custom",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String custom( @RequestBody String requestData) {
        try {
//            if (!userService.checkAdmin(username, token)) {
//                return toAjax(false).toString();
//            }
            KLog.w("custom !! " + requestData);
            if(WebServer.getInstance().getWebController() != null){
                return WebServer.getInstance().getWebController().dispatchWebController(requestData).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toAjax(true).toString();
    }
}