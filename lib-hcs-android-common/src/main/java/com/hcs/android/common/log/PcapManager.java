package com.hcs.android.common.log;


import static com.hcs.android.common.util.ExeCommand.executeSuCmd;

import android.content.Context;

import com.hcs.android.common.util.log.KLog;

public class PcapManager extends AbstractLogManager{

    public PcapManager(Context context){
        super(context);
    }


    /**
     * 获取日志类型标签
     */
    @Override
    public String getLogTag(){
        return  "pcap";
    }

    /**
     * 是否存在抓包程序
     */
    @Override
    public boolean isOpen()
    {
        String strRtn = executeSuCmd("ps -ef| grep tcpdump | ps | grep tcpdump",10000);
        return strRtn != null && strRtn.contains("tcpdump -i");
    }

    /**
     * 开始抓包
     */
    @Override
    public void startLog()
    {
        if (getMaxKeepTime() == 0)
        {
            KLog.i("pcapKeepTime 0.");
            return;
        }
        if (isOpen())
        {
            KLog.w("Pcap Already Start!");
            return;
        }

        checkFile();

        String name = getLogDir() +  "/" + getFileName();

        executeSuCmd("tcpdump -i any -s0 host ! 127.0.0.1 -G "+ getMaxKeepTime() +" -W 1 -w " + name +" &",10000);
        KLog.i("startPcap pcap name :" + name);
    }


    /**
     * 停止抓包
     */
    @Override
    public void stopLog()
    {
        if (!isOpen())
        {
            KLog.w("Pcap Not Start!");
            return;
        }
        executeSuCmd("pkill tcpdump -i",10000);
    }

    /**
     * 获取日志文件后缀名
     */
    @Override
    public String getFileExtensionName(){
        return ".cap";
    }
}