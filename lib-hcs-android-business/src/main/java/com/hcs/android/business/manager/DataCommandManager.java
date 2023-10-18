package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.RequestData;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据命令管理器
 */
public class DataCommandManager {
    /**
     * 当前的命令索引
     */
    private Long mNextCommandIndex;
    /**
     * 命令映射表
     */
    private final Map<Long, RequestData> mRequestMap = new HashMap<>();

    /**
     * 命令回调处理函数
     */
    private final IDataCommandListener mDataCommandListener;

    /**
     * 熔断定时器
     */
    private RobustTimer mFusingTimer;
    /**
     * 定时器工作间隔，单位毫秒
     */
    private final static int TIMER_SPAN = 500;
    /**
     * 上一次处理命令的时间
     */
    private long mLastHandleTime;

    private final Context mContext;

    private final int mMaxWaitDataCommandCount;
    private final int mMaxWaitDataCommandTime;
    public DataCommandManager(IDataCommandListener dataCommandListener,Context context){
        mContext = context;
        mNextCommandIndex = SettingsHelper.getInstance(BusinessApplication.getAppContext()).getLong(PreferenceConstant.LAST_DATA_COMMAND_INDEX,
                mContext.getResources().getInteger(R.integer.default_last_data_command_index)) + 1;

        mMaxWaitDataCommandCount = SettingsHelper.getInstance(BusinessApplication.getAppContext()).getInt(PreferenceConstant.MAX_WAIT_DATA_COMMAND_COUNT,
                mContext.getResources().getInteger(R.integer.default_max_wait_data_command_count));

        mMaxWaitDataCommandTime = SettingsHelper.getInstance(BusinessApplication.getAppContext()).getInt(PreferenceConstant.MAX_WAIT_DATA_COMMAND_TIME,
                mContext.getResources().getInteger(R.integer.default_max_wait_data_command_time));

        mLastHandleTime = System.currentTimeMillis();

        mDataCommandListener = dataCommandListener;

        //开启熔断定时器
        startFusingTimer();

    }

    /**
     * 熔断定时器
     */
    private void startFusingTimer(){
        mFusingTimer = new RobustTimer(true);
        RobustTimerTask timerTask = new RobustTimerTask() {
            @Override
            public void run() {
                synchronized (mRequestMap) {
                    if(mRequestMap.size() > 0) {
                        if (System.currentTimeMillis() - mLastHandleTime >= mMaxWaitDataCommandTime ) {
                            //如果超过熔断时间，则执行熔断操作
                            if (mDataCommandListener != null) {
                                mDataCommandListener.fusing();
                            }
                            mRequestMap.clear();
                            mLastHandleTime = System.currentTimeMillis();
                        }
                    }else {
                        mLastHandleTime = System.currentTimeMillis();
                    }
                }
            }
        };
        mFusingTimer.schedule(timerTask, 0, TIMER_SPAN);
    }
    /**
     * 是否下一个命令
     */
    private boolean isNextCommand(@NonNull RequestData requestData){
        return requestData.getCommandIndex().equals(mNextCommandIndex);
    }

    /**
     * 告诉调用者处理任务
     */
    private void handleCommand(RequestData requestData){
        if(mDataCommandListener != null){
            mDataCommandListener.handle(requestData);
        }
        mLastHandleTime = System.currentTimeMillis();
        mNextCommandIndex = requestData.getCommandIndex();
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.LAST_DATA_COMMAND_INDEX,mNextCommandIndex);
        mNextCommandIndex++;
    }

    /**
     * 处理剩下的任务
     */
    private void handleNextData(){
        while (mRequestMap.get(mNextCommandIndex) != null) {
            RequestData tmp = mRequestMap.remove(mNextCommandIndex);
            handleCommand(tmp);
        }
    }
    public void addCommand(@NonNull RequestData requestData){
        synchronized (mRequestMap) {
            //如果是配置更新命令，则优先处理
            if (requestData.getCommandEnum() == CommandEnum.RSP_GET_CONFIG) {
                //扔掉这个命令之前的所有请求
                dropCommand(requestData.getCommandIndex());
                handleCommand(requestData);
                handleNextData();
                return;
            }
            //如果是之前的命令，则不予处理
            if (mNextCommandIndex > requestData.getCommandIndex()) {
                return;
            }
            if (isNextCommand((requestData))) {
                //如果是下一个任务，则直接处理
                handleCommand(requestData);
                //同时要判断是否可以清空缓存中的任务
                handleNextData();
            } else {
                mRequestMap.put(requestData.getCommandIndex(), requestData);
                if (mRequestMap.size() >= mMaxWaitDataCommandCount) {
                    //等待的命令长度超出则熔断
                    if (mDataCommandListener != null) {
                        mDataCommandListener.fusing();
                    }
                    mRequestMap.clear();
                }
            }
        }
    }

    /**
     * 将低于当前索引的命令丢弃
     * @param dataCommandIndex 最新的命令索引
     */
    public void dropCommand(Long dataCommandIndex){
        synchronized (mRequestMap){
            while (mNextCommandIndex <= dataCommandIndex){
                mRequestMap.remove(mNextCommandIndex++);
            }
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.LAST_DATA_COMMAND_INDEX,mNextCommandIndex);
        }
    }
    /**
     * 命令监听器
     */
    public interface IDataCommandListener{
        /**
         * 正常处理命令
         */
        void handle(RequestData requestData);

        /**
         * 熔断
         * 清空当前缓存的所有命令
         */
        void fusing();
    }
}
