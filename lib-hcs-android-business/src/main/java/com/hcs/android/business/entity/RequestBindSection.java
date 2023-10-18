package com.hcs.android.business.entity;

/**
 * 绑定病区请求
 */
public class RequestBindSection {

    /**
     * 病区名称
     */
    private String sectionName;
    public String getSectionName() {
        return sectionName;
    }
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    /**
     * 病区号
     */
    private String sectionNo;
    public String getSectionNo() {
        return sectionNo;
    }
    public void setSectionNo(String sectionNo) {
        this.sectionNo = sectionNo;
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
