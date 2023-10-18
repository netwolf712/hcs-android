package com.hcs.android.server.controller;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.R;
import com.hcs.android.server.config.Config;
import com.hcs.android.server.entity.ChangePasswordBo;
import com.hcs.android.server.entity.LoginUser;
import com.hcs.android.server.entity.User;
import com.hcs.android.server.service.TokenService;
import com.hcs.android.server.service.UserService;
import com.hcs.android.server.util.AccessObjectUtil;
import com.hcs.android.server.web.AjaxResult;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.util.MediaType;

@RestController
public class UserController extends BaseController {

    /**
     * 登录
     */
    @PostMapping(path = "/login" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String login(HttpRequest httpRequest,@RequestBody String requestData) {
        User tmp = JsonUtils.toObject(requestData,User.class);
        if(Config.DEFAULT_INNER_WEB_USERNAME.equalsIgnoreCase(tmp.getUsername())
                && !AccessObjectUtil.getIpAddress(httpRequest).contains("127.0.0.1")
        || Config.DEFAULT_SERVICE_WEB_USERNAME.equalsIgnoreCase(tmp.getUsername())
        ){
            //内部用户名不允许外部访问，否则就产生漏洞了，服务器也不允许直接登录，只能设备发送访问token过去
            return toAjax(false).toString();
        }
        UserService userService = new UserService();
        User user = userService.login(tmp.getUsername(),tmp.getPassword());
        if(user == null){
            return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.error_username_or_password)).toString();
        }else{
            //LoginUser loginUser = TokenService.getInstance().findUserByUsername(user.getUsername());
            return AjaxResult.success("",user.getToken()).toString();
        }
    }

    /**
     * 获取用户信息
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @GetMapping(path = "/getInfo")
    public String getInfo(HttpRequest request) {
        String token = TokenService.getInstance().getToken(request);
        LoginUser loginUser = TokenService.getInstance().findUserByToken(token);
        if(loginUser == null){
            return toAjax(false).toString();
        }else{
            return AjaxResult.success("",loginUser).toString();
        }
    }

    /**
     * 修改密码
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(path = "/changePassword")
    public String changePassword(HttpRequest httpRequest,@RequestBody String requestData) {
        String token = TokenService.getInstance().getToken(httpRequest);
        ChangePasswordBo tmp = JsonUtils.toObject(requestData,ChangePasswordBo.class);
        UserService userService = new UserService();
        User user = userService.getUserByToken(token);
        if(user == null){
            return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.no_user)).toString();
        }else{
            if(tmp.getOldPassword().equals(user.getPassword())){
                user.setPassword(tmp.getNewPassword());
                userService.updateUser(user);
                return toAjax(true).toString();
            }else{
                return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.change_password_failed)).toString();
            }
        }
    }

    /**
     * 用户登出
     * 不用强制用户之前在登录状态，全部返回成功
     */
    @Addition(stringType = "needLogin", booleanType = false)
    @PostMapping(value = "/logout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String logout(HttpRequest request) {
        String token = TokenService.getInstance().getToken(request);
        UserService userService = new UserService();
        User user = userService.getUserByToken(token);
        if(user != null){
            TokenService.getInstance().removeUser(user.getUsername());
        }
        return AjaxResult.success().toString();
    }
}
