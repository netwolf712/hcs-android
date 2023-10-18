package com.hcs.android.business.request.viewmodel;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.StringUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

/**
 * 呼叫逻辑
 */
public class CallLogic {
    /**
     * 状态变化消费者
     */
    private ISimpleCustomer<CallModel> mStateListener;
    public CallLogic(){
        EventBus.getDefault().register(this);
    }
    /**
     * 普通呼叫
     * @param remoteDevice 远端设备
     */
    public static void handleCall(DeviceModel remoteDevice){
        handleCall(WorkManager.getInstance().getSelfInfo(),remoteDevice,CallTypeEnum.NORMAL,"",false,false);
    }

    /**
     * 普通呼叫
     * @param remoteDevice 远端设备
     * @param callDirect 是否直接呼叫（针对主机来说，若直接呼叫则在呼出状态时直接调用acceptCall）
     */
    public static void handleCall2(DeviceModel remoteDevice,boolean callDirect){
        handleCall(WorkManager.getInstance().getSelfInfo(),remoteDevice,CallTypeEnum.NORMAL,"",false,callDirect);
    }

    /**
     * 开始外呼
     * @param remoteDevice 远端设备
     * @param callDirect 是否直接呼叫（针对主机来说，若直接呼叫则在呼出状态时直接调用acceptCall）
     */
    public static void startCallOut(DeviceModel remoteDevice,boolean callDirect){
        CallTypeEnum callTypeEnum = CallTypeEnum.NORMAL;
        DeviceModel selfDevice = WorkManager.getInstance().getSelfInfo();
        if(!isCanCall()){
            return;
        }
        DeviceModel checkDevice;
        if(selfDevice.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果是主机，则需要判断远端设备的状态
            checkDevice = remoteDevice;
        }else{
            //如果是分机，则要判断自己的当前状态
            checkDevice = selfDevice;
        }
        if(checkDevice.getState() == StateEnum.CALL_END || checkDevice.getState() == StateEnum.ONLINE) {
            checkDevice.setCallModel(WorkManager.getInstance().startCall(checkDevice.getBindPhoneNo(), callTypeEnum, "", false));
            checkDevice.setState(StateEnum.CALLING);
            if(!StringUtil.equalsIgnoreCase(checkDevice.getDevice().getDeviceId(),selfDevice.getDevice().getDeviceId())) {
                selfDevice.setState(StateEnum.CALLING);
            }
            if(selfDevice.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue() && callDirect){
                //只有主机才有直接呼叫的功能
                acceptCall(checkDevice.getBindPhoneNo());
            }
        }
    }
    /**
     * 执行呼叫操作
     * （呼叫、接听、挂起）
     * @param selfDevice 自己的设备
     * @param remoteDevice 远端设备
     * @param callTypeEnum 呼叫类型
     * @param cause 呼叫原因
     * @param isEmergency 是否紧急呼叫
     * @param callDirect 是否直接呼叫（针对主机来说，若直接呼叫则在呼出状态时直接调用acceptCall）
     */
    public static void handleCall(DeviceModel selfDevice,DeviceModel remoteDevice,CallTypeEnum callTypeEnum,String cause,boolean isEmergency,boolean callDirect){
        if(callTypeEnum != CallTypeEnum.VIDEO_CALL
                && callTypeEnum != CallTypeEnum.NORMAL
        ){
            return;
        }
        if(selfDevice == null){
            return;
        }
        DeviceModel checkDevice;
        if(selfDevice.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果是主机，则需要判断远端设备的状态
            checkDevice = remoteDevice;
        }else{
            //如果是分机，则要判断自己的当前状态
            checkDevice = selfDevice;
        }

        if(checkDevice.getState() == StateEnum.CALL_END || checkDevice.getState() == StateEnum.ONLINE) {
            checkDevice.setCallModel(WorkManager.getInstance().startCall(checkDevice.getBindPhoneNo(), callTypeEnum, cause, isEmergency));
            checkDevice.setState(StateEnum.CALLING);
            if(!StringUtil.equalsIgnoreCase(checkDevice.getDevice().getDeviceId(),selfDevice.getDevice().getDeviceId())) {
                selfDevice.setState(StateEnum.CALLING);
            }
            if(selfDevice.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue() && callDirect){
                //只有主机才有直接呼叫的功能
                acceptCall(checkDevice.getBindPhoneNo());
            }
        }else if(checkDevice.getState() == StateEnum.CALLING){
            CallModel callModel = checkDevice.getCallModel();
            if(callModel != null && StringUtil.equalsIgnoreCase(callModel.getCallee(),selfDevice.getBindPhoneNo())){
                //如果被叫对象是自己，则点击作为接收呼叫
                WorkManager.getInstance().acceptCall(checkDevice.getBindPhoneNo());
                checkDevice.setState(StateEnum.TALKING);
                if(!StringUtil.equalsIgnoreCase(checkDevice.getDevice().getDeviceId(),selfDevice.getDevice().getDeviceId())) {
                    selfDevice.setState(StateEnum.TALKING);
                }
            }else if(callModel != null && StringUtil.equalsIgnoreCase(callModel.getCaller(),selfDevice.getBindPhoneNo())){
                //如果主叫对象是自己，则点击作为挂机
                WorkManager.getInstance().releaseCall(checkDevice.getBindPhoneNo(),callTypeEnum);
                checkDevice.setState(StateEnum.ONLINE);
                if(!StringUtil.equalsIgnoreCase(checkDevice.getDevice().getDeviceId(),selfDevice.getDevice().getDeviceId())) {
                    selfDevice.setState(StateEnum.ONLINE);
                }
            }

        }else if(checkDevice.getState() == StateEnum.TALKING){
            //如果是通话中，则进行挂机
            CallModel callModel = checkDevice.getCallModel();
            if(callModel != null
                    && (StringUtil.equalsIgnoreCase(callModel.getCaller(),WorkManager.getInstance().getSelfInfo().getBindPhoneNo())
                    ||StringUtil.equalsIgnoreCase(callModel.getCallee(),WorkManager.getInstance().getSelfInfo().getBindPhoneNo()))){
                WorkManager.getInstance().releaseCall(checkDevice.getBindPhoneNo(),callTypeEnum);
                checkDevice.setState(StateEnum.ONLINE);
                if(!StringUtil.equalsIgnoreCase(checkDevice.getDevice().getDeviceId(),selfDevice.getDevice().getDeviceId())) {
                    selfDevice.setState(StateEnum.ONLINE);
                }
            }
        }
    }

