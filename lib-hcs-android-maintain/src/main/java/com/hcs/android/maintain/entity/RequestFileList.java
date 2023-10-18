package com.hcs.android.maintain.entity;

public class RequestFileList {
    /**
     * 文件相对路径
     */
    private String dir;

    /**
     * 文件主类型
     */
    private String mainType;

    /**
     * 文件子类型
     */
    private String subType;

    /**
     * 是否返回目录文件
     */
    private boolean withDir;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public boolean isWithDir() {
        return withDir;
    }

    public void setWithDir(boolean withDir) {
        this.withDir = withDir;
    }
}
