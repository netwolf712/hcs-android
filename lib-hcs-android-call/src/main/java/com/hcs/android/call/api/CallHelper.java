package com.hcs.android.call.api;

import androidx.annotation.NonNull;

import com.hcs.android.call.util.LinphoneUtils;

import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.core.CallParams;
import org.linphone.core.Reason;

/**
 * 呼叫帮助类
 * 整理出呼叫相关的常用接口，方便上层调用
 */
public class CallHelper {
    /**
     * 发起呼叫
     * @param phoneNumber 电话号码
     * @param displayName 显示名称
     * @return 呼叫实体，后续的呼叫控制都会用到它，若有需要请保存
     */
    public static Call startCall(String phoneNumber, String displayName){
        return LinphoneManager.getInstance().newOutgoingCall(phoneNumber, displayName);
    }

    /**
     * 通过好友索引发起呼叫
     * @param refId 好友索引
     * @param displayName 显示名称
     * @return 呼叫实体，后续的呼叫控制都会用到它，若有需要请保存
     */
    public static Call startCallByRefId(String refId, String displayName){
        return LinphoneManager.getInstance().newOutgoingCallByRefId(refId, displayName);
    }

    /**
     * 接听呼叫
     */
    public static boolean acceptCall(Call call){
        return LinphoneManager.getInstance().acceptCall(call);
    }

    /**
     * 停止呼叫
     */
    public static void stopCall(Call call){
        if (call != null) {
            call.terminate();
        }
    }

    /**
     * 暂停通话
     */
    public static void pauseCall(Call call){
        if (call != null) {
            call.pause();
        }
    }

    /**
     * 恢复通话
     */
    public static void resumeCall(Call call){
        if (call != null) {
            call.resume();
        }
    }

    /**
     * 拒绝呼叫
     * @param call 呼叫实体
     * @param reason 拒绝原因
     */
    public static void declineCall(Call call, Reason reason){
        if (call != null) {
            call.decline(reason);
        }
    }

    /**
     * 转移呼叫
     * @param call 呼叫实体
     * @param phoneNumber 需要转移的目的地址
     * @param displayName 显示名称
     */
    public static void transferCall(Call call, String phoneNumber,String displayName){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (call != null) {
                call.transfer(phoneNumber);
            }
        }
    }

    /**
     * 更新当前呼叫
     * 开启或关闭视频通话
     * @param isWithVideo true将语音通话切换为视频通话，false将视频通话切换为语音通话
     */
    public static void updateCallVideo(boolean isWithVideo){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (isWithVideo) {
                LinphoneManager.getInstance().addVideo();
            } else {
                LinphoneManager.getInstance().removeVideo();
            }
        }
    }

    /**
     * 设置呼叫状态监听器
     */
    public static void setCallListener(ICallListener callListener){
        LinphoneManager.getInstance().setCallListener(callListener);
    }

    /**
     * 获取远端的显示名称
     */
    public static String getDisplayNameFromCall(@NonNull Call call){
        return LinphoneUtils.getAddressDisplayName(call.getRemoteAddress());
    }

    /**
     * 获取远端的用户名
     */
    public static String getUsernameFromCall(@NonNull Call call){
        return LinphoneUtils.getUsernameFromAddress(call.getRemoteAddress().asString());
    }

    /**
     * 录音控制
     */
    public static void startRecord(Call call){
        if (call != null && !call.getParams().isRecording()) {
            call.getCallLog().setRefKey(call.getParams().getRecordFile());
            call.startRecording();
        }
    }
    public static void stopRecord(Call call){
        if (call != null && call.getParams().isRecording()) {
            call.stopRecording();
        }
    }

    /**
     * 获取呼叫日志
     */
    public static CallLog[] getCallLogs(){
        return CallEasier.getInstance().getCallLogs();
    }
}
