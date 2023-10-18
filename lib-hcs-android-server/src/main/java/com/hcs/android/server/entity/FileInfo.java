package com.hcs.android.server.entity;

/**
 * 文件信息
 */
public class FileInfo {
    /**
     * 文件绝对路径
     */
    private String filePath;
    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件大小，单位kb
     */
    private double length;

    /**
     * 是否是目录
     */
    private boolean isDir;

    /**
     * 文件是否准备好
     */
    private boolean ready;

    public FileInfo() {
    }

    public FileInfo(String name,String filePath, double length, boolean isDir) {
        this.name = name;
        this.filePath = filePath;
        this.length = length;
        this.isDir = isDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
