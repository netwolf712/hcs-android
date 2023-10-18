package com.hcs.commondemo.config;

import android.content.Context;
import android.os.Environment;

/**
 * 应用层的配置
 */
public class Config {
    /**
     * 录音文件后缀名
     */
    public static final String RECORD_EXTENSION_NAME = ".mp4";
    /**
     * 录音录像存放目录
     */
    public static final String RECORD_BASE_PATH = "recordings";

    /**
     * 获取录像存放绝对路径
     * @param context 上下文
     * @return 绝对路径
     */
    public static String getRecordPath(Context context){
        String recordPath =
                Environment.getExternalStorageDirectory()
                        + "/"
                        + context.getString(
                        context.getResources()
                                .getIdentifier(
                                        "app_name", "string", context.getPackageName()));


        return recordPath;
    }
}
