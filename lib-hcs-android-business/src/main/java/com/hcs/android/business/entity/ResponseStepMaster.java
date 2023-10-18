package com.hcs.android.business.entity;

public class ResponseStepMaster extends ResponseList<StepMaster>{
    /**
     * 呼叫等待时间
     * （只有StepMasterType = SUPERIOR（上级主机）时才有效）
     */
    private Integer callWaitTime;

    /**
     * 主机号码
     * （只有StepMasterType = SUPERIOR（上级主机）时才有效）
     */
    private String masterNo;

    public Integer getCallWaitTime() {
        return callWaitTime;
    }

    public void setCallWaitTime(Integer callWaitTime) {
        this.callWaitTime = callWaitTime;
    }

    public String getMasterNo() {
        return masterNo;
    }

    public void setMasterNo(String masterNo) {
        this.masterNo = masterNo;
    }
}
