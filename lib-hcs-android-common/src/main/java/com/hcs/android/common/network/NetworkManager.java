package com.hcs.android.common.network;

import static com.hcs.android.common.util.ExeCommand.executeSuCmd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.EthernetUtil;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.common.util.PermissionCheckUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Objects;

public class NetworkManager {

    /**
     * 获取当前活动状态的网卡的网络配置
     * 这个存在bug，假如网络配置异常导致网卡不可用时会导致获取不到网卡，然后网卡无法配置就一直是坏的状态
     * 所有这个接口不好用
     * 建议直接通过设备名称调用getAddressInfo方法获取
     * 当前加了个默认网卡名称，会要一点
     */
    public NetConfig getNetConfig(Context context){
        NetworkInfo networkInfo = getConnectedNetworkInfo(BaseApplication.getAppContext());
        if(networkInfo != null) {
            String devName = NetConfigConstants.NAME_ETH0;
            if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                devName = NetConfigConstants.NAME_WIFI;
            }
            return getAddressInfo(context,devName);
        }else{
            return getAddressInfo(context,SettingsHelper.getInstance(context).getString(NetConfigConstants.SETTINGS_ETHERNET_NAME,NetConfigConstants.DEFAULT_ETHERNET_NAME));
        }
    }


    /**
     * 获取当前网络连接的类型信息
     * 原生
     *
     * @param context
     * @return
     */
    public NetworkInfo getConnectedNetworkInfo(Context context) {
        if (context != null) {
            //获取手机所有连接管理对象
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取NetworkInfo对象
            NetworkInfo networkInfo = Objects.requireNonNull(manager).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                //返回NetworkInfo的类型
                return networkInfo;
            }
        }
        return null;
    }
    /**
     * 获取设备地址信息
     * @param devName
     * @return
     */
    public NetConfig getAddressInfo(Context context,String devName) {
        try {
            NetConfig netConfig = new NetConfig();
            netConfig.setName(devName);
            netConfig.setLinkMode(NetConfigConstants.LINK_MODE_DHCP);
            //获取IP地址、网关、子网掩码
            String cmd = String.format("ifconfig %s",devName);
            ExeCommand exeCommand = new ExeCommand();
            String strRtn = exeCommand.run(cmd,10000).getResult();
            KLog.i("网络信息" + strRtn);

            if(!StringUtil.isEmpty(strRtn)){
                //按照换行切割，进行大类划分
                List<Object> objList = StringUtil.CutStringWithChar(strRtn,'\n');
                if(objList != null && objList.size() > 0)
                {
                    //按照大类进行分类获取
                    for(Object object : objList){
                        String strLine = String.valueOf(object);
                        //是IP地址
                        if(strLine.indexOf("inet addr:") >= 0){
                            //如果IP地址已经获取到，则不重新获取（只获取第一个IP地址）
                            if(!StringUtil.isEmpty(netConfig.getIpAddress())) continue;
                            //按照空格将IP地址段切分成多个属性
                            List<Object> propList = StringUtil.CutStringWithChar(strLine,' ');
                            if(propList != null && propList.size() > 0){
                                for(Object prop : propList){
                                    //根据:继续切割
                                    List<Object> ipInfos = StringUtil.CutStringWithChar(String.valueOf(prop),':');
                                    if(ipInfos != null && ipInfos.size() >= 2){
                                        String tmp = (String)ipInfos.get(0);
                                        //IP地址
                                        if(tmp.equalsIgnoreCase("addr")){
                                            netConfig.setIpAddress((String)ipInfos.get(1));
                                        }else if(tmp.equalsIgnoreCase("mask")){
                                            //子网掩码
                                            netConfig.setMask((String)ipInfos.get(1));
                                        }
                                    }
                                }
                            }
                        }else if(strLine.indexOf("HWaddr") >= 0){
                            //是MAC地址
                            Integer startIndex = strLine.indexOf("HWaddr") + 7;
                            String tmp = strLine.substring(startIndex,startIndex + 17);
                            netConfig.setHardwareAddress(tmp);

                            //还可以获取到网络类型
                            //以太网
                            if(strLine.indexOf("Ethernet") >= 0){
                                netConfig.setNetType(NetConfigConstants.TYPE_ETHERNET);
                            }else{
                                //WIFI模块
                            }
                        }else if(strLine.indexOf("Scope: Link") >= 0){
                            //是IPV6，示例：inet6 addr: fe80::9822:36ff:fec3:7670/64 Scope: Link
                            //按照空格将IP地址段切分成多个属性
                            List<Object> propList = StringUtil.CutStringWithChar(strLine,' ');
                            if(propList != null && propList.size() >= 5){
                                String tmp = (String)propList.get(2);//按照如上示例，地址所在索引为2
                                //区分地址与掩码
                                List<Object> ipInfos = StringUtil.CutStringWithChar(tmp,'/');
                                if(ipInfos != null && ipInfos.size() >= 2){
                                    netConfig.setIpAddress6((String)ipInfos.get(0));
                                    netConfig.setMask6((String)ipInfos.get(1));
                                }
                            }
                        }else if(strLine.indexOf("Metric:") >= 0){
                            //只有以太网才通过这种方法判断
                            {
                                //是网络连接状况
                                if (strLine.indexOf("RUNNING") >= 0) {
                                    //如果状态是running，说明是接线的
                                    netConfig.setLinkStatus(NetConfigConstants.LINK_STATUS_CONNECTED);
                                } else {
                                    //否则认为是断开的
                                    netConfig.setLinkStatus(NetConfigConstants.LINK_STATUS_DISCONNECTED);
                                }
                            }
                        }

                    }
                }
            }

            getGateway(netConfig);
            netConfig.setDns1(getDNS(devName,1));
            netConfig.setDns2(getDNS(devName,2));
            netConfig.setLinkId(getLinkId(devName));

            String linkMode = SettingsHelper.getInstance(context).getString(NetConfigConstants.SETTINGS_ETHERNET_LINK_MODE,"");
            netConfig.setLinkMode(linkMode);

            return netConfig;
        } catch (Exception e) {
            KLog.e(e);
        }
        return null;
    }

    /**
     * 获取对应网络设备的默认网关地址
     * @param netConfig
     * @return
     */
    public void getGateway(NetConfig netConfig){
        try{
            String cmd = "ip route list table 0 | grep " + netConfig.getName();
            ExeCommand exeCommand = new ExeCommand();
            String strRtn = exeCommand.run(cmd,10000).getResult();
            if(!StringUtil.isEmpty(strRtn)){
                List<Object> objectList = StringUtil.CutStringWithChar(strRtn,'\n');
                if(objectList != null && objectList.size() > 0){
                    for(Object object : objectList){
                        String tmp = (String)object;
                        //找到默认网关
                        if(tmp.indexOf("default via") >= 0){
                            //进一步确认是IPv4的还是IPv6的网关
                            if(tmp.indexOf("proto static") >= 0){//有此字段的为IPv4的默认网关
                                //按照空格切割
                                List<Object> propList = StringUtil.CutStringWithChar(tmp,' ');
                                netConfig.setGateway((String) Objects.requireNonNull(propList).get(2));
                                break;
                            }else{  //否则为IPv6的
                                List<Object> propList = StringUtil.CutStringWithChar(tmp,' ');
                                netConfig.setGateway6((String) Objects.requireNonNull(propList).get(2));
                            }
                        }
                        else
                        {
                            if(tmp.indexOf("proto static") >= 0){
                                netConfig.setGateway("0.0.0.0");
                            }
                            else
                            {
                                // netConfig.setGateway6();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            KLog.e(e);
        }
    }

    /**
     * 获取scope id
     * @param devName
     * @return
     */
    public Integer getLinkId(String devName)
    {
        try{
            /**
             * 命令返回示例
             * 5: hwsim0: <BROADCAST,MULTICAST> mtu 1500 qdisc noop state DOWN mode DEFAULT group default
             *     link/ieee802.11/radiotap 12:00:00:00:00:00 brd ff:ff:ff:ff:ff:ff
             */
            String cmd = "ip link show | grep " + devName;
            String strRtn = executeSuCmd(cmd,10000);
            if(!StringUtil.isEmpty(strRtn)){
                String linkId = strRtn.substring(0,strRtn.indexOf(":"));
                return Integer.valueOf(linkId);
            }
        }catch (Exception e){
            KLog.e(e);
        }

        return 0;
    }

    /**
     * 获取DNS地址
     * @param index
     * @return
     */
    public String getDNS(String devName,Integer index){
        try {
            String cmd = String.format("getprop net.%s.dns%d",devName,index);
            ExeCommand exeCommand = new ExeCommand();
            return exeCommand.run(cmd,10000).getResult();
        }catch (Exception e){
            KLog.e(e);
        }
        return "";
    }
    /**
     * 更新网络配置
     */
    public void updateNetOption(Context context,NetConfig netConfig){
        try {
            NetConfig tmp = getAddressInfo(context,netConfig.getName());

            //如果为静态IP，需要判断设定地址与当前地址是否一致，若不一致则需要更新
            if (!tmp.compareIPv4(netConfig)) {
                String cmd = String.format("ifconfig %s %s netmask %s", netConfig.getName(), netConfig.getIpAddress(), netConfig.getMask());
                executeSuCmd(cmd,10000);
            }
            //默认网关是否有改变
            if (!tmp.compareGateway4(netConfig)) {
                String cmd = String.format("ip route add default via %s dev %s proto static", netConfig.getGateway(), netConfig.getName());
                executeSuCmd(cmd,10000);
            }

            //修改IPv6的地址和默认网关
            if (!tmp.compareIPv6(netConfig)) {
                String cmd = String.format("ifconfig %s inet6 add %s/%s", netConfig.getName(), netConfig.getIpAddress6(), netConfig.getMask6());
                executeSuCmd(cmd,10000);
            }

            if (!tmp.compareGateway6(netConfig)) {
                String cmd = String.format("ip route add default via %s dev %s", netConfig.getGateway6(), netConfig.getName());
                executeSuCmd(cmd,10000);
            }

            //修改DNS
            if (!StringUtil.compare(tmp.getDns1(), netConfig.getDns1())) {
                String cmd = String.format("setprop net.%s.dns1 %s", netConfig.getName(), netConfig.getDns1() == null ? "" : netConfig.getDns1());
                executeSuCmd(cmd,10000);
            }
            if (!StringUtil.compare(tmp.getDns2(), netConfig.getDns2())) {
                String cmd = String.format("setprop net.%s.dns2 %s", netConfig.getName(), netConfig.getDns2() == null ? "" : netConfig.getDns2());
                executeSuCmd(cmd,10000);
            }



        }catch (Exception e){
            KLog.e(e);
        }
    }

    /**
     * 保存配置
     */
    public void saveConfig(Activity activity,NetConfig netConfig){
        PermissionCheckUtil.checkChangeNetworkPermission(activity,accept -> {
            saveConfig(activity.getApplicationContext(),netConfig);
        });

    }

    public void saveConfig(Context context,NetConfig netConfig){
        new Thread(()->{
            Looper.prepare();
            if (NetConfigConstants.LINK_MODE_STATIC.equals(netConfig.getLinkMode())) {
                EthernetUtil.setEthernetStaticIp(context
                        ,netConfig.getIpAddress()
                        ,netConfig.getMask()
                        ,netConfig.getGateway()
                        ,StringUtil.isEmpty(netConfig.getDns1()) ? "8.8.8.8" : netConfig.getDns1());
                //实际测试发现需要再次配置才生效
                //可能是setEthernetStaticIp功能还存在bug
                //ps: 经测试发现dns不能为空，否则就出现设置不生效的问题
                //但是还是继续保留吧
                updateNetOption(context,netConfig);
            } else {
                EthernetUtil.setDynamicIp(context);
            }
            Looper.loop();
        }).start();
    }
}
