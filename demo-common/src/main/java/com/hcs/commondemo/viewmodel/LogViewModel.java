package com.hcs.commondemo.viewmodel;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.log.DmesgManager;
import com.hcs.android.common.log.LogcatManager;
import com.hcs.android.common.log.PcapManager;
import com.hcs.commondemo.entity.LogBo;

public class LogViewModel {
    private static LogViewModel sInstance;

    public static synchronized LogViewModel getInstance() {
        if (sInstance == null) {
            synchronized (LogViewModel.class) {
                sInstance = new LogViewModel();
            }
        }
        return sInstance;
    }

    /**
     * 日志类型为DMESG
     */
    public final static int LOG_TYPE_DMESG = 0;

    /**
     * 日志类型为logcat
     */
    public final static int LOG_TYPE_LOGCAT = 1;

    /**
     * 日志类型为PCAP
     */
    public final static int LOG_TYPE_PCAP = 2;


    private DmesgManager mDmesgManager;
    private PcapManager mPcapManager;
    private LogcatManager mLogcatManager;
    private LogBo logBo;
    public LogBo getLogBo(){
        return logBo;
    }
    public void setLogBo(LogBo logBo){
        this.logBo = logBo;
    }

    public LogViewModel() {
        if(logBo == null) {
            logBo = new LogBo();
        }
        if(mDmesgManager == null){
            mDmesgManager = new DmesgManager(BaseApplication.getAppContext());
            logBo.setDmesgDir(mDmesgManager.getLogDir());
            logBo.setEnableDmesg(mDmesgManager.isOpen());
        }
        if(mPcapManager == null){
            mPcapManager = new PcapManager(BaseApplication.getAppContext());
            logBo.setPcapDir(mPcapManager.getLogDir());
            logBo.setEnablePcap(mPcapManager.isOpen());
        }
        if(mLogcatManager == null){
            mLogcatManager = new LogcatManager(BaseApplication.getAppContext());
            logBo.setLogcatDir(mLogcatManager.getLogDir());
            logBo.setEnableLogcat(mLogcatManager.isOpen());
        }
    }

    /**
     * 开启日志功能
     * @param logType 日志类型
     */
    public void startLog(int logType){
        switch (logType){
            case LOG_TYPE_DMESG:
                mDmesgManager.startLogAsyn();
                break;
            case LOG_TYPE_LOGCAT:
                mLogcatManager.startLogAsyn();
                break;
            case LOG_TYPE_PCAP:
                mPcapManager.startLogAsyn();
                break;
            default:
                break;
        }
    }

    /**
     * 关闭日志功能
     * @param logType 日志类型
     */
    public void stopLog(int logType){
        switch (logType){
            case LOG_TYPE_DMESG:
                mDmesgManager.stopLogAsyn();
                break;
            case LOG_TYPE_LOGCAT:
                mLogcatManager.stopLogAsyn();
                break;
            case LOG_TYPE_PCAP:
                mPcapManager.stopLogAsyn();
                break;
            default:
                break;
        }
    }
}

