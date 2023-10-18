package com.hcs.android.common.log;

import static com.hcs.android.common.util.ExeCommand.executeSuCmd;

import android.content.Context;

public class DmesgManager extends AbstractLogManager{

    public DmesgManager(Context context){
        super(context);
    }

    /**
     * 开启dmesg记录
     */
    @Override
    public void startLog(){
        //当前存在dmesg进程，直接返回。
        String dmesgCmd = "dmesg -T -w  > ";

        checkFile();

        if (!isOpen()) {
            //生成新的文件
            executeSuCmd(dmesgCmd + "\"" +  getLogDir() + "/" + getFileName() + "\"" + "&");
        }
    }

    /**
     * 获取日志类型标签
     */
    @Override
    public String getLogTag(){
        return  "dmesg";
    }

    /**
     * 是否存在dmesg
     */
    @Override
    public boolean isOpen()
    {
        String strRtn = executeSuCmd("ps -ef| grep dmesg | ps | grep dmesg",10000);
        return strRtn != null && strRtn.contains("dmesg -T -w");
    }

    @Override
    public void stopLog(){
        executeSuCmd("dmesg -C");
        String killCmd = "killall -9 dmesg";
        executeSuCmd(killCmd);
    }

    /**
     * 获取日志文件后缀名
     */
    @Override
    public String getFileExtensionName(){
        return ".log";
    }
}
