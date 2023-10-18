package com.hcs.android.common.log;

import static com.hcs.android.common.util.ExeCommand.executeSuCmd;

import android.content.Context;


import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ZipFileUtil;
import com.hcs.android.common.util.log.KLog;

import java.io.File;
import java.util.Date;

public abstract class AbstractLogManager {

    /**
     * 用于存储最大文件大小配置的键值
     */
    private final static String KEY_BASE_PROPERTY_MAX_FILE_SIZE = "log_max_file_size_";

    /**
     * 用于存储最大持续时间的键值
     */
    private final static String KEY_BASE_PROPERTY_MAX_KEEP_TIME = "log_max_keep_time_";

    /**
     * 用于存储最大文件数量配置的键值
     */
    private final static String KEY_BASE_PROPERTY_MAX_FILE_COUNT = "log_max_file_count_";

    /**
     * 用于存储文件路径配置的键值
     */
    private final static String KEY_BASE_LOG_DIR = "log_dir_";

    private Context mContext;
    /**
     * 日志抓取时间，单位秒
     */
    private final static long DEFAULT_MAX_KEEP_TIME = 120;

    /**
     * 日志文件数量
     */
    private final static int DEFAULT_MAX_FILE_COUNT = 3;

    /**
     * 文件大小，单位kb
     */
    private final static long DEFAULT_MAX_FILE_SIZE = 102400;
    /**
     * 开启日志
     */
    public abstract void startLog();

    /**
     * 关闭日志
     */
    public abstract void stopLog();

    /**
     * 获取日志类型的标签
     */
    public abstract String getLogTag();
    /**
     * 日志是否打开中
     */
    public abstract boolean isOpen();

    public AbstractLogManager(Context context){
        mContext = context;
    }

    private String getGlobalProperties(String key){
        return SettingsHelper.getInstance(mContext).getString(key,"");
    }
    private void setGlobalProperties(String key,String value){
        SettingsHelper.getInstance(mContext).putData(key,value);
    }
    /**
     * 设置日志目录
     * @param logDir 日志目录
     */
    public void setLogDir(String logDir){
        setGlobalProperties(KEY_BASE_LOG_DIR + getLogTag(),logDir);
    }

    /**
     * 获取日志目录
     * @return 日志目录
     */
    public String getLogDir(){
        String logDir = getGlobalProperties(KEY_BASE_LOG_DIR + getLogTag());

        if(StringUtil.isEmpty(logDir)){
            return getBaseLogDir() + "/" + getLogTag();
        }else {
            return logDir;
        }
    }

    /**
     * 基础路径
     */
    public String getBaseLogDir(){
        return FileUtil.getAppFileDir() + "/logs";
    }

    /**
     * 获取日志文件后缀名
     */
    public abstract String getFileExtensionName();
    /**
     * 日志抓取时间，单位秒
     */
    public void setMaxKeepTime(long maxKeepTime){
        setGlobalProperties(KEY_BASE_PROPERTY_MAX_KEEP_TIME + getLogTag(),String.valueOf(maxKeepTime));
    }
    public long getMaxKeepTime(){
        String maxKeepTime = getGlobalProperties(KEY_BASE_PROPERTY_MAX_KEEP_TIME + getLogTag());
        if(StringUtil.isEmpty(maxKeepTime)){
            return DEFAULT_MAX_KEEP_TIME;
        }else{
            return Long.parseLong(maxKeepTime);
        }
    }

    /**
     * 日志文件数量
     */
    public void setMaxFileCount(int maxFileCount){
        setGlobalProperties(KEY_BASE_PROPERTY_MAX_FILE_COUNT + getLogTag(),String.valueOf(maxFileCount));
    }
    public int getMaxFileCount(){
        String maxFileCount = getGlobalProperties(KEY_BASE_PROPERTY_MAX_FILE_COUNT + getLogTag());
        if(StringUtil.isEmpty(maxFileCount)){
            return DEFAULT_MAX_FILE_COUNT;
        }else{
            return Integer.parseInt(maxFileCount);
        }
    }

    /**
     * 文件大小，单位kb
     */
    public void setMaxFileSize(long maxFileSize){
        setGlobalProperties(KEY_BASE_PROPERTY_MAX_FILE_SIZE + getLogTag(),String.valueOf(maxFileSize));
    }
    public long getMaxFileSize(){
        String maxFileSize = getGlobalProperties(KEY_BASE_PROPERTY_MAX_FILE_SIZE + getLogTag());
        if(StringUtil.isEmpty(maxFileSize)){
            return DEFAULT_MAX_FILE_SIZE;
        }else{
            return Long.parseLong(maxFileSize);
        }
    }

    /**
     * 异步形式开启日志
     * 解决在ui现成调用接口的问题
     */
    public void startLogAsyn(){
        new Thread(this::startLog).start();
    }

    /**
     * 异步形式关闭日志
     * 解决在ui现成调用接口的问题
     */
    public void stopLogAsyn(){
        new Thread(this::stopLog).start();
    }

    /**
     *
     */
    protected void checkFile(){
        try {
            File dirFile = new File(getLogDir());
            if (!dirFile.exists()){
                FileUtil.createOrExistsDir(dirFile);
                //executeSuCmd("mkdir -p " + getLogDir());
                executeSuCmd("chmod  -R 777 " + getLogDir());
            }

            long earlierTime = 0;
            int fileCount = 0;
            String oldFile = "";
            if (dirFile.canRead()) {
                File[] fileList = dirFile.listFiles();
                if (fileList == null || fileList.length == 0) {
                    return;
                }
                for (File file : fileList) {
                    //遍历目录，找出所有文件
                    int index = file.toString().lastIndexOf("/");
                    String name = file.toString().substring(index + 1);
                    if (!name.startsWith(getLogTag())) continue;

                    String dateFromName = name.substring(getLogTag().length(), getLogTag().length() + 15);
                    Date tmpDate = DateUtil.parseTime(dateFromName, DateUtil.FormatType.yyyyMMdd_HHmmss);
                    KLog.d(getLogTag(), "file: date: = " + dateFromName + "   time: " + tmpDate.getTime());
                    if (earlierTime == 0) {
                        earlierTime = tmpDate.getTime();
                        oldFile = name;
                    } else {
                        if (tmpDate.getTime() < earlierTime) {
                            earlierTime = tmpDate.getTime();
                            oldFile = name;
                        }
                    }
                    fileCount++;
                }
            } else {
                return;
            }
            if (fileCount > getMaxFileCount()) {
                //删除超出的最老的文件。
                KLog.d(getLogTag(), "checkDir: delete file : " + getLogDir() + "/" + oldFile);
                executeSuCmd("rm " + getLogDir() + "/" + oldFile);
            }
        }catch (Exception e){
            KLog.e(e);
        }
    }

    /**
     * 获取日志文件名称
     */
    protected String getFileName(){
        String date = DateUtil.formatDate(new Date(), DateUtil.FormatType.yyyyMMdd_HHmmss);
        return getLogTag() + date + getFileExtensionName();
    }
}
