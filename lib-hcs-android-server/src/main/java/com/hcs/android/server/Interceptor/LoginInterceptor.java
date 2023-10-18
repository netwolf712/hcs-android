/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hcs.android.server.Interceptor;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.server.R;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.server.entity.LoginUser;
import com.hcs.android.server.service.TokenService;
import com.hcs.android.server.web.AjaxResult;
import com.yanzhenjie.andserver.annotation.Interceptor;
import com.yanzhenjie.andserver.error.BasicException;
import com.yanzhenjie.andserver.framework.HandlerInterceptor;
import com.yanzhenjie.andserver.framework.handler.MethodHandler;
import com.yanzhenjie.andserver.framework.handler.RequestHandler;
import com.yanzhenjie.andserver.framework.mapping.Addition;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;

/**
 * Created by Zhenjie Yan on 2018/9/11.
 * 用户登录拦截器
 */
@Interceptor
public class LoginInterceptor implements HandlerInterceptor {

    public static final String LOGIN_ATTRIBUTE = "USER.LOGIN.SIGN";

    @Override
    public boolean onIntercept(@NonNull HttpRequest request, @NonNull HttpResponse response,
        @NonNull RequestHandler handler) {
        if (handler instanceof MethodHandler) {
            MethodHandler methodHandler = (MethodHandler) handler;
            Addition addition = methodHandler.getAddition();
            if (!isLogin(request, addition)) {
                throw new BasicException(401, AjaxResult.error(BaseApplication.getAppContext().getString(R.string.need_login)).toString());
            }
        }
        return false;
    }

    private boolean isNeedLogin(Addition addition) {
        if (addition == null) {
            return false;
        }

        String[] stringType = addition.getStringType();
        if (StringUtil.isEmpty(stringType)) {
            return false;
        }

        boolean[] booleanType = addition.getBooleanType();
        if (booleanType == null || booleanType.length == 0) {
            return false;
        }
        return stringType[0].equalsIgnoreCase("needLogin") && booleanType[0];
    }

    private boolean isLogin(HttpRequest request, Addition addition) {
        if (isNeedLogin(addition)) {
            String token = TokenService.getInstance().getToken(request);
            if(StringUtil.isEmpty(token)) {
                return false;
            }else{
                LoginUser loginUser = TokenService.getInstance().findUserByToken(token);
                if(loginUser == null){
                    return false;
                }
            }
        }
        return true;
    }
}