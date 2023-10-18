package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StepMasterTypeEnum;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.entity.StepMasterModel;
import com.hcs.android.business.service.StepMasterService;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 继任设备管理器
 */
public class StepMasterManager {
    private final Context mContext;
    /**
     * 上级主机
     * key=主机等级
     */
    private final Map<Integer,StepMaster> mSuperiorMasterMap;


    /**
     * 等待被呼叫的主机
     */
    private final List<StepMasterModel> mWaitMasters;


    private final Map<StepMasterTypeEnum,List<StepMaster>> mStepMasterMap;
    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final StepMasterManager mInstance = new StepMasterManager();
    }

    public static StepMasterManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    private StepMasterManager(){
        mStepMasterMap = new HashMap<>();
        mContext = BusinessApplication.getAppContext();
        mWaitMasters = new ArrayList<>();
        mSuperiorMasterMap = new HashMap<>();
        loadAllData();
        loadSuperiorMasters();
    }


    //加载各类型的主机
    public void loadAllData(){
        synchronized (mStepMasterMap){
            for(StepMasterTypeEnum stepMasterTypeEnum : StepMasterTypeEnum.values()){
                mStepMasterMap.put(stepMasterTypeEnum,loadMasters(stepMasterTypeEnum));
            }
            sortStepMasterList(mStepMasterMap.get(StepMasterTypeEnum.SUPERIOR));
        }
    }

    /**
     * 加载上级主机
     */
    private void loadSuperiorMasters(){
        synchronized (mStepMasterMap){
            List<StepMaster> stepMasters = mStepMasterMap.get(StepMasterTypeEnum.SUPERIOR);
            mSuperiorMasterMap.clear();
            if(!StringUtil.isEmpty(stepMasters)){
                for(StepMaster stepMaster : stepMasters){
                    mSuperiorMasterMap.put(stepMaster.getMasterLevel(),stepMaster);
                }
            }

        }
    }
    private List<StepMaster> loadMasters(@NonNull StepMasterTypeEnum typeEnum){
        return StepMasterService.getInstance().getStepMasterList(typeEnum.getValue());
    }

    /**
     * 获取缓存中某种类型的继任主机的列表
     */
    public List<StepMaster> getStepMasterList(@NonNull StepMasterTypeEnum typeEnum){
        synchronized (mStepMasterMap) {
            return mStepMasterMap.get(typeEnum);
        }
    }
    /**
     * 对列表进行重排序
     * 否则从map里取上来的是乱的
     */
    private List<StepMaster> sortStepMasterList(List<StepMaster> stepMasterList){
        //排序
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stepMasterList.sort(Comparator.comparingInt(StepMaster::getMasterLevel));
        }
        return stepMasterList;
    }

    public void reloadData(StepMasterTypeEnum typeEnum){
        if(typeEnum == null){
            loadAllData();
        }else{
            synchronized (mStepMasterMap){
                mStepMasterMap.put(typeEnum,loadMasters(typeEnum));
            }
        }
    }

    /**
     * 更新某一个种类的继任主机
     * 只能一起更新
     * 因为需要将之前的全部删除再次写入（没错，就是为算法偷懒）
     * @param stepMasterList 继任主机列表
     * @param typeEnum 类型，为null表示全部
     */
    public void updateStepMasters(List<StepMaster> stepMasterList, StepMasterTypeEnum typeEnum,Long updateTime){
        if(typeEnum == null){
         StepMasterService.getInstance().deleteAll();
        }else {
            StepMasterService.getInstance().deleteByType(typeEnum.getValue());
            if(typeEnum == StepMasterTypeEnum.SUPERIOR){
                //如果是上级主机，则还需更新呼叫周期
                if(!StringUtil.isEmpty(stepMasterList)) {
                    SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MAX_CALL_WAIT_TIME, stepMasterList.get(0).getCallWaitTime());
                }
            }
        }
        StepMasterService.getInstance().insertAll(stepMasterList);
        reloadData(typeEnum);
        //更新继任主机刷新时间
        long currentTime = updateTime == null ? System.currentTimeMillis() : updateTime;
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.STEP_MASTER_UPDATE_TIME,currentTime);
    }

    /**
     * 获取所有的继任主机
     */
    public List<StepMaster> getAllStepMasterList(){
        List<StepMaster> masterList = new ArrayList<>();
        for(StepMasterTypeEnum stepMasterTypeEnum : StepMasterTypeEnum.values()){
            masterList.addAll(getStepMasterList(stepMasterTypeEnum));
        }
        return masterList;
    }

    /**
     * 获取继任主机的最后一次配置的时间
     */
    public long getLastUpdateTime(){
        return SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.STEP_MASTER_UPDATE_TIME,mContext.getResources().getInteger(R.integer.default_step_master_update_time));
    }

    /**
     * 开启主机的呼叫等待工作
     * @param callWaitListener 监听可以呼叫哪些主机了
     */
    public void startCallWaitTimer(ISimpleCustomer<StepMaster> callWaitListener){
        synchronized (mWaitMasters){
            //加载所有附加主机
            synchronized (mStepMasterMap){
                List<StepMaster> appendMasterList = mStepMasterMap.get(StepMasterTypeEnum.APPEND);
                if(!StringUtil.isEmpty(appendMasterList)){
                    for(StepMaster stepMaster : appendMasterList){
                        StepMasterModel stepMasterModel = new StepMasterModel(stepMaster,stepMaster.getCallWaitTime());
                        stepMasterModel.startCallWaitTimer(callWaitListener);
                        mWaitMasters.add(stepMasterModel);
                    }
                }
            }
        }
    }

    /**
     * 停止呼叫等级计时器
     */
    public void stopCallWaitTimer(){
        synchronized (mWaitMasters){
            if(!StringUtil.isEmpty(mWaitMasters)){
                for(StepMasterModel stepMasterModel : mWaitMasters){
                    stepMasterModel.stopCallWaitTimer();
                }
                mWaitMasters.clear();
            }
        }
    }

    private boolean isInBeTrustNo(String beTrustNo){
        synchronized (mStepMasterMap){
            List<StepMaster> beTrustMasterList = mStepMasterMap.get(StepMasterTypeEnum.BE_TRUST);
            if(StringUtil.isEmpty(beTrustMasterList)){
                return false;
            }
            for(StepMaster stepMaster : beTrustMasterList) {
                if (StringUtil.equalsIgnoreCase(stepMaster.getMasterNo(), beTrustNo)) {
                    return true;
                }
            }
            return false;
        }
    }
    public void removeBeTrustNo(String beTrustNo){
        synchronized (mStepMasterMap) {
            List<StepMaster> beTrustMasterList = mStepMasterMap.get(StepMasterTypeEnum.BE_TRUST);
            if (!StringUtil.isEmpty(beTrustMasterList)){
                for(StepMaster stepMaster : beTrustMasterList){
                    if(StringUtil.equalsIgnoreCase(stepMaster.getMasterNo(),beTrustNo)){
                        StepMasterService.getInstance().deleteStepMaster(stepMaster);
                        beTrustMasterList.remove(stepMaster);
                        return;
                    }
                }
            }
        }
    }

    public void addBeTrustNo(String beTrustNo){
        synchronized (mStepMasterMap){
            List<StepMaster> beTrustMasterList = mStepMasterMap.get(StepMasterTypeEnum.BE_TRUST);
            if(!isInBeTrustNo(beTrustNo)){
                StepMaster stepMaster = new StepMaster();
                stepMaster.setMasterNo(beTrustNo);
                StepMasterService.getInstance().updateStepMaster(stepMaster);
                if(beTrustMasterList == null){
                    beTrustMasterList = new ArrayList<>();
                    mStepMasterMap.put(StepMasterTypeEnum.BE_TRUST,beTrustMasterList);
                }
                beTrustMasterList.add(stepMaster);
            }
        }
    }
}
