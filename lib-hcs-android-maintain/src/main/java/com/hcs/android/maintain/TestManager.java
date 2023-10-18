package com.hcs.android.maintain;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.maintain.entity.RequestTest;
import com.hcs.android.maintain.entity.ResponseTestStatus;
/**
 * 测试管理器
 */
public class TestManager {
    private final Context mContext;

    /**
     * 测试状态
     */
    private final ResponseTestStatus mTestStatus;

    private TestManager(Context context){
        mContext = context;
        mTestStatus = new ResponseTestStatus();
        mTestStatus.setStatus(ResponseTestStatus.STATUS_IDLE);
    }

    @SuppressLint("StaticFieldLeak")
    private static TestManager mInstance = null;
    public static TestManager getInstance(){
        if(mInstance == null){
            synchronized (TestManager.class) {
                if(mInstance == null) {
                    mInstance = new TestManager(BaseApplication.getAppContext());
                }
            }
        }
        return mInstance;
    }

    public void handleTest(@NonNull RequestTest requestTest){
        if(requestTest.isStartTest()) {
            mTestStatus.setStatus(ResponseTestStatus.STATUS_TESTING);
            mTestStatus.setResult("");
            new Thread(() -> {
                String result = ExeCommand.executeSuCmd(requestTest.getTestCommand());
                mTestStatus.setStatus(ResponseTestStatus.STATUS_FINISHED);
                mTestStatus.setResult(result);
            }).start();
        }else{
            mTestStatus.setStatus(ResponseTestStatus.STATUS_IDLE);
            mTestStatus.setResult("");
        }
    }

    public ResponseTestStatus getTestStatus(){
        return mTestStatus;
    }
}
