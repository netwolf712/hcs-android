package com.hcs.android.maintain.entity;

/**
 * 恢复配置
 */
public class RequestRecoverConfig {
    /**
     * 用于还原的压缩包路径
     */
    private String recoverFilePath;

    /**
     * 还原的类型
     * 配置、数据等，先预留
     */
    private Integer recoverType;

    public String getRecoverFilePath() {
        return recoverFilePath;
    }

    public void setRecoverFilePath(String recoverFilePath) {
        this.recoverFilePath = recoverFilePath;
    }

    public Integer getRecoverType() {
        return recoverType;
    }

    public void setRecoverType(Integer recoverType) {
        this.recoverType = recoverType;
    }
}
