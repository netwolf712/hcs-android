package com.hcs.android.call.api;

import org.linphone.core.Call;

/**
 * 呼叫监听器
 * 用于监听呼叫状态的改变
 */
public interface ICallListener {
    /**
     * 呼叫状态改变
     * @param call 呼叫对象
     * @param state 当前状态
     * @param message 改变原因
     */
    void onCallStateChanged(final Call call, final Call.State state, final String message);
}
