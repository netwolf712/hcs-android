package com.hcs.android.common.network;

import com.hcs.android.common.util.StringUtil;

public class NetConfig {
    /**
     * 网络名称
     */
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    /**
     * 网络类型，0:未知，1:以太网，2:2G，3:3G，4:4G，5:5G
     */
    private String netType;
    public String getNetType(){
        return netType;
    }
    public void setNetType(String netType){
        this.netType = netType;
    }

    /**
     * 连接状态，对应Android中ConnectivityManager对state的定义：0:连接中，1:已连接，3:挂起，4:断开中，5:已断开，6:未知
     */
    private String linkStatus;
    public String getLinkStatus(){
        return linkStatus;
    }
    public void setLinkStatus(String linkStatus){
        this.linkStatus = linkStatus;
    }

    /**
     * MAC地址
     */
    private String hardwareAddress;
    public String getHardwareAddress(){
        return hardwareAddress;
    }
    public void setHardwareAddress(String hardwareAddress){
        this.hardwareAddress = hardwareAddress;
    }

    /**
     * IP地址
     */
    private String ipAddress;
    public String getIpAddress(){
        return ipAddress;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }
    /**
     * 子网掩码
     */
    private String mask;
    public String getMask(){
        return mask;
    }
    public void setMask(String mask){
        this.mask = mask;
    }

    /**
     * 默认网关
     */
    private String gateway;
    public String getGateway(){
        return gateway;
    }
    public void setGateway(String gateway){
        this.gateway = gateway;
    }

    /**
     * IPv6的默认网关
     */
    private String gateway6;
    public String getGateway6(){
        return gateway6;
    }
    public void setGateway6(String gateway6){
        this.gateway6 = gateway6;
    }

    /**
     * 首选DNS服务器
     */
    private String dns1;
    public String getDns1(){
        return dns1;
    }
    public void setDns1(String dns1){
        this.dns1 = dns1;
    }

    /**
     * 备选DNS服务器
     */
    private String dns2;
    public String getDns2(){
        return dns2;
    }
    public void setDns2(String dns2){
        this.dns2 = dns2;
    }

    /**
     * ipv6的地址
     */
    private String ipAddress6;
    public String getIpAddress6(){
        return ipAddress6;
    }
    public void setIpAddress6(String ipAddress6){
        this.ipAddress6 = ipAddress6;
    }

    /**
     * ipv6的掩码
     */
    private String mask6;
    public String getMask6(){
        return mask6;
    }
    public void setMask6(String mask6){
        this.mask6 = mask6;
    }

    /**
     * 带宽：:10M，2:100M，3:1000M
     */
    private String bandwidth;
    public String getBandwidth(){
        return bandwidth;
    }
    public void setBandwidth(String bandwidth){
        this.bandwidth = bandwidth;
    }

    /**
     * 连接模式：1:DHCP，2:静态，3:PPPoE，4：VPN
     */
    private String linkMode;
    public String getLinkMode(){
        return linkMode;
    }
    public void setLinkMode(String linkMode){
        this.linkMode = linkMode;
    }

    /**
     * 显示名称
     */
    private String displayName;
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    /**
     * ip link show所显示出来的每个网络对应的id
     */
    private Integer linkId;
    public Integer getLinkId(){
        return linkId;
    }
    public void setLinkId(Integer linkId){
        this.linkId = linkId;
    }


    /**
     * 比较是否有字段改变
     * @param netConfig
     * @return
     */
    public boolean compare(NetConfig netConfig)
    {
        if(!compareOthers(netConfig)) return false;
        if(!compareIPv4(netConfig)) return false;
        if(!compareGateway4(netConfig)) return false;
        if(!compareIPv6(netConfig)) return false;
        if(!compareGateway6(netConfig)) return false;
        if(!compareDNS(netConfig)) return false;
        return true;
    }

