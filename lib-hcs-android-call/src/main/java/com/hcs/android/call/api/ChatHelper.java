package com.hcs.android.call.api;

import androidx.annotation.Nullable;

import com.hcs.android.common.util.JsonUtils;

import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;

/**
 * 聊天帮助类
 * 方便上层调用
 */
public class ChatHelper {
    /**
     * 发送文字信息
     * @param phoneNumber 远端号码
     * @param message 文字信息
     */
    @Nullable
    public static ChatRoom sendMessage(String phoneNumber, String message){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (!PhoneManager.getInstance().isPhoneReady()) {
                return null;
            }
            return ChatManager.getInstance().sendChatMessage(phoneNumber, message);
        }
    }

    @Nullable
    public static ChatRoom sendMessage(String phoneNumber, Object message){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (!PhoneManager.getInstance().isPhoneReady()) {
                return null;
            }
            return ChatManager.getInstance().sendChatMessage(phoneNumber, JsonUtils.toJsonString(message));
        }
    }
    /**
     * 获取聊天记录
     * @param phoneNumber 远端号码
     * @param startCount 记录开始位置（数据库里的偏移位置）
     * @param endCount 记录结束位置（数据库里的偏移位置）
     * @return 记录数组
     */
    public static ChatMessage[] getChatHistory(String phoneNumber, int startCount , int endCount){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return ChatManager.getInstance().getChatHistory(phoneNumber, startCount, endCount);
        }
    }

    /**
     * 获取记录条数
     * @param phoneNumber 远端号码
     * @return 记录条数
     */
    public static int getChatHistoryCount(String phoneNumber){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return ChatManager.getInstance().getChatHistoryCount(phoneNumber);
        }
    }

    /**
     * 设置聊天信息监听器
     */
    public static void setChatListener(IChatMessageListener chatMessageListener){
        LinphoneManager.getInstance().setChatMessageListener(chatMessageListener);
    }

    /**
     * 获取所有的聊天室
     */
    public static ChatRoom[] getChatRooms(){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return ChatManager.getInstance().getChatRooms();
        }
    }
}
