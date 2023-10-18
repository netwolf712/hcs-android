package com.hcs.calldemo.viewmodel;

import com.hcs.android.call.api.FriendHelper;
import com.hcs.android.common.util.StringUtil;
import com.hcs.calldemo.entity.FriendBo;
import com.hcs.calldemo.entity.FriendDetail;

import org.linphone.core.Friend;

public class FriendDetailViewModel{

    private FriendBo friendBo;

    public FriendDetailViewModel() {

    }

    /**
     * 通过索引id获取好友
     * @param refId 索引id
     */
    public void loadFriend(String refId){
        Friend friend = FriendHelper.getFriendByRefId(refId);
        if(friend != null) {
            FriendBo friendBo = new FriendBo(friend);
            setFriendBo(friendBo);
        }
    }

    public void loadFriend(Friend friend){
        FriendBo friendBo = new FriendBo(friend);
        setFriendBo(friendBo);
    }
    /**
     * 保存好友信息
     */
    public boolean saveFriend(){
        if(friendBo != null) {
            FriendDetail friendDetail = friendBo.getFriendDetail();
            if(friendDetail == null){
                friendDetail = new FriendDetail();
            }
            friendDetail.setGroupName(friendBo.getGroupName());
            friendBo.setFriendDetail(friendDetail);
            if(FriendHelper.addFriend(friendBo.getPhoneNO(),friendBo.getName(),friendBo.getAddress(), friendBo.getGroupName(), friendBo.getFriendDetail()) != null);{
                return true;
            }
        }
        return false;
    }

    public boolean removeFriend(){
        if(friendBo != null && !StringUtil.isEmpty(friendBo.getRefId())){
            return FriendHelper.deleteFriendByRefId(friendBo.getRefId());
        }
        return false;
    }
    public FriendBo getFriendBo(){
        if(friendBo == null){
            friendBo = new FriendBo();
            friendBo.setName("1");
            friendBo.setGroupName("4");
            friendBo.setPhoneNO("2");
            friendBo.setAddress("xxx");
        }
        return friendBo;
    }
    public void setFriendBo(FriendBo friendBo){
        this.friendBo = friendBo;
    }
}

