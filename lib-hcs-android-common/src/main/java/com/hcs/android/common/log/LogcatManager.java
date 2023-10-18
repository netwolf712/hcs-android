package com.hcs.android.common.log;

import static com.hcs.android.common.util.ExeCommand.executeSuCmd;
import static com.hcs.android.common.util.ExeCommand.executeSynSuCmd;

import android.content.Context;
import android.util.Log;

import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;

import java.io.File;
import java.util.Date;

public class LogcatManager extends AbstractLogManager{
    public LogcatManager(Context context){
        super(context);
    }

    private String getLogcatCmd(){
        return "logcat -r " + getMaxFileSize() + " -n 4 -f ";
    }


    /**
     * 开启logcat记录
     */
    @Override
    public void startLog(){

        checkFile();

        if (!isOpen()){
            String cmd = getLogcatCmd() + "\"" + getLogDir() + "/" + getFileName() + "\"" + " &";
            executeSynSuCmd(cmd);
            Log.d("LogcatManager", "startLog: start");
        }
    }

    /**
     * 获取日志类型标签
     */
    @Override
    public String getLogTag(){
        return  "logcat";
    }
    
    /**
     * 是否存在logcat
     */
    @Override
    public boolean isOpen()
    {
        String strRtn = executeSuCmd("ps -ef| grep logcat | ps | grep logcat",10000);
        return strRtn != null && strRtn.contains("logcat -r");
    }

    public void stopLog(){
        String procInfo = executeSuCmd("ps -ef | grep logcat");
        if (!procInfo.contains("logcat -r")) return;

        String killCmd = "killall -9 logcat";
        executeSuCmd(killCmd);
        Log.d("LogcatManager", "stopLog: " + killCmd);
    }

    /**
     * 获取日志文件后缀名
     */
    @Override
    public String getFileExtensionName(){
        return ".log";
    }
}
