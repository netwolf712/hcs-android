package com.hcs.android.business.entity;

/**
 * 列表请求的基础对象
 */
public class RequestListBase {
    /**
     * 当前分页
     */
    private Integer currentPage;
    public Integer getCurrentPage(){
        return currentPage;
    }
    public void setCurrentPage(Integer currentPage){
        this.currentPage = currentPage;
    }

    /**
     * 每页大小
     */
    private Integer pageSize;
    public Integer getPageSize(){
        return pageSize;
    }
    public void setPageSize(Integer pageSize){
        this.pageSize = pageSize;
    }

    /**
     * 请求哪个主机的数据
     * 可以为空
     * 特定场合需要
     */
    private String masterDeviceId;

    public String getMasterDeviceId() {
        return masterDeviceId;
    }

    public void setMasterDeviceId(String masterDeviceId) {
        this.masterDeviceId = masterDeviceId;
    }
}