    /**
     * 是否可以呼叫
     * 如果正在通话或正在外呼中，则不能发起新的呼叫
     * @return true 可以呼叫，false不可以
     */
    public static boolean isCanCall(){
        DeviceModel selfDevice = WorkManager.getInstance().getSelfInfo();
        return !(selfDevice.getState() == StateEnum.TALKING || selfDevice.getState() == StateEnum.CALLING);
    }
    /**
     * 执行呼叫挂起操作
     */
    public static void hangupCall(String bindPhoneNo,CallTypeEnum callTypeEnum){
        WorkManager.getInstance().releaseCall(bindPhoneNo,callTypeEnum);
    }

    /**
     * 执行呼叫接听操作
     */
    public static void acceptCall(String bindPhoneNo){
        WorkManager.getInstance().acceptCall(bindPhoneNo);
    }

    /**
     * 添加状态监听器
     */
    public void addStateListener(ISimpleCustomer<CallModel> consumer){
        mStateListener = consumer;
    }

    /**
     * 开始信息显示类型的呼叫
     * 所有计时类的呼叫
     */
    public static void startInfoCall(CallTypeEnum callTypeEnum,String cause,boolean isEmergency){
        if(callTypeEnum == CallTypeEnum.VIDEO_CALL
                || callTypeEnum == CallTypeEnum.NORMAL
        ){
            return;
        }
        WorkManager.getInstance().startCallMaster(callTypeEnum, cause, isEmergency);
    }

    /**
     * 开始信息显示类型的呼叫
     * 所有计时类的呼叫
     */
    public static void startTimingCall(CallTypeEnum callTypeEnum,String cause){
        if(callTypeEnum == CallTypeEnum.VIDEO_CALL
                || callTypeEnum == CallTypeEnum.EMERGENCY
                || callTypeEnum == CallTypeEnum.NORMAL
        ){
            return;
        }
        WorkManager.getInstance().updateStateInfo(callTypeEnum,StateEnum.CALL_TIMING_START,cause);
    }


    /**
     * 判断是呼入还是呼出
     * @param callModel 呼叫对象
     * @return true 呼入，false 呼出
     */
    public static boolean isCallIn(@NonNull CallModel callModel){
        return StringUtil.equalsIgnoreCase(WorkManager.getInstance().getSelfInfo().getDevice().getDeviceId(),callModel.getCalleeDeviceId());
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
}
