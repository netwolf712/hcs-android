package com.hcs.android.call.api;

import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;

/**
 * 聊天信息监听器
 */
public interface IChatMessageListener {
    /**
     * 收到聊天信息
     * @param chatRoom 聊天室
     * @param chatMessage 聊天内容
     */
    void onChatMessageReceived(ChatRoom chatRoom, ChatMessage chatMessage);
}
