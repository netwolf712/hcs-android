package com.hcs.android.call.api;

import org.linphone.core.Friend;

/**
 * 好友监听器
 */
public interface IFriendListener {
    /**
     * 有新朋友加入
     * @param friend 新加入的好友
     */
    void onFriendAdded(Friend friend);

    /**
     * 有好友被删除
     * @param friend 被删除的好友
     */
    void onFriendRemoved(Friend friend);
}
