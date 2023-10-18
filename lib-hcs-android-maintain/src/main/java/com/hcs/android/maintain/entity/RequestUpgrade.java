package com.hcs.android.maintain.entity;

/**
 * 升级请求相关参数
 */
public class RequestUpgrade {
    /**
     * 升级apk
     */
    public final static int UPGRADE_TYPE_APK = 0;

    /**
     * 升级系统
     */
    public final static int UPGRADE_TYPE_SYSTEM = 1;

    /**
     * 升级文件路径
     */
    private String filePath;

    /**
     * 升级类型
     * 对应UPGRADE_TYPE_APK、UPGRADE_TYPE_SYSTEM
     */
    private Integer upgradeType;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(Integer upgradeType) {
        this.upgradeType = upgradeType;
    }
}
