package com.hcs.android.call.api;

import org.linphone.core.GlobalState;

/**
 * 全局状态改变
 */
public interface IGlobalStateListener {
    /**
     * 状态改变
     */
    void onGlobalStateChanged(GlobalState state,String message);
}
