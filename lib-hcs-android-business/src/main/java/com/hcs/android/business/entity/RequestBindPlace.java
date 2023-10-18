package com.hcs.android.business.entity;

import java.util.List;

/**
 * 绑定位置请求
 */
public class RequestBindPlace {
    /**
     * 父位置uid
     */
    private String parentUid;

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    /**
     * 子位置uid列表
     */
    private List<String> placeUidList;

    public List<String> getPlaceUidList() {
        return placeUidList;
    }

    public void setPlaceUidList(List<String> placeUidList) {
        this.placeUidList = placeUidList;
    }


    /**
     * 主机号码
     * 对分机来说是主机号码
     * 对主机来说就是他自己
     */
    private String parentNo;
    public String getParentNo() {
        return parentNo;
    }
    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }
}
