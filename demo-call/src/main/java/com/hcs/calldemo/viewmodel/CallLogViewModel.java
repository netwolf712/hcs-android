package com.hcs.calldemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.call.LinphoneService;
import com.hcs.android.call.api.CallHelper;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.calldemo.entity.CallLogBo;
import com.hcs.calldemo.factory.CommonViewModel;

import org.linphone.core.CallLog;

import java.util.ArrayList;
import java.util.List;

public class CallLogViewModel extends BaseRefreshViewModel<CallLogBo, CommonViewModel> {
    private RobustTimer mTimer;
    private boolean mWaiting = false;


    public CallLogViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        mTimer = new RobustTimer(true);
    }

    private List<CallLogBo> convertCallLogList(CallLog[] callLogs){
        List<CallLogBo>  callLogBoList = new ArrayList<>();
        if (callLogs != null && callLogs.length > 0) {
            for (CallLog callLog : callLogs) {
                CallLogBo callLogBo = new CallLogBo(callLog);
                callLogBoList.add(callLogBo);
            }
        }
        return callLogBoList;
    }
    @Override
    public void refreshData() {
        postShowNoDataViewEvent(true);
        if(!LinphoneService.isReady()){
            waitLinphoneReady();
            return;
        }
        postShowNoDataViewEvent(false);
        mList.clear();

        CallLog[] callLogs = CallHelper.getCallLogs();
        mList.addAll(convertCallLogList(callLogs));

        postStopRefreshEvent();
    }

    @Override
    public void loadMore() {
        refreshData();
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
                    refreshData();
                });
            }
        };
        mWaiting = true;
        mTimer.schedule(timerTask, 30, 30);
    }
}
