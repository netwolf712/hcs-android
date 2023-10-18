package com.hcs.android.business.entity;

import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;

/**
 * 继任主机处理模块
 */
public class StepMasterModel {
    /**
     * 继任主机实体
     */
    private final StepMaster mStepMaster;

    private ISimpleCustomer<StepMaster> mListener;

    /**
     * 总的等待时间
     * 上级主机有个时间累加的过程
     */
    private final int mTotalWaitTime;
    /**
     * 外呼定时器
     */
    private RobustTimer mCallWaitTimer;

    public StepMasterModel(StepMaster stepMaster,int totalWaitTime){
        mStepMaster = stepMaster;
        mTotalWaitTime = totalWaitTime;
    }
    /**
     * 开启外呼等待摘机的定时器
     */
    public void startCallWaitTimer(ISimpleCustomer<StepMaster> listener){
        mListener = listener;
        synchronized (mStepMaster){
            if(mCallWaitTimer == null && mListener != null){
                mCallWaitTimer = new RobustTimer(true);
                RobustTimerTask timerTask = new RobustTimerTask() {
                    @Override
                    public void run() {
                        stopCallWaitTimer();
                        //执行超时回调
                        mListener.accept(mStepMaster);
                    }
                };
                mCallWaitTimer.schedule(timerTask, 0, mTotalWaitTime);
            }
        }
    }

    public void stopCallWaitTimer(){
        synchronized (mStepMaster){
            if(mCallWaitTimer != null){
                //停止定时器
                mCallWaitTimer.cancel();
                mCallWaitTimer = null;
            }
        }
    }

}
