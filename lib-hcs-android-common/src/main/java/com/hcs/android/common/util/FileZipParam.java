package com.hcs.android.common.util;

public class FileZipParam {
    private String path;
    private String parentDirPath;

    public FileZipParam(String path){
        this.path = path;
    }
    public FileZipParam(String path, String parentDirPath){
        this.path = path;
        this.parentDirPath = parentDirPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentDirPath() {
        return parentDirPath;
    }

    public void setParentDirPath(String parentDirPath) {
        this.parentDirPath = parentDirPath;
    }

    @Override
    public String toString() {
        return "FileZipParam{" +
                "path='" + path + '\'' +
                ", parentDirPath='" + parentDirPath + '\'' +
                '}';
    }
}
