package com.hcs.android.common;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.hcs.android.common.util.log.KLog;


/**
 * Description: <初始化应用程序><br>
 * Author:      mxdl<br>
 * Date:        2018/6/6<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class BaseApplication extends Application {
    /**
     * 应用启动时间
     */
    private final static long appStartTime = System.currentTimeMillis();
    @SuppressLint("StaticFieldLeak")
    private static BaseApplication mApplication;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mApplication = this;
        init();
    }

    /**
     * 初始化功能
     */
    private void init(){
        // 跟踪功能初始化
        KLog.init(true);
    }

    public static BaseApplication getInstance(){
        return mApplication;
    }
    public static Context getAppContext(){
        return mContext;
    }

    public static long getAppStartTime(){
        return appStartTime;
    }
}
