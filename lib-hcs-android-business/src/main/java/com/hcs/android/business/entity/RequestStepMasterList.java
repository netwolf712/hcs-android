package com.hcs.android.business.entity;

/**
 * 请求继任主机列表
 */
public class RequestStepMasterList extends RequestListBase{
    /**
     * 主机类型
     * null表示所有
     */
    private Integer stepMasterType;
    public Integer getStepMasterType(){
        return stepMasterType;
    }
    public void setStepMasterType(Integer stepMasterType){
        this.stepMasterType = stepMasterType;
    }

    public RequestStepMasterList(){

    }
    public RequestStepMasterList(Integer stepMasterType, int currentPage, int pageSize){
        setStepMasterType(stepMasterType);
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }
}
