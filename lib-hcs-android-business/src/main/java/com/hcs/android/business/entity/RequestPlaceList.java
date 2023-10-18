package com.hcs.android.business.entity;

/**
 * 请求位置列表
 */
public class RequestPlaceList extends RequestListBase{
    /**
     * 位置类型
     */
    private Integer placeType;
    public Integer getPlaceType(){
        return placeType;
    }
    public void setPlaceType(Integer placeType){
        this.placeType = placeType;
    }

    public RequestPlaceList(){

    }
    public RequestPlaceList(Integer placeType, int currentPage, int pageSize){
        setPlaceType(placeType);
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }
}
