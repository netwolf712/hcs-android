package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.TimeSlotTypeEnum;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.entity.TimeSlot;
import com.hcs.android.business.service.MulticastGroupService;
import com.hcs.android.business.service.TimeSlotService;
import com.hcs.android.business.util.GroupUtil;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 定时广播管理器
 */
public class AutoMulticastManager {
    private final Context mContext;

    /**
     * 主机号码
     */
    private String mMasterNo;
    /**
     * 定时分区映射表
     * uid,MulticastGroupModel
     */
    private final Map<Integer, MulticastGroupModel> mAutoMulticastGroupMap = new HashMap<>();

    /**
     * 自动广播定时器
     */
    private RobustTimer mAutoMulticastTimer;

    private final Object mSynObj = new Object();
    private AutoMulticastManager(){
        mContext = BusinessApplication.getAppContext();
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final AutoMulticastManager mInstance = new AutoMulticastManager();
    }

    public static AutoMulticastManager getInstance(){
        return AutoMulticastManager.MInstanceHolder.mInstance;
    }
    /**
     * 获取定时器的自动检测周期
     * 单位毫秒
     */
    public Integer getAutoCheckPeriod(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_AUTO_CHECK_PERIOD
                ,mContext.getResources().getInteger(R.integer.default_audio_multicast_auto_check_period));
    }

    /**
     * 设置定时器的自动检测周期
     * @param autoCheckPeriod 自动检测周期，单位毫秒
     */
    public void setAutoCheckPeriod(int autoCheckPeriod){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_AUTO_CHECK_PERIOD,autoCheckPeriod);
    }

    /**
     * 加载自动广播的配置列表
     * @param masterNo 主机号码
     */
    public void loadAutoMulticastModels(String masterNo){
        mMasterNo = masterNo;
        synchronized (mAutoMulticastGroupMap) {
            mAutoMulticastGroupMap.clear();
            List<MulticastGroup> groupList = MulticastGroupService.getInstance().getTimeSlotMulticastGroupList();
            if (!StringUtil.isEmpty(groupList)) {
                for (MulticastGroup multicastGroup : groupList) {
                    MulticastGroupModel multicastGroupModel = GroupUtil.convertToModel(multicastGroup, mContext, mMasterNo);
                    multicastGroupModel.setTimeSlot(TimeSlotService.getInstance().getTimeSlotById(multicastGroup.getUid()));
                    mAutoMulticastGroupMap.put(multicastGroup.getUid(),multicastGroupModel);
                }
            }
        }
    }

    /**
     * 处理自动广播逻辑
     */
    private void handleAutoMulticast(){
        //只有广播管理器准备好之后才能工作
        if(!AudioMulticastManager.getInstance().isReady()){
            return;
        }
        Collection<MulticastGroupModel> multicastGroupModelList = mAutoMulticastGroupMap.values();
        if(multicastGroupModelList.size() > 0){
            Iterator<MulticastGroupModel> it = multicastGroupModelList.iterator();
            long currentTime = System.currentTimeMillis();
            while(it.hasNext()) {
                MulticastGroupModel multicastGroupModel = it.next();
                TimeSlot timeSlot = multicastGroupModel.getTimeSlot();
                if(TimeSlotManager.getInstance().isInTime(currentTime,timeSlot)){
                    //如果在时间段内，且没在广播，则开启广播功能
                    if(!multicastGroupModel.isPlaying() && !multicastGroupModel.isTalking()){
                        AudioMulticastManager.getInstance().startAudioMulticast(multicastGroupModel);
                    }
                }else{
                    if(multicastGroupModel.isPlaying()){
                        //如果没在时间段内，且在广播，则关闭广播功能
                        AudioMulticastManager.getInstance().stopAudioMulticast(multicastGroupModel);
                    }
                }

            }
        }
    }

    /**
     * 开启自动广播功能
     */
    public void startAutoMulticastTimer(){
        startAutoMulticastTimer(getAutoCheckPeriod());
    }

    /**
     * 开启自动广播定时器
     */
    private void startAutoMulticastTimer(int period){
        synchronized (mSynObj) {
            if (mAutoMulticastTimer != null) {
                mAutoMulticastTimer = new RobustTimer();
                RobustTimerTask timerTask = new RobustTimerTask() {
                    @Override
                    public void run() {
                        handleAutoMulticast();
                    }
                };
                mAutoMulticastTimer.schedule(timerTask, 0, period);
            }
        }
    }

    /**
     * 关闭自动广播定时器
     */
    public void stopAutoMulticastTimer(){
        synchronized (mSynObj){
            if(mAutoMulticastTimer != null){
                //停止定时器
                mAutoMulticastTimer.cancel();
                mAutoMulticastTimer = null;
            }
        }
    }

    /**
     * 从缓存中获取自动广播的配置列表
     */
    public Collection<MulticastGroupModel> getAutoMulticastModelFromCash(){
        return mAutoMulticastGroupMap.values();
    }

    public void updateAutoMulticastGroupModel(@NonNull MulticastGroupModel multicastGroupModel){
        if(multicastGroupModel.getTimeSlot() == null){
            return;
        }
        //先保存时间段
        TimeSlotManager.getInstance().updateTimeSlot(multicastGroupModel.getTimeSlot());
        //将时间段id赋值给分区配置
        multicastGroupModel.getMulticastGroup().setTimeSlotId(multicastGroupModel.getTimeSlot().getUid());
        //再保存分区配置
        MulticastGroupService.getInstance().updateMulticastGroup(multicastGroupModel.getMulticastGroup());
        synchronized (mAutoMulticastGroupMap){
            mAutoMulticastGroupMap.put(multicastGroupModel.getMulticastGroup().getUid(),multicastGroupModel);
        }
    }

    public void removeAutoMulticastGroupModel(@NonNull Integer uid){
        synchronized (mAutoMulticastGroupMap){
            MulticastGroupModel multicastGroupModel = mAutoMulticastGroupMap.remove(uid);
            if(multicastGroupModel != null) {
                //如果正在播放的，则先停止播放
                if(multicastGroupModel.isPlaying()){
                    AudioMulticastManager.getInstance().stopAudioMulticast(multicastGroupModel);
                }
                //清除时间段
                TimeSlotManager.getInstance().removeTimeSlot(TimeSlotTypeEnum.AUTO_AUDIO_MULTICAST,multicastGroupModel.getMulticastGroup().getTimeSlotId());
                //清除分组
                MulticastGroupService.getInstance().deleteMulticastGroup(multicastGroupModel.getMulticastGroup());
            }
        }
    }
}
