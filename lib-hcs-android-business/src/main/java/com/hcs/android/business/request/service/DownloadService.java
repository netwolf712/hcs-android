package com.hcs.android.business.request.service;

import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestDownload;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface DownloadService {
    /**
     * 设备登录
     */
    @Streaming
    @POST("/device/download")
    Call<ResponseBody> download(@Body RequestDTO<RequestDownload>  requestDTO);
}
