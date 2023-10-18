package com.hcs.android.business.request.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.IPCamera;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestUpdateCapability;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.JsonUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

/**
 * 给分机用的请求模块
 * 不存在信息修改的功能
 * 只有数据的实时展示
 */
public class SlaveRequestModel {
    private final Context mContext;
    /**
     * 病员信息消费者
     */
    private ISimpleCustomer<DeviceModel> mConsumer;
    /**
     * 显示模板改变消费者
     */
    private ISimpleCustomer<Object> mTemplateListener;
    /**
     * 状态变化消费者
     */
    private ISimpleCustomer<CallModel> mStateListener;

    /**
     * 位置变化消费者
     */
    private ISimpleCustomer<PlaceModel> mPlaceListener;
    /**
     * 语音广播消费者
     */
    private ISimpleCustomer<Object> mAudioMulticastListener;
    private final DeviceModel mDeviceModel;
    private PlaceModel mPlaceModel;
    public DeviceModel getDeviceModel(){
        return mDeviceModel;
    }
    public PlaceModel getPlaceModel(){
        return mPlaceModel;
    }
    public SlaveRequestModel() {
        EventBus.getDefault().register(this);
        mContext = BusinessApplication.getAppContext();
        mDeviceModel = WorkManager.getInstance().getSelfInfo();
        mPlaceModel = WorkManager.getInstance().findPlaceModelByDevice(mDeviceModel);
    }

    public PlaceModel reloadPlaceModel(){
        mPlaceModel = WorkManager.getInstance().findPlaceModelByDevice(mDeviceModel);
        return mPlaceModel;
    }
    /**
     * 添加数据改变时的监听器
     */
    public void setDataChangeListener(ISimpleCustomer<DeviceModel> consumer){
        mConsumer = consumer;
    }

    /**
     * 添加模板改变时的监听器
     */
    public void setTemplateChangeListener(ISimpleCustomer<Object> consumer){
        mTemplateListener = consumer;
    }

    /**
     * 添加状态监听器
     */
    public void setStateListener(ISimpleCustomer<CallModel> consumer){
        mStateListener = consumer;
    }

    /**
     * 添加语音广播监听器
     */
    public void setAudioMulticastListener(ISimpleCustomer<Object> customer){
        mAudioMulticastListener = customer;
    }

    public void setPlaceListener(ISimpleCustomer<PlaceModel> placeListener){
        mPlaceListener = placeListener;
    }
    /**
     * 缓存更新
     * 更新的数据内容
     */
    @Subscriber(tag = EventBusConstant.HANDLE_CACHE_UPDATED, mode = ThreadMode.MAIN)
    public void handleCacheUpdated(final Object obj) {
        if(mConsumer != null){
            mConsumer.accept(mDeviceModel);
        }
    }

    /**
     * 位置更新
     * 更新的数据内容
     */
    @Subscriber(tag = EventBusConstant.HANDLE_PLACE_UPDATED, mode = ThreadMode.MAIN)
    public void handleCacheUpdated(final PlaceModel placeModel) {
        if(mPlaceListener != null){
            mPlaceModel = placeModel;
            mPlaceListener.accept(placeModel);
        }
    }

    /**
     * 模板更新
     * 需要对显示样式做更新
     * （整个界面重新加载）
     */
    @Subscriber(tag = EventBusConstant.HANDLE_TEMPLATE_UPDATED, mode = ThreadMode.MAIN)
    public void handleTemplateUpdated(final Object obj) {
        if(mTemplateListener != null){
            mTemplateListener.accept(obj);
        }
    }

    /**
     * 状态改变
     */
    @Subscriber(tag = EventBusConstant.HANDLE_STATE_CHANGED, mode = ThreadMode.MAIN)
    public void handleStateChanged(final CallModel callModel) {
        if(mStateListener != null){
            mStateListener.accept(callModel);
        }
    }

    /**
     * 语音广播提醒
     */
    @Subscriber(tag = EventBusConstant.AUDIO_MULTICAST_NOTICE, mode = ThreadMode.MAIN)
    public void handleAudioMulticastNotice(final Object obj) {
        if(mAudioMulticastListener != null){
            mAudioMulticastListener.accept(obj);
        }
    }

    /**
     * 获取有定时功能的呼叫的定时时间
     * 单位秒
     */
    public Integer getTimingLastTime(@NonNull CallTypeEnum callTypeEnum){
        switch (callTypeEnum){
            case OXYGEN_INHALATION:
                return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.OXYGEN_INHALATION_TIME,mContext.getResources().getInteger(R.integer.default_oxygen_inhalation_time));
            case MEDICATION:
                return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MEDICATION_TIME,mContext.getResources().getInteger(R.integer.default_medication_time));
            case INFUSION:
                return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.INFUSION_TIME,mContext.getResources().getInteger(R.integer.default_infusion_time));
            case SHIN_TEST:
                return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.SKIN_TEST_TIME,mContext.getResources().getInteger(R.integer.default_skin_test_time));
            case NURSING:
                return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.NURSING_TIME,mContext.getResources().getInteger(R.integer.default_nursing_time));
            default:return 0;
        }
    }

    /**
     * 设置持续时间
     * @param callTypeEnum 呼叫类型
     * @param lastTime 时间时间，单位秒
     */
    public void setTimingLastTime(@NonNull CallTypeEnum callTypeEnum,Integer lastTime){
        switch (callTypeEnum){
            case OXYGEN_INHALATION:
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.OXYGEN_INHALATION_TIME,lastTime);
                break;
            case MEDICATION:
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MEDICATION_TIME,lastTime);
            case INFUSION:
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.INFUSION_TIME,lastTime);
                break;
            case SHIN_TEST:
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.SKIN_TEST_TIME,lastTime);
                break;
            case NURSING:
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.NURSING_TIME,lastTime);
                break;
        }
    }
}
