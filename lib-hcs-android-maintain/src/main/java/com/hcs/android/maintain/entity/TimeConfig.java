package com.hcs.android.maintain.entity;

/**
 * 时间配置
 */
public class TimeConfig {
    /**
     * 是否启用NTP功能
     */
    private boolean enableNtp;

    /**
     * NTP服务器地址
     */
    private String ntpServer;

    /**
     * 时间戳
     */
    private Long unixTime;

    /**
     * 是否24小时制
     */
    private boolean hour24;

    /**
     * 是否自动判断时区
     */
    private boolean autoTimeZone;

    /**
     * 当前时区
     */
    private String timeZoneId;

    /**
     * 时区的显示名称
     */
    private String timeZoneName;

    public boolean isEnableNtp() {
        return enableNtp;
    }

    public void setEnableNtp(boolean enableNtp) {
        this.enableNtp = enableNtp;
    }

    public String getNtpServer() {
        return ntpServer;
    }

    public void setNtpServer(String ntpServer) {
        this.ntpServer = ntpServer;
    }

    public Long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(Long unixTime) {
        this.unixTime = unixTime;
    }

    public boolean isHour24() {
        return hour24;
    }

    public void setHour24(boolean hour24) {
        this.hour24 = hour24;
    }

    public boolean isAutoTimeZone() {
        return autoTimeZone;
    }

    public void setAutoTimeZone(boolean autoTimeZone) {
        this.autoTimeZone = autoTimeZone;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }
}
