package com.hcs.android.maintain.entity;

/**
 * 存活回复
 */
public class ResponseAlive {
    public ResponseAlive(long startTime){
        this.startTime = startTime;
    }
    /**
     * 应用启动时间
     */
    private long startTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
