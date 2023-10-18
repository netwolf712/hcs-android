package com.hcs.android.maintain;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.log.AbstractLogManager;
import com.hcs.android.common.log.DmesgManager;
import com.hcs.android.common.log.LogcatManager;
import com.hcs.android.common.log.PcapManager;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.maintain.constant.PreferenceConstant;
import com.hcs.android.maintain.controller.WebController;
import com.hcs.android.maintain.entity.RequestSetConfig;
import com.hcs.android.maintain.entity.ResponseConfig;
import com.hcs.android.server.web.WebServer;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志管理器
 */
public class LogManager {
    /**
     * 各种日志类型定义
     */
    public final static String LOG_PCAP = "pcap";
    public final static String LOG_LOGCAT = "logcat";
    public final static String LOG_DMESG = "dmesg";

    private DmesgManager mDmesgManager;
    private PcapManager mPcapManager;
    private LogcatManager mLogcatManager;

    @SuppressLint("StaticFieldLeak")
    private static LogManager mInstance = null;
    public static LogManager getInstance(){
        if(mInstance == null){
            synchronized (LogManager.class) {
                if(mInstance == null) {
                    mInstance = new LogManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mDmesgManager = new DmesgManager(context);
        mLogcatManager = new LogcatManager(context);
        mPcapManager = new PcapManager(context);
        if(SettingsHelper.getInstance(context).getBoolean(PreferenceConstant.PREF_KEY_LOG_DMESG,context.getResources().getBoolean(R.bool.default_maintain_log_dmesg_open))){
            mDmesgManager.startLog();
        }
        if(SettingsHelper.getInstance(context).getBoolean(PreferenceConstant.PREF_KEY_LOG_LOGCAT,context.getResources().getBoolean(R.bool.default_maintain_log_logcat_open))){
            mLogcatManager.startLog();
        }
        if(SettingsHelper.getInstance(context).getBoolean(PreferenceConstant.PREF_KEY_LOG_PCAP,context.getResources().getBoolean(R.bool.default_maintain_log_pcap_open))){
            mPcapManager.startLog();
        }
    }

    /**
     * 根据各日志的特点开放对应的配置接口
     */
    /**
     * pcap的最大抓包时间
     */
    public long getPcapMaxKeepTime(){
        return mPcapManager.getMaxKeepTime();
    }
    public void setPcapMaxKeepTime(long maxKeepTime){
        mPcapManager.setMaxKeepTime(maxKeepTime);
    }

    /**
     * pcap的最大文件数量
     */
    public int getPcapMaxFileCount(){
        return mPcapManager.getMaxFileCount();
    }
    public void setPcapMaxFileCount(int maxFileCount){
        mPcapManager.setMaxFileCount(maxFileCount);
    }

    /**
     * pcap的日志路径
     */
    public String getPcapLogDir(){
        return mPcapManager.getLogDir();
    }
    public void setPcapLogDir(String logDir){
        mPcapManager.setLogDir(logDir);
    }

    /**
     * dmesg的最大文件数量
     */
    public int getDmesgMaxFileCount(){
        return mDmesgManager.getMaxFileCount();
    }
    public void setDmesgMaxFileCount(int maxFileCount){
        mDmesgManager.setMaxFileCount(maxFileCount);
    }

    /**
     * dmesg的日志路径
     */
    public String getDmesgLogDir(){
        return mDmesgManager.getLogDir();
    }
    public void setDmesgLogDir(String logDir){
        mDmesgManager.setLogDir(logDir);
    }

    /**
     * logcat的最大文件数量
     */
    public int getLogcatMaxFileCount(){
        return mLogcatManager.getMaxFileCount();
    }
    public void setLogcatMaxFileCount(int maxFileCount){
        mLogcatManager.setMaxFileCount(maxFileCount);
    }

    /**
     * Logcat的日志路径
     */
    public String getLogcatLogDir(){
        return mDmesgManager.getLogDir();
    }
    public void setLogcatLogDir(String logDir){
        mDmesgManager.setLogDir(logDir);
    }

    /**
     * Logcat的最大文件大小
     */
    public long getLogcatMaxFileSize(){
        return mDmesgManager.getMaxFileSize();
    }
    public void setLogcatMaxFileSize(long maxFileSize){
        mDmesgManager.setMaxFileSize(maxFileSize);
    }

    /**
     * 获取指定类型的日志配置
     */
    public ResponseConfig getLogConfig(String logType){
        ResponseConfig responseConfig = new ResponseConfig();
        responseConfig.setLogType(logType);
        AbstractLogManager abstractLogManager;
        if(LOG_PCAP.equalsIgnoreCase(logType)){
            abstractLogManager = mPcapManager;
        }else if(LOG_LOGCAT.equalsIgnoreCase(logType)){
            abstractLogManager = mLogcatManager;
        }else{
            abstractLogManager = mDmesgManager;
        }
        responseConfig.setMaxFileCount(abstractLogManager.getMaxFileCount());
        responseConfig.setMaxFileSize(abstractLogManager.getMaxFileSize());
        responseConfig.setMaxKeepTime(abstractLogManager.getMaxKeepTime());
        responseConfig.setLogDir(abstractLogManager.getLogDir());
        responseConfig.setOpen(abstractLogManager.isOpen());
        return responseConfig;
    }

    /**
     * 获取所有类型的日志的配置
     * @param logType 日志类型，为空时表示获取所有
     */
    public List<ResponseConfig> getLogConfigList(String logType){
        List<ResponseConfig> responseConfigList = new ArrayList<>();
        if(StringUtil.isEmpty(logType)) {
            responseConfigList.add(getLogConfig(LOG_PCAP));
            responseConfigList.add(getLogConfig(LOG_LOGCAT));
            responseConfigList.add(getLogConfig(LOG_DMESG));
        }else{
            responseConfigList.add(getLogConfig(logType));
        }
        return responseConfigList;
    }
    /**
     * 对指定类型的日志进行配置
     */
    public void setLogConfig(@NonNull RequestSetConfig requestSetConfig){
        String logType = requestSetConfig.getLogType();
        AbstractLogManager abstractLogManager;
        if(LOG_PCAP.equalsIgnoreCase(logType)){
            abstractLogManager = mPcapManager;
        }else if(LOG_LOGCAT.equalsIgnoreCase(logType)){
            abstractLogManager = mLogcatManager;
        }else{
            abstractLogManager = mDmesgManager;
        }
        abstractLogManager.setLogDir(requestSetConfig.getLogDir());
        abstractLogManager.setMaxFileCount(requestSetConfig.getMaxFileCount());
        abstractLogManager.setMaxFileSize(requestSetConfig.getMaxFileSize());
        abstractLogManager.setMaxKeepTime(requestSetConfig.getMaxKeepTime());
        if(requestSetConfig.isOpen()){
            abstractLogManager.startLog();
        }else{
            abstractLogManager.stopLog();
        }
    }

    public void setLogConfig(List<RequestSetConfig> requestSetConfigList){
        if(!StringUtil.isEmpty(requestSetConfigList)){
            for(RequestSetConfig requestSetConfig : requestSetConfigList){
                setLogConfig(requestSetConfig);
            }
        }
    }
}
