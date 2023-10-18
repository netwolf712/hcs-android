package com.hcs.android.business.entity;

import java.util.List;

/**
 * 绑定分区请求
 */
public class RequestBindGroup {
    /**
     * 分区号
     */
    private Integer groupSn;

    public Integer getGroupSn() {
        return groupSn;
    }

    public void setGroupSn(Integer groupSn) {
        this.groupSn = groupSn;
    }

    /**
     * 位置uid列表
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
