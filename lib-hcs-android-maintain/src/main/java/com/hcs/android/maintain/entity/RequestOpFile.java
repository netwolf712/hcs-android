package com.hcs.android.maintain.entity;

import java.util.List;

/**
 * 请求文件操作
 */
public class RequestOpFile {
    /**
     * 移动文件
     */
    public final static int OPERATE_TYPE_MOVE = 1;

    /**
     * 拷贝文件
     */
    public final static int OPERATE_TYPE_COPY = 2;

    /**
     * 删除文件
     */
    private final static int OPERATE_TYPE_DELETE = 3;

    /**
     * 操作类型
     * 1移动，2拷贝，3删除
     */
    private int operateType;

    /**
     * 源文件目录
     */
    private List<String> srcFileList;

    /**
     * 目的文件目录
     */
    private String dstFolder;

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public List<String> getSrcFileList() {
        return srcFileList;
    }

    public void setSrcFileList(List<String> srcFileList) {
        this.srcFileList = srcFileList;
    }

    public String getDstFolder() {
        return dstFolder;
    }

    public void setDstFolder(String dstFolder) {
        this.dstFolder = dstFolder;
    }
}
