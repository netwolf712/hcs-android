package com.hcs.android.business.entity;

/**
 * 请求附件列表
 */
public class RequestAttachmentList extends RequestListBase{
    /**
     * 用途
     */
    private String use;
    public String getUse(){
        return use;
    }
    public void setUse(String use){
        this.use = use;
    }

    public RequestAttachmentList(){

    }
    public RequestAttachmentList(String use, int currentPage, int pageSize){
        setUse(use);
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }
}
