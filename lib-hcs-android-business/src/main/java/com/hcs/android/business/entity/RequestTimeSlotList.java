package com.hcs.android.business.entity;

/**
 * 请求时间段列表
 */
public class RequestTimeSlotList extends RequestListBase{
    /**
     * 时间段类型
     * null表示所有
     */
    private Integer timeSlotType;
    public Integer getTimeSlotType(){
        return timeSlotType;
    }
    public void setTimeSlotType(Integer timeSlotType){
        this.timeSlotType = timeSlotType;
    }

    public RequestTimeSlotList(){

    }
    public RequestTimeSlotList(Integer timeSlotType, int currentPage, int pageSize){
        setTimeSlotType(timeSlotType);
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }
}
