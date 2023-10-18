package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcs.android.business.R;
import com.hcs.android.business.constant.BooleanConstant;
import com.hcs.android.business.constant.CallMediaTypeEnum;
import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.ConfirmTypeEnum;
import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.constant.StepMasterTypeEnum;
import com.hcs.android.business.constant.TrustStateEnum;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestAudioMulticast;
import com.hcs.android.business.entity.RequestConfig;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestData;
import com.hcs.android.business.entity.RequestTrust;
import com.hcs.android.business.entity.RequestUpdateCapability;
import com.hcs.android.business.entity.RequestVolumeSet;
import com.hcs.android.business.entity.ResponseConfig;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.util.GroupUtil;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.web.AjaxResult;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 分机管理器
 * 所有分机继承与此类
 */
public abstract class SlaveManager extends DeviceManager{
    /**
     * 记住自己的主机是谁
     */
    private DeviceModel mMasterDevice = null;

    /**
     * 与主机失去链接的次数
     */
    private int mLostConnectCnt = 0;

    /**
     * 外呼定时器
     */
    private RobustTimer mCallWaitTimer;

    /**
     * 当前外呼的号码
     */
    private CallModel mCurrentCallOutModel;
    private String mCurrentCallNo;


    /**
     * 语音组播
     */
    private final MulticastGroupModel mMulticastGroupModel;

    /**
     * 命令管理器
     */
    private final DataCommandManager mDataCommandManager;

    public SlaveManager(Context context){
        super(context);
        getSlaveConfig();
        //设置自己的当前状态
        mSelf.setState(StateEnum.INIT);
        int groupSn = SettingsHelper.getInstance(context).getInt(PreferenceConstant.PREF_KEY_GROUP_SN,context.getResources().getInteger(R.integer.default_group_sn));
        mMulticastGroupModel = new MulticastGroupModel(groupSn
        ,String.valueOf(groupSn),SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_ADDRESS,context.getString(R.string.default_audio_multicast_address))
        , GroupUtil.getAudioMulticastPort(Integer.parseInt(mSelf.getDevice().getParentNo()),groupSn));


        //当前为请求配置状态
        mSelf.setState(StateEnum.REQ_CONFIG);

        mDataCommandManager = new DataCommandManager(new DataCommandManager.IDataCommandListener() {
            @Override
            public void handle(RequestData requestData) {
                //执行命令
                mWebHandlerManager.handleCommand(requestData.getCommandEnum().getId(),requestData.getRawData());
            }

            @Override
            public void fusing() {
                //执行熔断操作
                //状态重新转换为请求配置
                mSelf.setState(StateEnum.REQ_CONFIG);
                //发送请求配置请求
                sendReqGetConfig(mMasterDevice);
            }
        },context);

