package com.hcs.android.maintain.entity;

/**
 * 回复配置情况
 */
public class ResponseConfig {
    /**
     * 日志类型
     * pcap,logcat,dmesg
     */
    private String logType;

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    /**
     * 日志目录
     */
    private String logDir;

    /**
     * 日志最长保存时间
     * 单位毫秒
     */
    private long maxKeepTime;

    /**
     * 最大文件数量
     */
    private int maxFileCount;

    /**
     * 最大文件大小
     * 单位kb
     */
    private long maxFileSize;

    /**
     * 是否开启
     */
    private boolean open;

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public long getMaxKeepTime() {
        return maxKeepTime;
    }

    public void setMaxKeepTime(long maxKeepTime) {
        this.maxKeepTime = maxKeepTime;
    }

    public int getMaxFileCount() {
        return maxFileCount;
    }

    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
