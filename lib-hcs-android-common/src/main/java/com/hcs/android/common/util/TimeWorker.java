package com.hcs.android.common.util;

import android.os.Looper;

import java.util.concurrent.Semaphore;

/**
 * 多线程的定时工作器
 */
public class TimeWorker {
    private boolean mRunning = false;

    /**
     * 工作信号量
     * 采用定时器形式定时触发
     */
    private final Semaphore mWorkSem = new Semaphore(0);
    private RobustTimer mTimer;
    /**
     * 定时工作内容
     */
    private Runnable mPeriodWork;
    public void setPeriodWork(Runnable r){
        mPeriodWork = r;
    }


    /**
     * 开始工作
     * @param period 工作频率
     * @param r 工作内容
     */
    public void startWork(long period,Runnable r){
        setPeriodWork(r);
        mTimer = new RobustTimer(true);
        RobustTimerTask lTask = new RobustTimerTask() {
            @Override
            public void run() {
                mWorkSem.release(1);
            }
        };
        mTimer.schedule(lTask, 0, period);
        //开启线程
        startWorkThread();
    }

    /**
     * 停止工作
     */
    public void stopWork(){
        mRunning = false;
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 开启工作线程
     */
    public void startWorkThread(){
        if(!mRunning) {
            mRunning = true;
            new Thread(()->{
                while (mRunning) {
                    try {
                        mWorkSem.acquire(1);
                        if (mPeriodWork != null) {
                            mPeriodWork.run();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
}
