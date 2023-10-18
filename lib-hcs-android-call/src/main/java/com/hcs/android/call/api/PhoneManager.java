package com.hcs.android.call.api;

import static android.content.Intent.ACTION_MAIN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.hcs.android.call.LinphoneService;
import com.hcs.android.common.BaseApplication;

import org.linphone.core.GlobalState;

/**
 * 电话管理类，提供给上层调用的主类
 * sdk被调用使优先要处理的类
 */
public class PhoneManager {
    private Handler mHandler;
    private Context mContext;
    private ServiceWaitThread mServiceThread;
    private PhoneListener mPhoneListener;

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final PhoneManager mInstance = new PhoneManager();
    }

    public static PhoneManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    /**
     * 初始化
     */
    public PhoneManager init(Context context){
        mContext = context;
        mHandler = new Handler();
        return this;
    }

    /**
     * 设置一个activity的上下文
     * 主要是权限申请时需要
     */
    public void setActivityContext(Activity context){
        LinphoneManager.getInstance().setActivityContext(context);
    }
    /**
     * 启动
     */
    public void start(){
        if (LinphoneService.isReady()) {
            onServiceReady();
        } else {
            // start linphone as background
            BaseApplication.getAppContext().startService(new Intent(ACTION_MAIN).setClass(mContext, LinphoneService.class));
            mServiceThread = new ServiceWaitThread();
            mServiceThread.start();
        }
    }

    public void start(PhoneListener phoneListener){
        mPhoneListener = phoneListener;
        start();
    }

    private void onServiceReady() {
        mHandler.postDelayed(
                () -> {
                    if(mPhoneListener != null){
                        mPhoneListener.onPhoneReady();
                    }
                },
                500);
    }

    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            boolean post = mHandler.post(
                    PhoneManager.this::onServiceReady);
            mServiceThread = null;
        }
    }

    /**
     * linphone是不是准备去好可以正常调用了
     */
    public boolean isPhoneReady(){
        return LinphoneManager.getInstance().getGlobalState() == GlobalState.On
                || LinphoneManager.getInstance().getGlobalState() == GlobalState.Ready;
    }

    public interface PhoneListener{
        /**
         * phone启动成功
         */
        void onPhoneReady();
    }
}
