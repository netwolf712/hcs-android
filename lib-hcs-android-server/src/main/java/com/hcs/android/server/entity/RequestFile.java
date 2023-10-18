package com.hcs.android.server.entity;

import java.util.List;

/**
 * 文件下载请求所需参数
 */
public class RequestFile {
    /**
     * 文件列表
     */
    private List<String> fileList;

    /**
     * 是否统一打包
     * （针对fileList里只有一份文件的情况下，true表示打包，否则就传原始文件）
     */
    private boolean zipAll;

    /**
     * 文件主类型标识，由业务层自己定
     * （如果：log、image、video等）
     */
    private String mainType;

    /**
     * 文件子类型标识，由业务层自己定
     * （如果：log、image、video等）
     */
    private String subType;

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public boolean isZipAll() {
        return zipAll;
    }

    public void setZipAll(boolean zipAll) {
        this.zipAll = zipAll;
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
}
