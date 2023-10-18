package com.hcs.android.business.entity;

/**
 * 请求更新继任主机
 */
public class RequestUpdateStepMaster {
    /**
     * 继任主机最近的更新时间
     */
    private Long lastUpdateTime;

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * 需要更新的继任主机数据
     */
    private ResponseList<StepMaster> responseList;

    public ResponseList<StepMaster> getResponseList() {
        return responseList;
    }

    public void setResponseList(ResponseList<StepMaster> responseList) {
        this.responseList = responseList;
    }

    /**
     * 继任主机类型
     * null表示所有
     */
    private Integer stepMasterType;

    public Integer getStepMasterType() {
        return stepMasterType;
    }

    public void setStepMasterType(Integer stepMasterType) {
        this.stepMasterType = stepMasterType;
    }
}
