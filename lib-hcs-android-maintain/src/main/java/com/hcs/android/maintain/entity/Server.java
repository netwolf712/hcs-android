package com.hcs.android.maintain.entity;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.network.NetConfig;
import com.hcs.android.common.network.NetworkManager;
import com.hcs.android.common.util.Arith;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.maintain.entity.server.Cpu;
import com.hcs.android.maintain.entity.server.Jvm;
import com.hcs.android.maintain.entity.server.Mem;
import com.hcs.android.maintain.entity.server.Sys;
import com.hcs.android.maintain.entity.server.SysFile;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 服务器相关信息
 * 
 * @author ruoyi
 */
public class Server
{
    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem();

    /**
     * JVM相关信息
     */
    private Jvm jvm = new Jvm();

    /**
     * 服务器相关信息
     */
    private Sys sys = new Sys();

    /**
     * 磁盘相关信息
     */
    private List<SysFile> sysFiles = new LinkedList<SysFile>();

    public Cpu getCpu()
    {
        return cpu;
    }

    public void setCpu(Cpu cpu)
    {
        this.cpu = cpu;
    }

    public Mem getMem()
    {
        return mem;
    }

    public void setMem(Mem mem)
    {
        this.mem = mem;
    }

    public Jvm getJvm()
    {
        return jvm;
    }

    public void setJvm(Jvm jvm)
    {
        this.jvm = jvm;
    }

    public Sys getSys()
    {
        return sys;
    }

    public void setSys(Sys sys)
    {
        this.sys = sys;
    }

    public List<SysFile> getSysFiles()
    {
        return sysFiles;
    }

    public void setSysFiles(List<SysFile> sysFiles)
    {
        this.sysFiles = sysFiles;
    }

    public void copyTo()
    {

        getInfoWithTop();

        setSysInfo();

        setJvmInfo();

        setSysFiles();
    }


    /**
     * 设置服务器信息
     */
    @SuppressLint("HardwareIds")
    private void setSysInfo()
    {
        Properties props = System.getProperties();
        NetworkManager networkManager = new NetworkManager();
        NetConfig netConfig = networkManager.getNetConfig(BaseApplication.getAppContext());
        sys.setComputerName(android.os.Build.SERIAL);
        sys.setComputerIp(netConfig.getIpAddress());
        sys.setOsName("Android");
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置Java虚拟机
     */
    private void setJvmInfo()
    {
        Properties props = System.getProperties();
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles()
    {
        getInternalMemoryInfo();
        getExternalMemoryInfo();
    }

    /**
     * 字节转换
     * 
     * @param size 字节大小
     * @return 转换后值
     */
    @SuppressLint("DefaultLocale")
    public String convertFileSize(long size)
    {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb)
        {
            return String.format("%.1f GB", (float) size / gb);
        }
        else if (size >= mb)
        {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        }
        else if (size >= kb)
        {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        }
        else
        {
            return String.format("%d B", size);
        }
    }

    /**
     * 解析内存信息
     * @param str "Mem:    978600k total,   514012k used,   464588k free,     2156k buffers"
     */
    private void analyzeMem(String str){
        str = str.substring(4);
        List<Object> objList = StringUtil.CutStringWithChar(str,',');
        for(Object obj : objList){
            String tmp = obj.toString();
            tmp = tmp.replaceAll(" ","");
            if(tmp.contains("ktotal")){
                mem.setTotal(Long.parseLong(tmp.replaceAll("ktotal","")) * 1024);
            }else if(tmp.contains("kused")){
                mem.setUsed(Long.parseLong(tmp.replaceAll("kused","")) * 1024);
            }else if(tmp.contains("kfree")){
                mem.setFree(Long.parseLong(tmp.replaceAll("kfree","")) * 1024);
            }
        }
    }

    /**
     * 解析CPU信息
     * @param str "400%cpu  15%user   0%nice  18%sys 367%idle   0%iow   0%irq   0%sirq   0%host "
     */
    private void analyzeCPU(String str){
        List<Object> objectList = StringUtil.CutStringWithChar(str,' ');
        for(Object obj : objectList){
            String tmp = obj.toString();
            tmp = tmp.replaceAll(" ","");
            if(tmp.contains("cpu")){
                cpu.setCpuNum(Integer.parseInt(tmp.replace("00%cpu","")));
            }else if(tmp.contains("user")){
                cpu.setUsed(Double.parseDouble(tmp.replace("%user",""))/100);
            }else if(tmp.contains("sys")){
                cpu.setSys(Double.parseDouble(tmp.replace("%sys",""))/100);
            }else if(tmp.contains("idle")){
                cpu.setFree(Double.parseDouble(tmp.replace("%idle",""))/100);
            }
        }
    }

    /**
     * 使用top命令获取cpu和内存信息
     */
    private void getInfoWithTop(){
        String result = ExeCommand.executeSuCmd("top -n 1");
        //解析内存数据
        List<Object> objList = StringUtil.CutStringWithChar(result,'\n');
        if(objList != null && objList.size() > 4){
            //解析内存
            analyzeMem(objList.get(1).toString());
            analyzeCPU(objList.get(3).toString());
        }
    }


    /**
     * 获取内部存储信息
     */

    public void getInternalMemoryInfo() {

        File path = Environment.getDataDirectory();

        StatFs fs = new StatFs(path.getPath());
        SysFile sysFile = new SysFile();
        sysFile.setDirName(path.getName());
        sysFile.setTypeName("total");
        sysFile.setTotal(convertFileSize(fs.getTotalBytes()));
        sysFile.setFree(convertFileSize(fs.getFreeBytes()));
        sysFile.setUsed(convertFileSize(fs.getTotalBytes() - fs.getFreeBytes()));
        sysFile.setUsage(Arith.mul(Arith.div(fs.getTotalBytes() - fs.getFreeBytes(), fs.getTotalBytes(), 4), 100));
        sysFiles.add(sysFile);

    }

    /**
     * 获取外部存储信息
     */

    public void getExternalMemoryInfo() {

        if (externalMemoryAvailable()) {

            File path = Environment.getExternalStorageDirectory();

            StatFs fs = new StatFs(path.getPath());

            SysFile sysFile = new SysFile();
            sysFile.setDirName(path.getName());
            sysFile.setTypeName("external");
            sysFile.setTotal(convertFileSize(fs.getTotalBytes()));
            sysFile.setFree(convertFileSize(fs.getFreeBytes()));
            sysFile.setUsed(convertFileSize(fs.getTotalBytes() - fs.getFreeBytes()));
            sysFile.setUsage(Arith.mul(Arith.div(fs.getTotalBytes() - fs.getFreeBytes(), fs.getTotalBytes(), 4), 100));
            sysFiles.add(sysFile);

        }
    }


    /**
     * SDCARD是否存在
     */

    public boolean externalMemoryAvailable() {

        return android.os.Environment.getExternalStorageState().equals(

                android.os.Environment.MEDIA_MOUNTED);

    }

}
