package com.hcs.android.maintain.entity;

/**
 * 请求获取日志配置
 */
public class RequestGetConfig {
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
}
