package com.hcs.android.business.entity;

import java.util.List;

/**
 * 绑定病房门请求
 */
public class RequestBindRoom {
    /**
     * 病房号
     */
    private String roomNo;

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    /**
     * 病床序号列表
     */
    private List<Integer> bedSnList;

    public List<Integer> getBedSnList() {
        return bedSnList;
    }

    public void setBedSnList(List<Integer> bedSnList) {
        this.bedSnList = bedSnList;
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
