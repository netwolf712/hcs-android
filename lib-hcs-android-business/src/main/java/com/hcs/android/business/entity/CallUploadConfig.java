package com.hcs.android.business.entity;

import java.util.List;

/**
 * 呼叫上传配置
 */
public class CallUploadConfig {
    /**
     * 呼叫上传主机数量
     */
    private int masterPhoneCount;

    /**
     * 上传主机列表
     */
    private List<String> masterList;

    /**
     * 上传等待时间
     */
    private int callUploadWaitTime;

    /**
     * 禁止上传时间段数量
     */
    private int forbidUploadTimeCount;

    /**
     * 禁止上传的时间段
     */
    private List<String> forbidUploadTimeList;
}
