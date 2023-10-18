package com.hcs.android.call.api;

import androidx.annotation.NonNull;

/**
 * 广播助手
 * 方便快速开启广播功能
 */
public class MulticastHelper {

    /**
     * 开始音频广播
     * @param soundCardName 声卡名称
     * @param multicastIp 广播地址
     * @param multicastPort 广播端口
     * @param playFile 放音文件
     * @param volumeLevel 声音等级
     * @return 广播句柄
     */
    @NonNull
    public static Long startAudioMulticast(String soundCardName,String multicastIp, int multicastPort, String playFile,int volumeLevel){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return LinphoneManager.getLc().multicastStart(soundCardName,multicastIp, multicastPort, playFile,volumeLevel);
        }
    }

    /**
     * 停止音频广播
     * @param obj 广播句柄
     */
    public static void stopAudioMulticast(Long obj){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            LinphoneManager.getLc().multicastStop(obj);
        }
    }

    /**
     * 接收音频广播
     * @param soundCardName 声卡名称
     * @param multicastIp 广播地址
     * @param multicastPort 广播端口
     * @param recordFile 录音文件
     * @param volumeLevel 声音等级
     * @return 广播句柄
     */
    @NonNull
    public static Long startAudioMulticastListen(String soundCardName,String multicastIp, int multicastPort, String recordFile,int volumeLevel){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return LinphoneManager.getLc().multicastListen(soundCardName, multicastIp, multicastPort, recordFile,(float)volumeLevel);
        }
    }

    /**
     * 是否启动循环播放的功能
     * @param obj 由startAudioMulticast或startAudioMulticastListen返回的对象句柄
     * @param pauseTime 停留时间，-1表示不循环，0表示无停留时间，单位毫秒
     */
    public static void enableMulticastLoop(long obj,int pauseTime){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            LinphoneManager.getLc().multicastPlayerEnableLoop(obj, pauseTime);
        }
    }

    public static void setMulticastPlayerListener(IMulticastPlayListener multicastPlayerListener){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            LinphoneManager.getInstance().setMulticastPlayerListener(multicastPlayerListener);
        }
    }
}
