package com.hcs.android.call.api;

import org.linphone.core.Friend;

/**
 * 出席状态改变监听器
 */
public interface IPresenceListener {
    /**
     * 好友状态改变
     * @param friend 好友
     * @param presence 出席状态
     */
    void onPresenceChanged(Friend friend,int presence);
}
