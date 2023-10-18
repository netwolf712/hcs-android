package com.hcs.android.call.api;

import org.linphone.core.Address;

/**
 * 组播放音监听器
 */
public interface IMulticastPlayListener {
    /**
     * 文件播放完成
     * @param lm 播放句柄
     */
    void onPlayFinished(long lm);
}
