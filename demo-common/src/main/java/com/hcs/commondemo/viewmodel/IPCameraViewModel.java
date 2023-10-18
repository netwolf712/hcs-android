package com.hcs.commondemo.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.onvif.onvif.OnvifHelper;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.commondemo.entity.RecordBo;
import com.hcs.commondemo.factory.CommonViewModel;

import java.util.List;

public class IPCameraViewModel extends BaseRefreshViewModel<RecordBo, CommonViewModel> {
    /**
     * 网络摄像头ONVIF协议的访问账号
     */
    private String onvifUser = "hcsxxx";
    private String onvifPassword = "hcsxxx";
    private String onvifAddress = "192.168.1.2";

    public void setOnvifUser(String onvifUser){
        this.onvifUser = onvifUser;
    }
    public void setOnvifPassword(String onvifPassword){
        this.onvifPassword = onvifPassword;
    }
    public void setOnvifAddress(String onvifAddress){
        this.onvifAddress = onvifAddress;
    }
    private Activity mActivity;


    public IPCameraViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
    }


    @Override
    public void refreshData() {
        postShowNoDataViewEvent(true);

        postShowNoDataViewEvent(false);
        scanFile();
        postStopRefreshEvent();
    }

    @Override
    public void loadMore() {
        refreshData();
    }

    public void scanFile(){
        mList.clear();

        OnvifHelper.buildManager(BaseApplication.getAppContext(),onvifAddress,onvifUser,onvifPassword, onvifManager -> {
            List<String> videoList = onvifManager.getVideoStreams();
            if(!StringUtil.isEmpty(videoList)) {
                UIThreadUtil.runOnUiThread(() -> {
                    if(!StringUtil.isEmpty(videoList)){
                        for (String videoPath : videoList){
                            RecordBo recordBo = new RecordBo();
                            recordBo.setFilePath(videoPath);
                            recordBo.setName("海康摄像头");
                            recordBo.setDuration("9999");
                            recordBo.setStartTime("2022-07-26 15:27");
                            mList.add(recordBo);
                        }
                    }
                });
            }
        });
    }

}
