package com.hcs.calldemo.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hcs.android.common.util.StringUtil;
import com.hcs.calldemo.BR;

import org.linphone.core.Address;
import org.linphone.core.Friend;
import org.linphone.core.PresenceModel;

/**
 * 用于测试的好友对象
 */
public class FriendBo extends BaseObservable {
    /**
     * 电话号码
     */
    private String phoneNO;
    public String getPhoneNO(){
        return phoneNO;
    }
    public void setPhoneNO(String phoneNO){
        this.phoneNO = phoneNO;
    }

    /**
     * 地址
     */
    private String address;
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    /**
     * 显示名称
     */
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    /**
     * 索引id
     */
    private String refId;
    public String getRefId(){
        return refId;
    }
    public void setRefId(String refId){
        this.refId = refId;
    }

    /**
     * 分组名称
     */
    private String groupName;
    public String getGroupName(){
        return groupName;
    }
    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    /**
     * 当前状态
     */
    private String presence;
    @Bindable
    public String getPresence(){
        return presence;
    }
    public void setPresence(String presence){
        this.presence = presence;
        notifyPropertyChanged(BR.presence);
    }

    private FriendDetail friendDetail;
    public FriendDetail getFriendDetail(){
        return friendDetail;
    }
    public void setFriendDetail(FriendDetail friendDetail){
        this.friendDetail = friendDetail;
    }
    public FriendBo(){

    }
    public FriendBo(Friend friend){
        this.setRefId(friend.getRefKey());
        this.setPhoneNO(StringUtil.isEmpty(friend.getPhoneNumbers()) ? "" : friend.getPhoneNumbers()[0]);
        String strAddress = "";
        if(friend.getAddress() != null){
            Address address = friend.getAddress();
            strAddress = address.getDomain();
        }
        this.setAddress(strAddress);
        this.setName(friend.getName());
        PresenceModel pm = friend.getPresenceModel();
        if(pm != null) {
            this.setPresence(String.valueOf(pm.getActivity().getType().toInt()));
        }

        Object obj = friend.getUserData();
        if(obj != null && obj instanceof FriendDetail){
            this.setFriendDetail((FriendDetail)obj);
            this.setGroupName(getFriendDetail().getGroupName());
        }
    }

}
