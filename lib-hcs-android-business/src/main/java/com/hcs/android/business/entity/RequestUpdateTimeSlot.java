package com.hcs.android.business.entity;

/**
 * 请求更新继任主机
 */
public class RequestUpdateTimeSlot {
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
    private ResponseList<TimeSlot> responseList;

    public ResponseList<TimeSlot> getResponseList() {
        return responseList;
    }

    public void setResponseList(ResponseList<TimeSlot> responseList) {
        this.responseList = responseList;
    }

    /**
     * 时间段类型
     * null表示所有
     */
    private Integer timeSlotType;

    public Integer getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(Integer timeSlotType) {
        this.timeSlotType = timeSlotType;
    }
}
