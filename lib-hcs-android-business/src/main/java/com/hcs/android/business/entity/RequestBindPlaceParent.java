package com.hcs.android.business.entity;

import java.util.List;

/**
 * 绑定父位置请求
 */
public class RequestBindPlaceParent {
    /**
     * 父位置编号
     */
    private String parentPlaceNo;

    public String getParentPlaceNo() {
        return parentPlaceNo;
    }

    public void setParentPlaceNo(String parentPlaceNo) {
        this.parentPlaceNo = parentPlaceNo;
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
     * 父位置类型
     */
    private Integer parentPlaceType;

    public Integer getParentPlaceType() {
        return parentPlaceType;
    }

    public void setParentPlaceType(Integer parentPlaceType) {
        this.parentPlaceType = parentPlaceType;
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
