package com.hcs.android.maintain.constant;

import com.hcs.android.common.util.StringUtil;

/**
 * 文件类型枚举
 */
public enum FileTypeEnum {
    /**
     * pcap日志
     */
    LOG_PCAP("log","pcap"),
    /**
     * logcat
     */
    LOG_LOGCAT("log","logcat"),
    /**
     * dmesg
     */
    LOG_DMESG("log","dmesg"),
    /**
     * 图片
     */
    MEDIA_IMAGE("media","image"),
    /**
     * 声音
     */
    MEDIA_AUDIO("media","audio"),
    /**
     * 视频
     */
    MEDIA_VIDEO("media","video"),
    /**
     * 数据库
     */
    OTHER_DATABASE("other","database"),
    /**
     * 数据备份
     */
    OTHER_BACKUP("other","backup"),
    /**
     * 任意类型
     */
    OTHER_ANY("other","any");
    /**
     * 文件主类型
     */
    private final String mainType;

    /**
     * 文件子类型
     */
    private final String subType;

    FileTypeEnum(String mainType,String subType){
        this.mainType = mainType;
        this.subType = subType;
    }


    /**
     * 查找枚举
     */
    public static FileTypeEnum find(String mainType,String subType){
        if(StringUtil.isEmpty(mainType) || StringUtil.isEmpty(subType)){
            return OTHER_ANY;
        }
        for(FileTypeEnum filterEnum : FileTypeEnum.values()){
            if(mainType.equalsIgnoreCase(filterEnum.getMainType()) && subType.equalsIgnoreCase(filterEnum.getSubType())){
                return filterEnum;
            }
        }
        return OTHER_ANY;
    }

    public String getMainType(){
        return mainType;
    }

    public String getSubType(){
        return subType;
    }
}
