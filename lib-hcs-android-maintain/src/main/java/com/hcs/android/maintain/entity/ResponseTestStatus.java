package com.hcs.android.maintain.entity;

/**
 * 测试状态
 */
public class ResponseTestStatus {
    /**
     * 空闲
     */
    public final static int STATUS_IDLE = 0;
    /**
     * 测试中
     */
    public final static int STATUS_TESTING = 1;

    /**
     * 测试结束
     */
    public final static int STATUS_FINISHED = 2;

    /**
     * 测试状态
     */
    private Integer status;

    /**
     * 测试结果
     */
    private String result;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
