package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.TrustStateEnum;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;

/**
 * 托管逻辑管理器
 */
public class TrustManager {
    private final Context mContext;
    private ISimpleCustomer<Object> mListener;
    /**
     * 托管定时器
     */
    private RobustTimer mTrustCheckTimer;

    /**
     * 自动托管定时器
     */
    private RobustTimer mAutoTrustCheckTimer;
    private ISimpleCustomer<Object> mAutoTrustListener;

    private final Object mSynObj = new Object();
    private TrustManager(){
        mContext = BusinessApplication.getAppContext();
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final TrustManager mInstance = new TrustManager();
    }

    public static TrustManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    /**
     * 托管告知周期
     */
    public int getTrustCheckTime(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.TRUST_CHECK_TIME,mContext.getResources().getInteger(R.integer.default_trust_check_time));
    }
    public void setTrustCheckTime(int period){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.TRUST_CHECK_TIME,period);
    }

    /**
     * 自动托管检查周期
     */
    public int getAutoTrustCheckTime(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.AUTO_TRUST_CHECK_TIME,mContext.getResources().getInteger(R.integer.default_auto_trust_check_time));
    }
    public void setAutoTrustCheckTime(int period){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.AUTO_TRUST_CHECK_TIME,period);
    }
    /**
     * 开启外呼等待摘机的定时器
     */
    public void startTimer(ISimpleCustomer<Object> listener,int period){
        mListener = listener;
        synchronized (mSynObj){
            if(mTrustCheckTimer == null && mListener != null){
                mTrustCheckTimer = new RobustTimer(true);
                RobustTimerTask timerTask = new RobustTimerTask() {
                    @Override
                    public void run() {
                        //执行超时回调
                        mListener.accept(null);
                    }
                };
                mTrustCheckTimer.schedule(timerTask, 0, period);
            }
        }
    }

    public void stopTimer(){
        synchronized (mSynObj){
            if(mTrustCheckTimer != null){
                //停止定时器
                mTrustCheckTimer.cancel();
                mTrustCheckTimer = null;
            }
        }
    }

    /**
     * 开启外呼等待摘机的定时器
     */
    public void startAutoTrustTimer(ISimpleCustomer<Object> listener,int period){
        mAutoTrustListener = listener;
        synchronized (mSynObj){
            if(mAutoTrustCheckTimer == null && mAutoTrustListener != null){
                mAutoTrustCheckTimer = new RobustTimer(true);
                RobustTimerTask timerTask = new RobustTimerTask() {
                    @Override
                    public void run() {
                        //执行超时回调
                        mAutoTrustListener.accept(null);
                    }
                };
                mAutoTrustCheckTimer.schedule(timerTask, 0, period);
            }
        }
    }

    public void stopAutoTrustTimer(){
        synchronized (mSynObj){
            if(mAutoTrustCheckTimer != null){
                //停止定时器
                mAutoTrustCheckTimer.cancel();
                mAutoTrustCheckTimer = null;
            }
        }
    }

    //以下功能主要个请求托管的主机用
    /**
     * 获取托管状态
     */
    public TrustStateEnum getTrustState(){
        int state = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.TRUST_STATE,mContext.getResources().getInteger(R.integer.default_trust_state));
        return TrustStateEnum.findById(state);
    }

    /**
     * 设置托管状态
     * @param trustState 托管状态
     */
    public void setTrustState(int trustState){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.TRUST_STATE,trustState);
    }

    /**
     * 获取托管主机号码
     */
    public String getTrustNo(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.TRUST_PHONE_NO,mContext.getString(R.string.default_trust_phone_no));
    }

    /**
     * 设置托管主机号码
     */
    public void setTrustNo(String trustNo){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.TRUST_PHONE_NO,trustNo);
    }

    public boolean isAutoTrust(){
        return SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.AUTO_TRUST,mContext.getResources().getBoolean(R.bool.default_auto_trust));
    }
    public void setAutoTrust(boolean enable){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.AUTO_TRUST,enable);
    }
}
