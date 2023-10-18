package com.hcs.calldemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.call.LinphoneService;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.call.api.IChatMessageListener;
import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.calldemo.R;
import com.hcs.calldemo.entity.ChatMessageBo;
import com.hcs.calldemo.entity.PhoneSettingBo;
import com.hcs.calldemo.factory.CommonViewModel;

import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class SettingViewModel  {
    private RobustTimer mTimer;
    private boolean mWaiting = false;

    private PhoneSettingBo phoneSettingBo = new PhoneSettingBo();
    public PhoneSettingBo getPhoneSettingBo(){
        return phoneSettingBo;
    }

    public SettingViewModel() {
        mTimer = new RobustTimer(true);
        waitLinphoneReady();
    }

    public void loadSettings(){
        phoneSettingBo.setAutoAnswer(LinphonePreferences.instance().isAutoAnswerEnabled());
        phoneSettingBo.setAutoAnswerTime(String.valueOf(LinphonePreferences.instance().getAutoAnswerTime()));
        phoneSettingBo.setDisplayName(LinphonePreferences.instance().getDefaultDisplayName());
        phoneSettingBo.setUsername(LinphonePreferences.instance().getDefaultUsername());
        phoneSettingBo.setVideoEnabled(LinphonePreferences.instance().isVideoEnabled());
    }
    public void saveAutoAnswerSet(){
        LinphonePreferences.instance().enableAutoAnswer(phoneSettingBo.isAutoAnswer());
        LinphonePreferences.instance().setAutoAnswerTime(Integer.valueOf(phoneSettingBo.getAutoAnswerTime()));
    }
    public void saveUserInfo(){
        LinphonePreferences.instance().setDefaultUsername(phoneSettingBo.getUsername());
        LinphonePreferences.instance().setDefaultDisplayName(phoneSettingBo.getDisplayName());
    }
    public void saveVideo(){
        LinphonePreferences.instance().enableVideo(phoneSettingBo.isVideoEnabled());
    }
    public void saveUseIPv6(){
        LinphonePreferences.instance().useIpv6(phoneSettingBo.isUseIPv6());
    }
    private void waitLinphoneReady(){
        if(mWaiting){
            return;
        }
        RobustTimerTask timerTask = new RobustTimerTask() {
            @Override
            public void run() {
                if(!LinphoneService.isReady()){
                    return;
                }
                mWaiting = false;
                mTimer.cancel();
                UIThreadUtil.runOnUiThread(()->{
                    loadSettings();
                });
            }
        };
        mWaiting = true;
        mTimer.schedule(timerTask, 30, 30);
    }

}
