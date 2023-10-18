package com.hcs.app.viewmodel;

import com.hcs.android.call.api.CallHelper;
import com.hcs.android.call.api.ICallListener;
import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.app.entity.CallVo;

import org.linphone.core.Call;

public class CallViewModel implements ICallListener {

    private CallVo mCallVo;
    private Call mCurrentCall;
    public Call getCurrentCall(){
        return mCurrentCall;
    }
    public CallViewModel() {

        mCallVo = new CallVo();
        mCallVo.setRemoteAddress("111@xxx");
        mCallVo.setLocalName("caller tester");
        mCallVo.setRemoteName("callee tester");
        mCallVo.setCallStatus("hello world!");
        callControlLogic(Call.State.Idle);
        CallHelper.setCallListener(this);

        LinphonePreferences.instance().setAutomaticallyAcceptVideoRequests(true);
    }

    /**
     * 呼叫控制逻辑
     * @param status 呼叫状态
     */
    private void callControlLogic(Call.State status){
        switch (status){
            //初始状态
            case Idle:
            case Released:
                mCallVo.setCanStartCall(true);
                mCallVo.setCanStopCall(false);
                mCallVo.setCanPauseCall(false);
                mCallVo.setCanResumeCall(false);
                mCallVo.setCanStartVideo(false);
                mCallVo.setCanStopVideo(false);
                mCallVo.setCanAcceptCall(false);
                mCallVo.setCanRejectCall(false);
                mCallVo.setRemoteName("");
                break;
            //呼出中
            case OutgoingInit:
            case OutgoingProgress:
            case OutgoingRinging:
            case OutgoingEarlyMedia:
                mCallVo.setCanStartCall(false);
                mCallVo.setCanStopCall(true);
                mCallVo.setCanPauseCall(false);
                mCallVo.setCanResumeCall(false);
                mCallVo.setCanStartVideo(false);
                mCallVo.setCanStopVideo(false);
                mCallVo.setCanAcceptCall(false);
                mCallVo.setCanRejectCall(false);
                if(mCurrentCall != null){
                    mCallVo.setRemoteName(CallHelper.getDisplayNameFromCall(mCurrentCall));
                }
                break;
            //呼叫建立
            case Connected:
                mCallVo.setCanStartCall(false);
                mCallVo.setCanStopCall(true);
                mCallVo.setCanPauseCall(true);
                mCallVo.setCanResumeCall(false);
                mCallVo.setCanStartVideo(true);
                mCallVo.setCanStopVideo(false);
                mCallVo.setCanAcceptCall(false);
                mCallVo.setCanRejectCall(false);
                break;
            case Pausing:
                //暂停中，先把暂停按钮隐藏了，免得重复触发
                mCallVo.setCanPauseCall(false);
                break;
            //呼叫暂停
            case Paused:
            case PausedByRemote:
                mCallVo.setCanStartCall(false);
                mCallVo.setCanStopCall(true);
                mCallVo.setCanPauseCall(false);
                mCallVo.setCanResumeCall(true);
                mCallVo.setCanStartVideo(true);
                mCallVo.setCanStopVideo(false);
                mCallVo.setCanAcceptCall(false);
                mCallVo.setCanRejectCall(false);
                break;
            case Resuming:
                //恢复中，先把恢复按钮隐藏了，免得重复触发
                mCallVo.setCanResumeCall(false);
                break;
            //呼入中
            case IncomingReceived:
            case IncomingEarlyMedia:
                mCallVo.setCanStartCall(false);
                mCallVo.setCanStopCall(false);
                mCallVo.setCanPauseCall(false);
                mCallVo.setCanResumeCall(false);
                mCallVo.setCanStartVideo(false);
                mCallVo.setCanStopVideo(false);
                mCallVo.setCanAcceptCall(true);
                mCallVo.setCanRejectCall(true);
                if(mCurrentCall != null){
                    mCallVo.setRemoteAddress(mCurrentCall.getRemoteAddressAsString());
                    mCallVo.setRemoteName(CallHelper.getDisplayNameFromCall(mCurrentCall));
                }
                break;
            case StreamsRunning:
                checkCanStartVideo();
                break;
            default:
                break;
        }
    }

    /**
     * 检查是否可以开启视频通话
     */
    private void checkCanStartVideo(){
        if(mCurrentCall != null) {
            //如果是更新呼叫状态，则需要判断更新后是否有视频
            boolean remoteVideo = mCurrentCall.getRemoteParams().isVideoEnabled();
            boolean localVideo = mCurrentCall.getCurrentParams().isVideoEnabled();
            if (remoteVideo && localVideo) {
                mCallVo.setCanStartVideo(false);
                mCallVo.setCanStopVideo(true);
            }else{
                mCallVo.setCanStartVideo(true);
                mCallVo.setCanStopVideo(false);
            }
        }
    }
    public CallVo getCallVo(){
        return mCallVo;
    }

    private void setChangeCallStatus(Call call,String status){
        mCurrentCall = call;
        mCallVo.setCallStatus(status);
    }
    //呼叫回调函数
    /**
     * 呼叫状态改变
     * @param call 呼叫对象
     * @param state 当前状态
     * @param message 改变原因
     */
    @Override
    public void onCallStateChanged(final Call call, final Call.State state, final String message){
        setChangeCallStatus(call,"state " + state.toInt() + ",cause " + message + (call.isRecording() ? ",录音中" : ""));
        callControlLogic(state);
    }

    public void callSIP(){

    }
}

