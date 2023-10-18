package com.hcs.android.call.api;

import android.view.View;

import com.hcs.android.call.R;

import org.linphone.core.Address;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomBackend;
import org.linphone.core.ChatRoomParams;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.Friend;
import org.linphone.core.ProxyConfig;
import org.linphone.core.tools.Log;

/**
 * 聊天管理器
 */
public class ChatManager {
    private static ChatManager mInstance = null;
    public static ChatManager getInstance(){
        if(mInstance == null){
            synchronized (ChatManager.class){
                if(mInstance == null) {
                    mInstance = new ChatManager();
                }
            }
        }
        return mInstance;
    }
    /**
     * 根据远端号码创建一个聊天室
     * 如果之前有则不重新创建
     * （一对一聊天室）
     * @param phoneNumber 远端号码
     * @return 聊天室
     */
    private ChatRoom getChatRoom(String phoneNumber){
        //通过电话号码找到好友
        Friend friend = LinphoneManager.getInstance().getFriendByRefId(phoneNumber);
        if(friend == null || friend.getAddress() == null){
            return null;
        }
        String tag = friend.getAddress().asString();
        Address participant = Factory.instance().createAddress(tag);
        Core lc = LinphoneManager.getLc();
        ProxyConfig lpc = lc.getDefaultProxyConfig();
        boolean isSecured = LinphonePreferences.instance().isSecurityChat();

        if (lpc != null) {
            ChatRoom room =
                    lc.findOneToOneChatRoom(
                            lpc.getContact(),
                            participant,
                            isSecured);
            if (room != null) {
                return room;
            } else {
                if (!LinphonePreferences.instance()
                        .useBasicChatRoomFor1To1()) {

                    ChatRoomParams params = lc.createDefaultChatRoomParams();
                    params.setEncryptionEnabled(isSecured);
                    params.setGroupEnabled(false);
                    // We don't want a basic chat room,
                    // so if isSecured is false we have to set this manually
                    params.setBackend(ChatRoomBackend.FlexisipChat);

                    Address participants[] = new Address[1];
                    participants[0] = participant;

                    room =
                            lc.createChatRoom(
                                    params,
                                    tag,
                                    participants);
                    if (room != null) {
                        Log.i(
                                "[Contact Details Fragment] createChatRoom returned ok...");
                    } else {
                        Log.w(
                                "[Contact Details Fragment] createChatRoom returned null...");
                    }
                } else {
                    room = lc.getChatRoom(participant);
                }
                return room;
            }
        }else{
            Address primary = lc.getPrimaryContactParsed();
            return lc.getChatRoom(participant,primary);
        }
    }

    /**
     * 发送文字信息
     * @param phoneNumber 远端号码
     * @param message 文字信息
     */
    public ChatRoom sendChatMessage(String phoneNumber,String message){
        ChatRoom chatRoom = getChatRoom(phoneNumber);
        if(chatRoom != null){
            ChatMessage chatMessage = chatRoom.createMessage(message);
            chatMessage.send();
            return chatRoom;
        }else{
            return null;
        }
    }

    /**
     * 获取聊天记录
     * @param phoneNumber 远端号码
     * @param startCount 记录开始位置（数据库里的偏移位置）
     * @param endCount 记录结束位置（数据库里的偏移位置）
     * @return 记录数组
     */
    public ChatMessage[] getChatHistory(String phoneNumber,int startCount ,int endCount){
        ChatRoom chatRoom = getChatRoom(phoneNumber);
        if(chatRoom != null){
            return chatRoom.getHistoryRange(startCount,endCount);
        }
        return null;
    }

    /**
     * 获取记录条数
     * @param phoneNumber 远端号码
     * @return 记录条数
     */
    public int getChatHistoryCount(String phoneNumber){
        ChatRoom chatRoom = getChatRoom(phoneNumber);
        if(chatRoom != null){
            return chatRoom.getHistorySize();
        }
        return 0;
    }

    /**
     * 获取所有的聊天室
     */
    public ChatRoom[] getChatRooms(){
        Core lc = LinphoneManager.getLc();
        if(lc == null){
            return null;
        }
        return lc.getChatRooms();
    }

}
