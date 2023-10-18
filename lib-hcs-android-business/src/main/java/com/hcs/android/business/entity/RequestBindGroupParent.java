package com.hcs.android.business.entity;

import java.util.List;

/**
 * 位置主动请求绑定到分区
 */
public class RequestBindGroupParent {
    /**
     * 分区号
     */
    private String groupNo;

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    /**
     * 自己的位置uid
     */
    private String placeUid;

    public String getPlaceUid() {
        return placeUid;
    }

    public void setPlaceUid(String placeUid) {
        this.placeUid = placeUid;
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
