package com.hcs.android.call.api;

import androidx.annotation.Nullable;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import org.linphone.core.Address;
import org.linphone.core.Core;
import org.linphone.core.Friend;
import org.linphone.core.FriendList;

/**
 * 好友管理
 */
public class FriendManager {

    private static final class MInstanceHolder {
        static final FriendManager mInstance = new FriendManager();
    }

    public static FriendManager getInstance(){
        return MInstanceHolder.mInstance;
    }
    @Nullable
    private synchronized Friend createOrUpdateFriend(String phoneNumber, String name, String remoteAddress, String group, Object userData) {
        boolean created = false;
        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc == null) {
            return null;
        }
        //找到分组
        FriendList friendList;
        if(!StringUtil.isEmpty(group)){
            friendList = LinphoneManager.getInstance().getFriendListByName(group);
        }else {
            friendList = lc.getDefaultFriendList();
        }
        if(friendList == null){
            friendList = lc.createFriendList();
            friendList.setDisplayName(group);
            lc.addFriendList(friendList);
        }

        Friend friend = LinphoneManager.getInstance().getFriendByRefId(phoneNumber);

        if (friend == null) {
            friend = lc.createFriend();
            friend.setSubscribesEnabled(false);
            //friend.setIncSubscribePolicy(SubscribePolicy.SPAccept);
            //此处先将电话号码作为索引
            friend.setRefKey(phoneNumber);
            friend.setUserData(userData);
            created = true;
        }
        {
            friend.edit();
            friend.setName(name);
            friend.setUserData(userData);
            if (!created) {
                for (Address address : friend.getAddresses()) {
                    friend.removeAddress(address);
                }
                for (String phone : friend.getPhoneNumbers()) {
                    friend.removePhoneNumber(phone);
                }
                if (friend.getVcard() != null){
                    for(Address sipAddress : friend.getVcard().getSipAddresses()){
                        friend.getVcard().removeSipAddress(sipAddress.asString());
                    }
                }
            }
            Address addr = lc.interpretUrl(remoteAddress);
            if(addr != null) {
                friend.addAddress(addr);
            }
            friend.addPhoneNumber(phoneNumber);
            if (friend.getVcard() != null) {
                friend.getVcard().setFamilyName(name);
                //friend.getVcard().setGivenName(name);
                friend.getVcard().addSipAddress(remoteAddress);
                if(!StringUtil.isEmpty(group)) {
                    friend.getVcard().setOrganization(group);
                }
                friend.getVcard().setUserData(userData);
            }
            //关闭订阅功能
            friend.setSubscribesEnabled(false);
            friend.done();
        }
        if (created) {
            FriendList.Status status = friendList.addFriend(friend);
            KLog.i("add friend status " + status);
            if(status != FriendList.Status.OK){
                return null;
            }
//            lc.addFriend(friend);
            if(friend.getRefKey() != null) {
                return lc.getFriendByRefKey(friend.getRefKey());
            }
        }

        return friend;
    }

    /**
     * 添加朋友
     * 无则添加，有则修改
     * @param phoneNumber 电话号码(=username)
     * @param name 显示名称
     * @param remoteAddress 远端地址 ip:port
     * @param group 所属分组
     * @param userData 用户自定义数据
     */
    public synchronized Friend addFriend(String phoneNumber,String name,String remoteAddress,String group,Object userData){
        return createOrUpdateFriend(phoneNumber, name, remoteAddress,group,userData);
    }

    private synchronized boolean deleteFriend(Friend friend){
        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (friend != null && lc != null) {
            for (FriendList list : lc.getFriendsLists()) {
               FriendList.Status status = list.removeFriend(friend);
               if(status == FriendList.Status.OK){
                   return true;
               }
            }
        }
        return false;
    }
    public synchronized boolean deleteFriendByPhoneNumber(String phoneNumber){
        Friend friend = LinphoneManager.getInstance().getFriendByPhoneNumber(phoneNumber);
        if(friend != null){
            return deleteFriend(friend);
        }
        return false;
    }

    public synchronized boolean deleteFriendByRefId(String refId){
        Friend friend = LinphoneManager.getInstance().getFriendByRefId(refId);
        if(friend != null){
            return deleteFriend(friend);
        }
        return false;
    }


    public synchronized FriendList[] getFriends(){
        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc == null) {
            return null;
        }
        return lc.getFriendsLists();
    }

    public synchronized FriendList getFriendListByName(String name){
        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc == null) {
            return null;
        }
        return lc.getFriendListByName(name);
    }
}
