package com.hcs.calldemo.entity;

/**
 * 好友详情
 * 此处用于测试org.linphone.core.Friend中的userData
 */
public class FriendDetail {
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
}
