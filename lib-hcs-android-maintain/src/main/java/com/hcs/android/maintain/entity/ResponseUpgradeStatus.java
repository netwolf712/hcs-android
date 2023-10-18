package com.hcs.android.maintain.entity;

/**
 * 回复升级状态
 */
public class ResponseUpgradeStatus {
    /**
     * 空闲，没有在升级
     */
    public final static int STATUS_IDLE = 0;

    /**
     * 正在拷贝文件
     */
    public final static int STATUS_COPYING_FILE = 1;

    /**
     * 正在解压缩文件
     */
    public final static int STATUS_UNZIPPING_FILE = 2;

    /**
     * 正在升级
     */
    public final static int STATUS_UPGRADING = 3;

    /**
     * 升级完成
     */
    public final static int STATUS_FINISHED = 4;

    /**
     * 升级失败
     */
    public final static int STATUS_FAILED = 5;

    /**
     * 校验中
     */
    public final static int STATUS_CHECKING = 6;

    /**
     * 升级状态
     */
    private Integer status;

    /**
     * 原因
     */
    private String reason;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
