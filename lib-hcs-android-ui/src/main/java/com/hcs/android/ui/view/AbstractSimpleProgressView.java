package com.hcs.android.ui.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;

/**
 * 简易的进度条
 * 先用进度条做一个最简单的能量变化条，哈哈
 */
abstract public class AbstractSimpleProgressView {
    protected Context mContext;

    /**
     * 进度控件
     */
    protected SeekBar mProgressBar;

    protected Boolean mShow = false;

    private final Object mSynObj = new Object();
    /**
     * 定时器
     */
    protected RobustTimer mRobustTimer;

    private IRefreshListener mRefreshListener;

    /**
     * 如果按住滚动条，则定时器先不刷新
     */
    private boolean mSeekBarTouched = false;

    private int mCurrentValue = 0;

    public AbstractSimpleProgressView(Context context, ViewGroup viewGroup){
        mContext = context;
        mProgressBar = new SeekBar(context,null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        viewGroup.addView(mProgressBar);

        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int movedTo = -1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mSeekBarTouched){
                    movedTo = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekBarTouched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mSeekBarTouched && movedTo >= 0){
                    if(mRefreshListener != null){
                        mRefreshListener.onRefresh(movedTo,true);
                    }
                    mCurrentValue = movedTo;
                }
                mSeekBarTouched = false;
                movedTo = -1;
            }
        });
    }

    /**
     * 获取进度条的最大值
     * @return 最大值
     */
    abstract int getMaxValue();

    /**
     * 获取进度条的当前值
     * @return 当前值
     */
    abstract int getCurrentValue();

    /**
     * 获取定时器刷新周期，单位毫秒
     * @return 刷新周期
     */
    abstract int getRefreshPeriod();

    public void startTimer(){
        if(mRobustTimer == null){
            mProgressBar.setMax(getMaxValue());
            mRobustTimer = new RobustTimer();
            mRobustTimer.schedule(new RobustTimerTask() {
                @Override
                public void run() {
                    synchronized (mSynObj) {
                        if(!mSeekBarTouched) {
                            if(mCurrentValue != getCurrentValue()) {
                                mCurrentValue = getCurrentValue();
                                mProgressBar.setProgress(mCurrentValue);
                                if (mRefreshListener != null) {
                                    mRefreshListener.onRefresh(mCurrentValue,false);
                                }
                            }
                        }
                    }
                }
            }, 0, getRefreshPeriod());
        }
    }

    /**
     * 关闭音频解析器
     */
    public void stop(){
        stopTimer();
    }
    /**
     * 关闭定时器
     */
    public void stopTimer(){

        if(mRobustTimer != null){
            synchronized (mSynObj) {
                mShow = false;
                mRobustTimer.cancel();
                mRobustTimer = null;
            }
        }
    }

    public void setRefreshListener(IRefreshListener refreshListener){
        mRefreshListener = refreshListener;
    }
    /**
     * 刷新监听器
     */
    public interface IRefreshListener{
        /**
         * 更新事件
         * @param currentValue 当前值
         * @param changedByUser 是否被用户改变
         */
        void onRefresh(int currentValue,boolean changedByUser);
    }
}
