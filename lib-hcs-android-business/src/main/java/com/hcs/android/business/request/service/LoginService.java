package com.hcs.android.business.request.service;

import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseLogin;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    /**
     * 设备登录
     */
    @POST("/device/login")
    Observable<RequestDTO<ResponseLogin>> login(@Body RequestDTO requestDTO);
}
