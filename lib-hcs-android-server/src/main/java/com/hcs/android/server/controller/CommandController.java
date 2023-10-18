package com.hcs.android.server.controller;

import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.service.UserService;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

/**
 * 通用命令层的web请求
 */
@RestController
@RequestMapping(path = "/command")
public class CommandController extends BaseController {

    private UserService userService = new UserService();

    public CommandController() {
        KLog.v("CommandController init");
    }

    /**
     * 使用post传递指定命令示例
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(value = "/setNetwork" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String setNetwork(@RequestBody String requestData) {
        try {
            KLog.w("setNetwork !!" + requestData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toAjax(true).toString();
    }

    /**
     * web命令处理
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(value = "/doCommands" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String doCommands(@RequestBody String requestData) {
        try {
            KLog.i("WebCommand :" + requestData + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toAjax(true).toString();
    }
}