package com.hcs.android.call.api;

import org.linphone.core.Friend;
import org.linphone.core.FriendList;

/**
 * 好友帮助类
 * 方便上层调用
 */
public class FriendHelper {
    /**
     * 添加好友
     * 无则添加，有则修改
     * @param username 用户名
     * @param displayName 显示名称
     * @param remoteAddress 远端地址 ip:port
     * @param group 所属分组
     * @param userData 用户自定义数据
     */
    public static Friend addFriend(String username,String displayName,String remoteAddress,String group,Object userData){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            String address = String.format("sip:%s@%s", username, remoteAddress);
            return FriendManager.getInstance().addFriend(username, displayName, address, group, userData);
        }
    }

    /**
     * 通过电话号码删除好友
     * @param phoneNumber 电话号码
     */
    public static boolean deleteFriendByPhoneNumber(String phoneNumber){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return FriendManager.getInstance().deleteFriendByPhoneNumber(phoneNumber);
        }
    }

    /**
     * 通过好友索引删除好友
     * @param refId 好友索引
     */
    public static boolean deleteFriendByRefId(String refId){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return FriendManager.getInstance().deleteFriendByRefId(refId);
        }
    }


    /**
     * 获取好友列表
     * @return 好友列表
     */
    public static FriendList[] getFriends(){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return FriendManager.getInstance().getFriends();
        }
    }

    public static FriendList getFriends(String groupName){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return FriendManager.getInstance().getFriendListByName(groupName);
        }
    }

    public static Friend getFriendByRefId(String refId){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            return LinphoneManager.getInstance().getFriendByRefId(refId);
        }
    }

}
