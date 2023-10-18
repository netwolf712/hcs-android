package com.hcs.android.call.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import org.linphone.core.Call;

import java.util.HashMap;
import java.util.Map;

/**
 * 呼叫管理器
 */
public class CallManager implements ICallListener {

    private ICallSimpleListener mCallSimpleListener;

    /**
     * 是否主动录音
     */
    private boolean mIsAutoRecord;

    public boolean isAutoRecord(){
        return mIsAutoRecord;
    }
    public void setAutoRecord(boolean autoRecord){
        mIsAutoRecord = autoRecord;
    }
    /**
     * 呼叫映射
     * Map<remoteNumber,CallModel>
     */
    private final Map<String, Call> mCallMap = new HashMap<>();

    /**
     * 开启呼叫监听器，只有在linphone初始化成功后才能设置
     * 所以另开方法
     */
    public void startCallListener(){
        CallHelper.setCallListener(this);
    }
    /**
     * 暂停其它正在通话中的呼叫
     * @param excludeNumber 不包括的那通呼叫
     */
    private void pauseOtherCalls(String excludeNumber){
        synchronized (mCallMap){
            for (Map.Entry<String,Call> callEntry : mCallMap.entrySet()){
                if(StringUtil.equalsIgnoreCase(callEntry.getKey(),excludeNumber)){
                    continue;
                }
                Call call = callEntry.getValue();
                if(call != null && call.getState() == Call.State.StreamsRunning){
                    CallHelper.pauseCall(call);
                }
            }
        }
    }

    /**
     * 开始呼叫
     * 暂停其它不相关的呼叫
     * 若此号码之前的呼叫已存在，则恢复此呼叫
     * @param remoteNumber 远端号码
     * @param displayName 显示名称
     */
    public void startCall(String remoteNumber,String displayName){
        synchronized (mCallMap){
            Call tmp = mCallMap.get(remoteNumber);
            if(tmp != null){
                pauseOtherCalls(remoteNumber);
                if(tmp.getState() == Call.State.Paused){
                    CallHelper.resumeCall(tmp);
                }
                return;
            }
            //查找有没有已经在通话中的，如果已经在通话中则暂停
            pauseOtherCalls(remoteNumber);
            Call tmpCall = CallHelper.startCall(remoteNumber,displayName);
            if(tmpCall != null){
                mCallMap.put(remoteNumber,tmpCall);
            }
        }
    }

    /**
     * 是否正在呼叫这个号码
     */
    public boolean isCalling(String remoteNumber){
        synchronized (mCallMap) {
            Call tmp = mCallMap.get(remoteNumber);
            return tmp != null;
        }
    }

    /**
     * 呼叫保持
     */
    public void pauseCall(String remoteNumber){
        synchronized (mCallMap) {
            Call call = mCallMap.get(remoteNumber);
            if (call != null) {
                CallHelper.pauseCall(call);
            }
        }
    }

    /**
     * 恢复呼叫
     */
    public void resumeCall(String remoteNumber){
        synchronized (mCallMap) {
            Call call = mCallMap.get(remoteNumber);
            if (call != null && call.getState() == Call.State.Paused) {
                CallHelper.resumeCall(call);
            }
        }
    }

    /**
     * 当前呼叫开启或关闭视频
     */
    public void enableVideo(boolean enable){
        CallHelper.updateCallVideo(enable);
    }

    /**
     * 停止呼叫
     */
    public void stopCall(String remoteNumber){
        synchronized (mCallMap) {
            Call call = mCallMap.get(remoteNumber);
            if (call != null) {
                CallHelper.stopRecord(call);
                CallHelper.stopCall(call);
                //mCallMap.remove(remoteNumber);
            }
        }
    }

    /**
     * 检测呼叫是否携带视频功能
     * @param remoteNumber 远端号码
     * @return true 视频，false 无视频
     */
    public boolean isCallWithVideo(String remoteNumber){
        synchronized (mCallMap){
            Call call = mCallMap.get(remoteNumber);
            if(call != null){
                return call.getParams().isVideoEnabled();
            }
        }
        return false;
    }
    @Nullable
    private Call findCallModel(Call call){
        synchronized (mCallMap){
//            for(Call tmp : mCallMap.values()){
//                if(tmp != null && tmp.equals(call)){
//                    return tmp;
//                }
//            }
//            return null;
            return mCallMap.get(call.getRemoteAddress().getUsername());
        }
    }
    private void removeCall(Call call){
        synchronized (mCallMap){
            mCallMap.remove(call.getRemoteAddress().getUsername());
        }
    }

    /**
     * 呼叫控制逻辑
     * @param status 呼叫状态
     */
    private void callControlLogic(@NonNull Call call, @NonNull Call.State status, String message){
        switch (status){
            //初始状态
            case Idle:
            case End:
            case Released: {
                Call findCall = findCallModel(call);
                if (findCall != null) {
                    CallHelper.stopRecord(call);
                    if (mCallSimpleListener != null) {
                        mCallSimpleListener.onReleaseCall(call, message);
                    }
                    removeCall(findCall);
                }
                break;
            }
            //呼出中
            case OutgoingInit:
            case OutgoingProgress:
            case OutgoingRinging:
            case OutgoingEarlyMedia:
                break;
            //呼叫建立
            case Connected:
                if(mCallSimpleListener != null){
                    mCallSimpleListener.onConnectedCall(call,message);
                }
                break;
            case Pausing:
                break;
            //呼叫暂停
            case Paused:
            case PausedByRemote:
                break;
            case Resuming:
                break;
            //呼入中
            case IncomingReceived: {
                Call findCall = findCallModel(call);
                if(findCall == null) {
                    if (mCallSimpleListener != null) {
                        mCallSimpleListener.onInComingCall(call, message);
                    }
                    mCallMap.put(call.getRemoteAddress().getUsername(),call);
                }
                break;
            }
            case IncomingEarlyMedia:
                break;
            case StreamsRunning:
                if(mIsAutoRecord) {
                    //呼叫建立后自动开始录音(只有数据流建立后才能启动录音，其它地方录音失败)
                    //只有主机才需要录音
                    CallHelper.startRecord(call);
                }
                break;
            default:
                break;
        }
    }

    //呼叫回调函数
    /**
     * 呼叫状态改变
     * @param call 呼叫对象
     * @param state 当前状态
     * @param message 改变原因
     */
    @Override
    public void onCallStateChanged(final Call call, @NonNull final Call.State state, final String message){
        KLog.i("call state changed ==> " + state.toInt() + ",message： " + (StringUtil.isEmpty(message) ? "" : message));
        //synchronized (mSynObj) {
            callControlLogic(call, state, message);
        //}
    }

    public void setCallSimpleListener(ICallSimpleListener callSimpleListener){
        mCallSimpleListener = callSimpleListener;
    }

    public interface ICallSimpleListener{
        /**
         * 有呼叫进入
         */
        void onInComingCall(Call call,String message);

        /**
         * 呼叫建立
         */
        void onConnectedCall(Call call,String message);

        /**
         * 呼叫释放
         */
        void onReleaseCall(Call call,String message);
    }
}
