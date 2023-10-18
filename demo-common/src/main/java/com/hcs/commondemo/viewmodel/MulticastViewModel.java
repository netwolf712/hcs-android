package com.hcs.commondemo.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;


import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.multicast.MulticastHelper;
import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.commondemo.R;
import com.hcs.commondemo.entity.MulticastMessageBo;
import com.hcs.commondemo.factory.CommonViewModel;


import java.util.ArrayList;
import java.util.Date;

public class MulticastViewModel extends BaseRefreshViewModel<MulticastMessageBo, CommonViewModel>{
    private RobustTimer mTimer;
    private boolean mWaiting = false;

    private MulticastHelper mMulticastHelper = null;

    /**
     * 广播地址
     */
    private String multicastAddress;
    public String getMulticastAddress(){
        return multicastAddress;
    }
    public void setMulticastAddress(String address){
        this.multicastAddress = multicastAddress;
    }

    /**
     * 广播端口
     */
    private String multicastPort;
    public String getMulticastPort(){
        return multicastPort;
    }
    public void setMulticastPort(String multicastPort){
        this.multicastPort = multicastPort;
    }
    /**
     * 要发送的消息
     */
    private String message;
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public MulticastViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        mTimer = new RobustTimer(true);
        multicastAddress = "239.1.2.3";
        multicastPort = "21236";
        message = "";
    }

    public void start(Activity activity){
        mMulticastHelper = new MulticastHelper(multicastAddress, Integer.valueOf(multicastPort), (data, len) -> {
            UIThreadUtil.runOnUiThread(()->{
                try {
                    String tmpStr = new String(data, "UTF-8");
                    MulticastMessageBo tmp = new MulticastMessageBo(tmpStr);
                    mList.add(tmp);
                }catch (Exception e){
                    KLog.e(e);
                }
            });
        });
        mMulticastHelper.start();
    }

    @Override
    public void refreshData() {
        postShowNoDataViewEvent(true);
        postShowNoDataViewEvent(false);
        mList.clear();
        postStopRefreshEvent();
    }

    @Override
    public void loadMore() {
        refreshData();
    }


    public void setMessage(){
        if(mMulticastHelper == null) {
            return;
        }
        if(StringUtil.isEmpty(message)){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.multicast_notice_message_can_not_empty));
            return;
        }
        MulticastMessageBo multicastMessageBo = new MulticastMessageBo();
        multicastMessageBo.setFromUser(mMulticastHelper.toString());
        multicastMessageBo.setSendTime(DateUtil.formatDate(new Date(System.currentTimeMillis()),DateUtil.FormatType.yyyyMMddHHmmss));
        multicastMessageBo.setMessage(message);
        try {
            mMulticastHelper.sendData(JsonUtils.toJsonString(multicastMessageBo).getBytes("UTF-8"));
            mList.add(multicastMessageBo);
        }catch (Exception e){
            KLog.e(e);
        }
    }
}
