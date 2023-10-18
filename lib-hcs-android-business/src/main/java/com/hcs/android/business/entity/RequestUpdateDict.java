package com.hcs.android.business.entity;

/**
 * 请求更新字典
 */
public class RequestUpdateDict {
    /**
     * 字典最近的更新时间
     */
    private Long lastUpdateTime;

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * 需要更新的字典数据
     */
    private ResponseList<Dict> responseList;

    public ResponseList<Dict> getResponseList() {
        return responseList;
    }

    public void setResponseList(ResponseList<Dict> responseList) {
        this.responseList = responseList;
    }
}