    /**
     * 比对IPv4的内容
     * @param netConfig
     * @return
     */
    public boolean compareIPv4(NetConfig netConfig){
        if(!StringUtil.compare(this.ipAddress,netConfig.getIpAddress())) return false;
        if(!StringUtil.compare(this.mask,netConfig.getMask())) return false;

        return true;
    }

    /**
     * 比对DNS的一致性
     * @param netConfig
     * @return
     */
    public boolean compareDNS(NetConfig netConfig){
        if(!StringUtil.compare(this.dns1,netConfig.getDns1())) return false;
        if(!StringUtil.compare(this.dns2,netConfig.getDns2())) return false;
        return true;
    }
    /**
     * 对比默认网关的内容
     * @param netConfig
     * @return
     */
    public boolean compareGateway4(NetConfig netConfig){
        if(!StringUtil.compare(this.gateway,netConfig.getGateway())) return false;
        return true;
    }
    /**
     * 比对IPv4的内容
     * @param netConfig
     * @return
     */
    public boolean compareIPv6(NetConfig netConfig){
        if(!StringUtil.compare(this.ipAddress6,netConfig.getIpAddress6())) return false;
        if(!StringUtil.compare(this.mask6,netConfig.getMask6())) return false;
        return true;
    }

    /**
     * 比对IPv6的默认网关
     * @param netConfig
     * @return
     */
    public boolean compareGateway6(NetConfig netConfig){
        if(!StringUtil.compare(this.gateway6,netConfig.getGateway6())) return false;
        return true;
    }
    /**
     * 比较其它变量的一致性
     * @param netConfig
     * @return
     */
    public boolean compareOthers(NetConfig netConfig){
        if(!StringUtil.compare(this.netType,netConfig.getNetType())) return false;
        if(!StringUtil.compare(this.bandwidth,netConfig.getBandwidth())) return false;
        if(!StringUtil.compare(this.hardwareAddress,netConfig.getHardwareAddress())) return false;
        if(!StringUtil.compare(this.linkStatus,netConfig.getLinkStatus())) return false;
        if(!StringUtil.compare(this.linkMode,netConfig.getLinkMode())) return false;
        if(this.linkId != null && netConfig.getLinkId() == null) return false;
        if(this.linkId == null && netConfig.getLinkId() != null) return false;
        if(this.linkId != null && netConfig.getLinkId() != null)
        {
            if(this.linkId.intValue() != netConfig.getLinkId().intValue())
                return false;
        }

        return true;
    }
    /**
     * 自定义拷贝
     * @param netConfig
     */
    public void copy(NetConfig netConfig){
        copyIPv4(netConfig);
        copyDNS(netConfig);
        copyIPv6(netConfig);
        copyOthers(netConfig);
    }

    /**
     * 拷贝IPv4内容
     * @param netConfig
     */
    public void copyIPv4(NetConfig netConfig)
    {
        this.gateway = netConfig.getGateway();
        this.ipAddress = netConfig.getIpAddress();
        this.mask = netConfig.getMask();
    }

    /**
     * 拷贝DNS内容
     * @param netConfig
     */
    public void copyDNS(NetConfig netConfig){
        this.dns1 = netConfig.getDns1();
        this.dns2 = netConfig.getDns2();
    }

    /**
     * 拷贝IPv6内容
     * @param netConfig
     */
    public void copyIPv6(NetConfig netConfig)
    {
        this.ipAddress6 = netConfig.getIpAddress6();
        this.mask6 = netConfig.getMask6();
        this.gateway6 = netConfig.getGateway6();
    }

    /**
     * 拷贝其它内容
     * @param netConfig
     */
    public void copyOthers(NetConfig netConfig)
    {
        this.netType = netConfig.getNetType();
        this.bandwidth = netConfig.getBandwidth();
        this.hardwareAddress = netConfig.getHardwareAddress();
        this.linkMode = netConfig.getLinkMode();
        this.linkStatus = netConfig.getLinkStatus();
        this.linkId = netConfig.getLinkId();
    }
}