        //初始化全局广播配置
        AudioMulticastManager.getInstance().initGlobalAudioMulticast(mSelf.getDevice().getParentNo());
    }

    /**
     * 开启广播监听
     */
    private void startAudioMulticastManager(){
        //启动全局广播监听
        AudioMulticastManager.getInstance().startAudioMulticast(AudioMulticastManager.getInstance().getGlobalMulticast());
        //启动自己所在分区的广播监听
        if(mMulticastGroupModel.getMulticastGroup().getGroupNo() != null){
            //如果为-1表示不在分区里
            AudioMulticastManager.getInstance().startAudioMulticast(mMulticastGroupModel);
        }
    }

    /**
     * 更新广播信息
     */
    private void updateGroupAudioMulticast(@NonNull PlaceModel placeModel){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_GROUP_SN,placeModel.getPlace().getGroupSn() == null ? MulticastGroupModel.WHOLE_GROUP : placeModel.getPlace().getGroupSn());
        mMulticastGroupModel.getMulticastGroup().setGroupNo(placeModel.getPlace().getGroupNo());
        mMulticastGroupModel.getMulticastGroup().setGroupSn(placeModel.getPlace().getGroupSn());
        mMulticastGroupModel.setMulticastPort(mMulticastGroupModel.getMulticastGroup().getGroupSn() == null ? null : GroupUtil.getAudioMulticastPort(Integer.parseInt(mSelf.getDevice().getParentNo()),mMulticastGroupModel.getMulticastGroup().getGroupSn()));
        //如果当前不在通话中，则重新打开广播
        if(mSelf.getState() != StateEnum.TALKING) {
            AudioMulticastManager.getInstance().startAudioMulticast(mMulticastGroupModel);
        }
    }
    /**
     * 关闭分区广播监听
     */
    private void stopGroupAudioMulticast(){
        if(!Objects.equals(mMulticastGroupModel.getMulticastGroup().getGroupSn(),MulticastGroupModel.WHOLE_GROUP)){
            AudioMulticastManager.getInstance().stopAudioMulticast(mMulticastGroupModel);
        }
    }
    /**
     * 关闭广播监听
     */
    private void stopAudioMulticastManager(){
        //停止之前的广播监听
        AudioMulticastManager.getInstance().stopAudioMulticast(AudioMulticastManager.getInstance().getGlobalMulticast());
        stopGroupAudioMulticast();
    }

    /**
     * 获取本机信息
     */
    protected DeviceModel getSelfInfo(){
        super.getSelfInfo();
        //获取分机的专有配置
        return mSelf;
    }

    /**
     * 每个设备都有专门的配置管理器
     */
    public abstract DeviceConfig getDeviceConfig();
    /**
     * 获取分机的特有配置
     */
    private void getSlaveConfig(){
        mSelf.getDevice().setTemplateConfigId(getDeviceConfig().getCurrentTemplateConfigId());
        mSelf.getDevice().setTemplateId(getDeviceConfig().getCurrentTemplateId());
    }

    /**
     * 发送获取配置的更新请求
     * @param deviceModel 需要发送请求的目标设备，当为null时采用广播模式
     */
    private void sendReqGetConfig(DeviceModel deviceModel){
        RequestDTO<RequestConfig> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_GET_CONFIG.getId());
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setCurrentTemplateConfigId(getDeviceConfig().getCurrentTemplateConfigId());
        requestConfig.setCurrentTemplateId(getDeviceConfig().getCurrentTemplateId());
        requestConfig.setDeviceUpdateTime(mSelf.getDevice().getUpdateTime());
        requestConfig.setLastDataCommandIndex(SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.LAST_DATA_COMMAND_INDEX,mContext.getResources().getInteger(R.integer.default_last_data_command_index)));
        requestConfig.setDictUpdateTime(SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.DICT_UPDATE_TIME,mContext.getResources().getInteger(R.integer.default_dict_update_time)));
        requestConfig.setStepMasterUpdateTime(StepMasterManager.getInstance().getLastUpdateTime());
        requestConfig.setTimeSlotUpdateTime(TimeSlotManager.getInstance().getLastUpdateTime());
        requestDTO.setData(requestConfig);
        //以SIP MESSAGE形式发送
        if(deviceModel != null) {
            //要等到明确回复了才能知道自己的master是谁
            ChatHelper.sendMessage(deviceModel.getPhoneNumber(), requestDTO);
        }else{
            //否则发送广播
            if(mHandlerBase != null) {
                mHandlerBase.sendMulticast(requestDTO);
            }
        }
    }

    /**
     * 更新设备信息
     * 判断是自己主机，则请求更新配置信息
     */
    public void updateDeviceInfo(@NonNull RequestDTO<DeviceModel> requestDTO){
        super.updateDeviceInfo(requestDTO);
        if(requestDTO.getData() != null) {
            DeviceModel deviceModel = requestDTO.getData();
            if (deviceModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()
                    && StringUtil.equals(mSelf.getDevice().getParentNo(), deviceModel.getDevice().getPhoneNo())
            ) {
                mMasterDevice = deviceModel;
                //如果之前的状态为离线，则设置成在线
                if(mSelf.getState() == StateEnum.OFFLINE){
                    mSelf.setState(StateEnum.ONLINE);
                }
                sendReqGetConfig(mMasterDevice);
            }
        }
    }

    /**
     * 得到主机对获取配置请求的回复
     */
    public void handleRspGetConfig(@NonNull RequestDTO<ResponseConfig> requestDTO){
        if(requestDTO.getData() != null){
            ResponseConfig responseConfig = requestDTO.getData();
            DeviceModel remoteDevice = requestDTO.asDeviceModel();
            if(remoteDevice.getDevice() == null || !Objects.equals(remoteDevice.getDevice().getDeviceType(),DeviceTypeEnum.NURSE_STATION_MASTER.getValue())){
                //只有主机才有资格回复此消息
                return;
            }
            switch (ConfirmTypeEnum.findById(responseConfig.getConfirmId())){
                case OK:
                    KLog.i("get config response ok!");
                    //更改状态为在线
                    if(StringUtil.equalsIgnoreCase(mSelf.getDevice().getParentNo(),remoteDevice.getDevice().getPhoneNo())) {
                        //需要判断是否自己设定的主机，否则会被其它主机用假消息欺骗过去
                        mMasterDevice = requestDTO.asDeviceModel();
                        mSelf.setState(StateEnum.ONLINE);
                        mSelf.getDevice().setWholeTitle(responseConfig.getHospitalTitle());
                        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_WHOLE_TITLE,responseConfig.getHospitalTitle());
                    }
                    break;
                case WRONG_BED:
                    KLog.w("get config response wrong bed!");
                    if(StringUtil.equalsIgnoreCase(mSelf.getDevice().getParentNo(),remoteDevice.getDevice().getPhoneNo())) {
                        //需要判断是否自己设定的主机，否则会被其它主机用假消息欺骗过去
                        mMasterDevice = requestDTO.asDeviceModel();
                    }
                    break;
                case WRONG_CONFIG_REQ:
                    KLog.w("get config response wrong config!");
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 检查连接状态
     * 分机需要主动与主机通讯
     */
    @Override
    public void checkConnection(){
        KLog.i("do checkConnection");
        super.checkConnection();
        if(mMasterDevice == null){
            //如果找不到主设备，则发送查询广播
            DeviceModel masterModel = getParentDevice();
            if(masterModel != null) {
                int maxLostConnectCnt = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MAX_LOST_CONNECT_COUNT, mContext.getResources().getInteger(R.integer.default_max_lost_connect_count));
                if(mLostConnectCnt < maxLostConnectCnt) {
                    sendReqGetConfig(masterModel);
                }else{
                    sendReqGetConfig(null);
                }
            }else{
                sendReqGetConfig(null);
            }
            mLostConnectCnt++;
            return;
        }else if(mSelf.getState() == StateEnum.REQ_CONFIG){
            //如果当前为请求配置状态，则继续发送请求
            sendReqGetConfig(mMasterDevice);
        }
        mLostConnectCnt = 0;
    }

    /**
     * 辨别是否呼叫双方
     */
    private boolean isCallDevice(@NonNull String deviceId,@NonNull DeviceModel remoteDevice){
        if(StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(),deviceId)){
            return true;
        }
        return StringUtil.equalsIgnoreCase(remoteDevice.getDevice().getDeviceId(), deviceId);
    }

    /**
     * 分机需要将呼叫信息通知给其它设备
     * 分机只能呼叫主机
     */
    @Override
    public CallModel startCall(String remoteNo, CallTypeEnum callTypeEnum, String cause, boolean isEmergency){
        //先发送自己的能力给对方
        if(callTypeEnum == CallTypeEnum.NORMAL
                || callTypeEnum == CallTypeEnum.EMERGENCY
        ) {
            DeviceModel deviceModel = getDeviceByPhoneNo(remoteNo);
            sendCapability(deviceModel);
        }
        CallModel callModel = super.startCall(remoteNo,callTypeEnum,cause,isEmergency);
        if(callModel != null){
            sendCallInfoToFriends(callModel);
        }
        return callModel;
    }


    /**
     * 只是作为信息显示的状态变更
     */
    public void updateStateInfo(CallTypeEnum callTypeEnum, StateEnum stateEnum, String cause){
        CallModel callModel = createCallModel(DeviceModel.getMasterPhoneNo(getMasterNo())
                , callTypeEnum
                , stateEnum
                , cause
            , false
            , true);

        if(stateEnum == StateEnum.CALLING || stateEnum == StateEnum.TALKING){
            addOtherCallModel(mSelf,callModel);
        }else{
            removeOtherCallModel(mSelf,callModel);
        }
        sendCallInfoToFriends(callModel);
    }

    /**
     * 一次性呼通所有主机
     * 包括直接（第一）主机
     * @param masterList 主机列表
     */
    private void callAllMasters(List<StepMaster> masterList,CallTypeEnum callTypeEnum,String cause,boolean isEmergency){
        //先呼叫第一主机
        if(mMasterDevice != null){
            mCurrentCallNo = mMasterDevice.getBindPhoneNo();
            CallModel callModel = startCall(mMasterDevice.getBindPhoneNo(), callTypeEnum, cause, isEmergency);
            updateDeviceStateByCall(callModel,StateEnum.CALLING);
            addOperationLog(callModel);
            sendCallInfoToFriends(callModel);
        }
        //再呼叫继任主机列表
        if(!StringUtil.isEmpty(masterList)){
            for(StepMaster stepMaster : masterList){
                DeviceModel tmp = getDeviceByPhoneNo(stepMaster.getMasterNo());
                if(tmp != null) {
                    startCall(tmp.getBindPhoneNo(), callTypeEnum, cause, isEmergency);
                }
            }
        }
    }
    public CallModel startCallMaster(@NonNull CallTypeEnum callTypeEnum, String cause, boolean isEmergency){
        synchronized (mSynObj) {
            switch (callTypeEnum){
                case NORMAL:
                case VIDEO_CALL:
                    //如果不是紧急呼叫，则一个个呼
                    mCurrentCallOutModel = null;
                    mCurrentCallNo = "";
                    DeviceModel deviceModel = findNextParent();
                    if (deviceModel != null) {
                        mCurrentCallNo = deviceModel.getBindPhoneNo();
                        mCurrentCallOutModel = startCall(deviceModel.getBindPhoneNo(), callTypeEnum, cause, isEmergency);
                        startCallWaitTimer();
                        return mCurrentCallOutModel;
                    }
                    return null;
                case NURSING:
                case INFUSION:
                case MEDICATION:
                case SHIN_TEST:
                case OXYGEN_INHALATION:
                    CallModel callModel = findCallModel(mMasterDevice, callTypeEnum);
                    if(callModel == null){
                        if(mMasterDevice != null){
                            mCurrentCallNo = mMasterDevice.getBindPhoneNo();
                            return startCall(mMasterDevice.getBindPhoneNo(), callTypeEnum, cause, isEmergency);
                        }
                    }
                    return null;
                case REINFORCE:
                    callAllMasters(StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.REINFORCE),callTypeEnum,cause,isEmergency);
                    return null;
                case BLUE_CODE:
                case EMERGENCY:
                    List<StepMaster> stepMasterList = new ArrayList<>();
                    List<StepMaster> superiorList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.SUPERIOR);
                    if(!StringUtil.isEmpty(superiorList)){
                        stepMasterList.addAll(superiorList);
                    }
                    List<StepMaster> appendList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.APPEND);
                    if(!StringUtil.isEmpty(appendList)){
                        stepMasterList.addAll(appendList);
                    }
                    callAllMasters(stepMasterList,callTypeEnum,cause,isEmergency);
                    return null;
            }
            return null;
        }
    }
    /**
     * 将呼叫信息告诉监听的朋友
     * （分机才需要这个处理）
     */
    protected void sendCallInfoToFriends(CallModel callModel){
        //采用广播的形式，只要关系此分机的自己获取数据并更新即可
        RequestDTO<CallModel> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_UPDATE_CALL_INFO.getId());
        requestDTO.setData(callModel);
        mHandlerBase.sendMulticast(JsonUtils.toJsonString(requestDTO));

    }

    @Override
    public void handleInComingCall(CallModel callModel){
        super.handleInComingCall(callModel);
    }

    @Override
    public void handleConnectedCall(CallModel callModel){
        super.handleConnectedCall(callModel);
        //停止呼叫等待计时
        stopCallWaitTimer();
        //发送状态改变通知
        sendCallInfoToFriends(callModel);
        //有呼叫建立则关闭广播
        stopAudioMulticastManager();
        //记住当前与自己呼通的对象
        mSelf.setCallModel(callModel);
    }

    @Override
    public void handleReleaseCall(CallModel callModel){
        //super.handleReleaseCall(callModel);
        clearCall(callModel);
    }

    /**
     * 清理呼叫
     */
    @Override
    public void clearCall(CallModel callModel){
        synchronized (mSynObj) {
            super.clearCall(callModel);
            //主被叫方是不是自己
            if (StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(), callModel.getCallerDeviceId())
                    || StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(), callModel.getCalleeDeviceId())) {
                //发送状态改变通知
                sendCallInfoToFriends(callModel);
                CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
                if (callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM) {
                    //停止呼叫等待计时
                    stopCallWaitTimer();
                    //结束呼叫则重新开始广播
                    startAudioMulticastManager();
                }

                //清理与自己通话中的呼叫对象
                removeCallModel(mSelf, callModel);
                //清理远端的呼叫对象
                DeviceModel deviceModel = getRemoteDevice(callModel);
                if (deviceModel != null) {
                    removeCallModel(deviceModel, callModel);
                }

                //如果是数据流类型的呼叫，需要清除其它主机上跟本分机相关的呼叫
                if (callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM) {
                    List<StepMaster> parentList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.SUPERIOR);
                    if (!StringUtil.isEmpty(parentList)) {
                        for (StepMaster stepMaster : parentList) {
                            DeviceModel parentModel = getDeviceByPhoneNo(DeviceModel.getMasterPhoneNo(stepMaster.getMasterNo()));
                            if (parentModel != null && parentModel.getCallModel() != null) {
                                CallModel tmpCallModel = parentModel.getCallModel();
                                if (StringUtil.equalsIgnoreCase(tmpCallModel.getCallerDeviceId(), mSelf.getDevice().getDeviceId())
                                        || StringUtil.equalsIgnoreCase(tmpCallModel.getCalleeDeviceId(), mSelf.getDevice().getDeviceId())) {
                                    parentModel.setCallModel(null);
                                    parentModel.setState(StateEnum.ONLINE);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    /**
     * 释放呼叫
     */
    @Override
    public void releaseCall(String bindPhoneNo, @NonNull CallTypeEnum callTypeEnum){
        synchronized (mSynObj) {
            //如果是消息类的，则发送一遍清理请求即可
            //如果是语音类的，则需要主机挂掉
            if (callTypeEnum.getMediaType() == CallMediaTypeEnum.MESSAGE) {
                //找到所有主机，统一释放呼叫
                DeviceModel remoteDevice = getParentDevice();
                CallModel callModel = findCallModel(remoteDevice, callTypeEnum);
                if(callModel != null) {
                    callModel.setState(StateEnum.CALL_END.getValue());
                    //逐一通知真正有发起呼叫的对象
                    RequestDTO<CallModel> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_CLEAR_CALL.getId());
                    requestDTO.setData(callModel);
                    ChatHelper.sendMessage(remoteDevice.getPhoneNumber(),requestDTO);
                }
                List<StepMaster> parentList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.SUPERIOR);
                if (!StringUtil.isEmpty(parentList)) {
                    for(StepMaster stepMaster : parentList){
                        DeviceModel tmp = getDeviceByPhoneNo(stepMaster.getMasterNo());
                        if(tmp != null) {
                            CallModel tmpCallModel = findCallModel(tmp, callTypeEnum);
                            if (tmpCallModel != null) {
                                tmpCallModel.setState(StateEnum.CALL_END.getValue());
                                //逐一通知真正有发起呼叫的对象
                                RequestDTO<CallModel> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_CLEAR_CALL.getId());
                                requestDTO.setData(tmpCallModel);
                                ChatHelper.sendMessage(tmp.getPhoneNumber(),requestDTO);
                            }
                        }
                    }
                }
                if(callModel != null) {
                    clearCall(callModel);
                }
            }
        }
    }
    @Override
    public void changePhoneNo(String orgPhoneNo,String orgParentNo,String phoneNo,String parentNo){
        super.changePhoneNo(orgPhoneNo, orgParentNo, phoneNo, parentNo);
        if(!StringUtil.equalsIgnoreCase(parentNo,orgParentNo)
        || !StringUtil.equalsIgnoreCase(phoneNo,parentNo)){
            //如果号码发生改变，则需要请求新的配置
            //如果主机发生改变，则配置需要重新获取，将模板版本号改变
            if(!StringUtil.equalsIgnoreCase(parentNo,orgParentNo)) {
                mSelf.getDevice().setTemplateConfigId("-1");
            }
            sendReqGetConfig(mMasterDevice);
        }
    }

    /**
     * 获取主机号码
     * 对分机来说主机号码就是parentNo
     */
    @Override
    public String getMasterNo(){
        return mSelf.getDevice().getParentNo();
    }

    /**
     * 停止外呼等待摘机的定时器
     */
    private void stopCallWaitTimer(){
        synchronized (mSynObj){
            StepMasterManager.getInstance().stopCallWaitTimer();
            if(mCallWaitTimer != null){
                mCallWaitTimer.cancel();
                mCallWaitTimer = null;
            }
        }
    }

    /**
     * 发送自己的能力给对方
     */
    private void sendCapability(@NonNull DeviceModel deviceModel){
        PlaceModel placeModel = getPlaceModelFromCache(mSelf.getDevice().getPlaceUid());
        //当前能力只有摄像头能力
        //如果有则通知对方，否则不处理
        if(placeModel != null && placeModel.getIpCamera() != null) {
            RequestDTO<RequestUpdateCapability> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_UPDATE_CAPABILITY.getId());
            RequestUpdateCapability requestUpdateCapability = new RequestUpdateCapability();
            requestUpdateCapability.setHasIPC(true);
            requestUpdateCapability.setPlaceUid(placeModel.getPlace().getUid());
            requestUpdateCapability.setMasterDeviceId(placeModel.getPlace().getMasterDeviceId());
            requestUpdateCapability.setIpCamera(placeModel.getIpCamera());
            requestDTO.setData(requestUpdateCapability);
            ChatHelper.sendMessage(deviceModel.getPhoneNumber(), requestDTO);
        }
    }

    /**
     * 开启外呼等待摘机的定时器
     */
    private void startCallWaitTimer(){
        synchronized (mSynObj){
            StepMasterManager.getInstance().startCallWaitTimer(this::sendCallToNextParent);
            if(mCallWaitTimer == null){
                mCallWaitTimer = new RobustTimer(true);
                RobustTimerTask timerTask = new RobustTimerTask() {
                    @Override
                    public void run() {
                        //给下一个主机发送呼叫
                        if(!TimeSlotManager.getInstance().isInForbidCallUploadTime(System.currentTimeMillis())) {
                            //只有在非禁止呼叫时间段内才可行
                            sendCallToNextParent();
                        }
                    }
                };
                int callSpan = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MAX_CALL_WAIT_TIME,mContext.getResources().getInteger(R.integer.default_max_call_wait_time) * 1000);
                mCallWaitTimer.schedule(timerTask, 0, callSpan);
            }
        }
    }

    /**
     * 找到下一个用于呼叫的主机
     */
    @Nullable
    private DeviceModel findNextParent(){
        if(mCurrentCallOutModel == null){
            if(mMasterDevice != null){
                return mMasterDevice;
            }
        }
        if(!TimeSlotManager.getInstance().isInForbidCallUploadTime(System.currentTimeMillis())) {
            //只有在非禁止呼叫时间段内才可行
            List<StepMaster> parentList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.SUPERIOR);
            if (!StringUtil.isEmpty(parentList)) {
                boolean findFirst = false;
                for (StepMaster stepMaster : parentList) {
                    if (findFirst) {
                        DeviceModel deviceModel = getDeviceByPhoneNo(DeviceModel.getMasterPhoneNo(stepMaster.getMasterNo()));
                        if (deviceModel != null && deviceModel.getState() != StateEnum.OFFLINE) {
                            return deviceModel;
                        }
                    } else {
                        if (StringUtil.equalsIgnoreCase(mCurrentCallNo, stepMaster.getMasterNo())) {
                            findFirst = true;
                        }
                    }
                }
            }
        }
        return null;
    }
    /**
     * 呼叫到下一个主机
     */
    private void sendCallToNextParent(){
        synchronized (mSynObj) {
            DeviceModel deviceModel = findNextParent();
            if(deviceModel == null){
                return;
            }
            if(mCurrentCallOutModel == null){
                return;
            }
            mCurrentCallNo = deviceModel.getBindPhoneNo();
            sendCapability(deviceModel);
            CallModel callModel = sendCall(deviceModel,CallTypeEnum.findById(mCurrentCallOutModel.getCallType()),mCurrentCallOutModel.getCause(),mCurrentCallOutModel.isEmergencyCall());
            addOperationLog(callModel);
            //继续开启呼叫等待计时
            startCallWaitTimer();
        }
    }
    private void sendCallToNextParent(@NonNull StepMaster stepMaster){
        synchronized (mSynObj) {
            DeviceModel deviceModel = getDeviceByPhoneNo(stepMaster.getMasterNo());
            if(deviceModel == null){
                return;
            }
            if(mCurrentCallOutModel == null){
                return;
            }
            mCurrentCallNo = deviceModel.getBindPhoneNo();
            sendCapability(deviceModel);
            CallModel callModel = sendCall(deviceModel,CallTypeEnum.findById(mCurrentCallOutModel.getCallType()),mCurrentCallOutModel.getCause(),mCurrentCallOutModel.isEmergencyCall());
            addOperationLog(callModel);
            //继续开启呼叫等待计时
            startCallWaitTimer();
        }
    }
    @Override
    public DeviceModel handleSendCall(@NonNull RequestDTO<CallModel> requestDTO) {
        DeviceModel remoteModel = super.handleSendCall(requestDTO);
        //先发送自己的能力给对方
        sendCapability(remoteModel);
        return remoteModel;
    }
    /**
     * 处理语音广播请求
     */
    public void handleStartAudioMulticast(@NonNull RequestDTO<RequestAudioMulticast> requestDTO){
        RequestAudioMulticast requestAudioMulticast = requestDTO.getData();
        //只有自己的主机，且自己的分组，才予以处理
        DeviceModel deviceModel = requestDTO.asDeviceModel();
        if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getPhoneNo(),getMasterNo())
                && requestAudioMulticast.getMulticastGroup() != null){
            if(Objects.equals(requestAudioMulticast.getMulticastGroup(), MulticastGroupModel.WHOLE_GROUP)
            || Objects.equals(requestAudioMulticast.getMulticastGroup(),mMulticastGroupModel.getMulticastGroup().getGroupSn())
            ){
                synchronized (mSynObj){
                    if(mMulticastGroupModel.getStreamPtr() != -1){
                        return;
                    }
                    mMulticastGroupModel.setMulticastAddress(requestAudioMulticast.getListenIP());
                    mMulticastGroupModel.setMulticastPort(requestAudioMulticast.getListenPort());
                    //如果需要发送提示音，则通过eventBus发送提示音
                    if(requestAudioMulticast.isPlayReadyCode()){
                        EventBus.getDefault().post(new Object(), EventBusConstant.AUDIO_MULTICAST_NOTICE);
                    }
                    AudioMulticastManager.getInstance().startAudioMulticast(mMulticastGroupModel);
                }
            }
        }
    }
    /**
     * 处理语音广播请求
     * 暂时不用做任何处理
     */
    public void handleStopAudioMulticast(@NonNull RequestDTO<RequestAudioMulticast> requestDTO){
        RequestAudioMulticast requestAudioMulticast = requestDTO.getData();
        //只有自己的主机，且自己的分组，才予以处理
        DeviceModel deviceModel = requestDTO.asDeviceModel();
        if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getPhoneNo(),getMasterNo()) && requestAudioMulticast.getMulticastGroup() != null){
            if(requestAudioMulticast.getMulticastGroup().equals(MulticastGroupModel.WHOLE_GROUP)
                    || Objects.equals(requestAudioMulticast.getMulticastGroup(),mMulticastGroupModel.getMulticastGroup().getGroupSn())
            ){
                synchronized (mSynObj){
                    if(mMulticastGroupModel.getStreamPtr() == -1){
                        return;
                    }
                    AudioMulticastManager.getInstance().stopAudioMulticast(mMulticastGroupModel);
                }
            }
        }
    }


    /**
     * 更新位置信息
     */
    @Override
    public void updatePlaceInfo(@NonNull RequestDTO<ResponseList<Place>> requestDTO){
        super.updatePlaceInfo(requestDTO);
        //分机要更新自己的位置对应关系
        //给自己再特殊处理一遍
        PlaceModel placeModel = findPlaceByDevice(mSelf);
        if(placeModel != null){
            mSelf.getDevice().setPlaceUid(placeModel.getPlace().getUid());
            //检查分组是否发生了改变
            if(!Objects.equals(placeModel.getPlace().getGroupSn(),mMulticastGroupModel.getMulticastGroup().getGroupSn())){
                //是则重新发起监听
                stopGroupAudioMulticast();
                updateGroupAudioMulticast(placeModel);
            }
            EventBus.getDefault().post(placeModel, EventBusConstant.HANDLE_PLACE_UPDATED);
        }
    }

    /**
     * 处理请求总入口
     */
    @Override
    public AjaxResult handleRequest(@NonNull RequestData requestData){
        //如果不是数据请求，则直接处理
        if(requestData.getCommandEnum().getType() != Constant.COMMAND_TYPE_DATA || requestData.getCommandIndex() == null){
            return (AjaxResult) mWebHandlerManager.handleCommand(requestData.getCommandEnum().getId(),requestData.getRawData());
        }else{
            //否则由命令管理器做二次过滤
            mDataCommandManager.addCommand(requestData);
            return AjaxResult.success();
        }
    }

    /**
     * 更新病员信息
     * 同时更新病床位置与病员信息的关系
     */
    @Override
    protected void updateBedPatient(@NonNull Patient patient){
        synchronized (mSynObj){
            //只遍历床位列表
            List<PlaceModel> placeModelList = getPlaceListFromCache(PlaceTypeEnum.BED.getValue(),"",0,Constant.INVALID_LIMIT);
            for(PlaceModel placeModel : placeModelList){
                if(!StringUtil.isEmpty(placeModel.getPatientModelList())) {
                    for (PatientModel tmp : placeModel.getPatientModelList()) {
                        //先找到之前是否已经有关联到床位
                        if (tmp != null &&
                                tmp.getPatient() != null &&
                                StringUtil.equalsIgnoreCase(tmp.getPatient().getSerialNumber(), patient.getSerialNumber())) {
                            //如果关联到了，则检查床位数据是否发生变化
                            if (Objects.equals(tmp.getPatient().getBedSn(), patient.getBedSn())) {
                                //若没发生变化，则只要更新数据即可
                                tmp.reloadPatient(patient);
                            } else {
                                //若发生变化，则先从当前节点移除
                                placeModel.getPatientModelList().remove(tmp);
                            }
                            return;
                        }
                    }
                }
            }
            //通过床位编号找到床位
            for(PlaceModel placeModel : placeModelList){
                if(Objects.equals(patient.getBedSn(),placeModel.getPlace().getPlaceSn())){
                    PatientModel patientModel = new PatientModel(patient);
                    placeModel.getPatientModelList().add(patientModel);
                    return;
                }
            }
        }
    }

    /**
     * 更新病员信息
     */
    @Override
    public void updatePatientInfo(@NonNull RequestDTO<ResponseList<Patient>> requestDTO){
        super.updatePatientInfo(requestDTO);
        //通知界面层缓存更新了
        EventBus.getDefault().post(mSelf, EventBusConstant.HANDLE_CACHE_UPDATED);
    }

    /**
     * 重新加载数据
     */
    @Override
    public void reloadData(){
        //关闭之前的广播监听
        stopAudioMulticastManager();
        super.reloadData();
        //当前为请求配置状态,重新请求配置信息
        mSelf.setState(StateEnum.REQ_CONFIG);
        //重新开启广播监听
        startAudioMulticastManager();
    }

    /**
     * 开启呼叫监听器
     * 说明linphone初始化完成，可以做点其它事情了
     * 比如开启广播监听
     */
    @Override
    public void startCallListener(){
        super.startCallListener();
        //在分机初始化完成后既开启语音广播监听工作
        startAudioMulticastManager();
    }

    /**
     * 处理托管请求
     */
    @Override
    public void handleReqTrust(RequestDTO<RequestTrust> trustRequestDTO){
        RequestTrust requestTrust = trustRequestDTO.getData();
        if(StringUtil.equalsIgnoreCase(mSelf.getDevice().getParentNo(),requestTrust.getBeTrustNo())){
            //进入托管状态
            if(requestTrust.getState() == BooleanConstant.INTEGER_TRUE) {
                //如果请求托管的是自己的主机，则记录托管主机号码，当前进入托管状态
                TrustManager.getInstance().setTrustNo(requestTrust.getTrustNo());
                TrustManager.getInstance().setTrustState(TrustStateEnum.TRUSTING.getValue());
                //将当前主机转换为托管主机
                mMasterDevice = getDeviceByPhoneNo(requestTrust.getTrustNo());
            }else{
                //取消托管状态
                TrustManager.getInstance().setTrustNo("");
                TrustManager.getInstance().setTrustState(TrustStateEnum.NONE.getValue());
                //将当前主机转换为正式主机
                mMasterDevice = getDeviceByPhoneNo(mSelf.getDevice().getParentNo());
            }
        }
    }

    /**
     * 获取通话音量
     */
    @Override
    public int getSpeakerVolume(@NonNull RequestVolumeSet requestVolumeSet){
        if(isUseNightVolumeSet(requestVolumeSet)){
            return requestVolumeSet.getSlaveSpeakerNightVolume();
        }else{
            return requestVolumeSet.getSlaveSpeakerDayVolume();
        }
    }

    /**
     * 判断这是不是对自己有效的广播
     * 分机只接收来自同一主机的分机或自己主机、托管主机的广播
     * @param deviceModel 广播消息发送方
     * @return true 有效，false 无效
     */
    @Override
    public boolean validateMulticast(@NonNull DeviceModel deviceModel){
        boolean isValidate = super.validateMulticast(deviceModel);
        //主机判断是否有效，有效则继续处理
        if(isValidate){
            //主机的广播
            if (Objects.equals(deviceModel.getDevice().getDeviceType(),DeviceTypeEnum.NURSE_STATION_MASTER.getValue())){
                //是自己主机发送的广播肯定是要听的
                if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getPhoneNo(),mSelf.getDevice().getParentNo())){
                    return true;
                }
                //如果当前是托管中，则是托管主机的广播，也是要听的
                TrustStateEnum stateEnum = TrustManager.getInstance().getTrustState();
                if(stateEnum != TrustStateEnum.NONE) {
                    List<StepMaster> trustMasterList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.TRUST);
                    if (!StringUtil.isEmpty(trustMasterList)) {
                        for (StepMaster stepMaster : trustMasterList) {
                            if (StringUtil.equalsIgnoreCase(deviceModel.getDevice().getPhoneNo(), stepMaster.getMasterNo())) {
                                return true;
                            }
                        }
                    }
                }
            }else{
                //分机的广播
                if (StringUtil.equalsIgnoreCase(deviceModel.getDevice().getParentNo(),mSelf.getDevice().getParentNo())){
                    return true;
                }
            }
        }
        //其它的广播统一不处理
        return false;
    }

    /**
     * 分机需要保存自己的呼叫状态
     */
    @Override
    public void updateDeviceStateByCall(@NonNull CallModel callModel, @NonNull StateEnum stateEnum){
        super.updateDeviceStateByCall(callModel,stateEnum);
        //把呼叫模块存入自己的呼叫列表
        addCallModel(mSelf,callModel);
    }
}
