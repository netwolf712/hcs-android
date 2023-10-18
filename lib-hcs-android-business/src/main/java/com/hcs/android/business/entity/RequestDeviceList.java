package com.hcs.android.business.entity;

/**
 * 请求设备列表
 */
public class RequestDeviceList extends RequestListBase{
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

    public RequestDeviceList(){

    }
    public RequestDeviceList(String groupName,int currentPage,int pageSize){
        setGroupName(groupName);
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }
}
