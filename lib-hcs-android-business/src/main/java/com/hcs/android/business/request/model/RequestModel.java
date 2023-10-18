package com.hcs.android.business.request.model;


import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestDownload;
import com.hcs.android.business.request.service.DownloadListener;
import com.hcs.android.business.request.service.DownloadService;
import com.hcs.android.business.request.service.RequestService;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.log.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestModel{

    private final RequestService mRequestService;
    public RequestModel() {
        mRequestService = RetrofitHelper.getInstance().getService(RequestService.class);
    }

    private RequestService getRequestService(){
        return mRequestService;
    }

    /**
     * 获取设备列表
     * @param selfModel 本机信息
     * @param groupName 设备分组名称
     * @param currentPage 当前页
     * @param pageSize 每页大小
     * @return 设备列表
     */
    public Observable<List<DeviceModel>> getDeviceList(DeviceModel selfModel, String groupName, int currentPage, int pageSize) {
        //todo:添加从服务器获取获取设备列表的实际操作
        return null;
    }

    public void updateDeviceInfo(@NonNull RequestDTO<DeviceModel> requestDTO){
        if(!RetrofitHelper.getInstance().isNeedLogin()) {
            mRequestService.updateDeviceInfo(requestDTO);
        }
    }

    /**
     * 从服务器下载文件
     * @param requestDTO 请求内容
     * @param savePath 文件保存路径
     * @param downloadListener 下载进度监听器
     */
    public void downloadFile(@NonNull RequestDTO<RequestDownload> requestDTO, String savePath, DownloadListener downloadListener){
        if(!RetrofitHelper.getInstance().isNeedLogin()) {
            DownloadService downloadService = RetrofitHelper.getInstance().getService(DownloadService.class);
            Call<ResponseBody> call = downloadService.download(requestDTO);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    File file = new File(savePath);
                    FileUtil.createOrExistsFile(file);
                    //下载文件放在子线程
                    new Thread(()-> writeFile2Disk(response, file, downloadListener)).start();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    if(downloadListener != null) {
                        downloadListener.onFailure(BusinessApplication.getAppContext().getString(R.string.error_network));
                    }
                }
            });
        }
    }

    private void writeFile2Disk(@NonNull Response<ResponseBody> response, File file, DownloadListener downloadListener) {
        if(downloadListener != null) {
            downloadListener.onStart();
        }
        long currentLength = 0;
        OutputStream os = null;

        if (response.body() == null) {
            if(downloadListener != null) {
                downloadListener.onFailure(BusinessApplication.getAppContext().getString(R.string.error_resource));
            }
            return;
        }
        InputStream is = response.body().byteStream();
        long totalLength = response.body().contentLength();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                KLog.i( "当前进度: " + currentLength);
                if(downloadListener != null) {
                    downloadListener.onProgress((int) (100 * currentLength / totalLength));
                }
                if ((int) (100 * currentLength / totalLength) == 100) {
                    if(downloadListener != null) {
                        downloadListener.onFinish(file.getAbsolutePath());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            if(downloadListener != null) {
                downloadListener.onFailure(BusinessApplication.getAppContext().getString(R.string.error_no_file));
            }
            e.printStackTrace();
        } catch (IOException e) {
            if(downloadListener != null) {
                downloadListener.onFailure(BusinessApplication.getAppContext().getString(R.string.error_io));
            }
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
