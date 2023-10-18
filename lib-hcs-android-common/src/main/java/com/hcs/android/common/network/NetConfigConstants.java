package com.hcs.android.common.network;

public class NetConfigConstants {

    /**
     * 网络名称
     */
    /**
     * 4G网卡
     */
    public static final String NAME_HWSIM0 = "hwsim0" ;

    /**
     * 以太网
     */
    public static final String NAME_ETH0 = "eth0" ;

    /**
     * WIFI
     */
    public static final String NAME_WIFI = "wlan0" ;

    /**
     * 网络类型
     */
    /**
     * 移动网络-GPRS
     */
    public static final String TYPE_UNKNONW = "0" ;

    /**
     * 以太网
     */
    public static final String TYPE_ETHERNET = "1" ;


    /**
     * WIFI模块
     */
    public static final String TYPE_WIFI = "2" ;

    /**
     * 链接模式
     */

    /**
     * DHCP
     */
    public static final String LINK_MODE_DHCP_NAME = "DHCP" ;
    public static final String LINK_MODE_DHCP = "1" ;

    /**
     * 静态
     */
    public static final String LINK_MODE_STATIC_NAME = "STATIC";
    public static final String LINK_MODE_STATIC = "2" ;


    /**
     * PPPoE
     */
    public static final String LINK_MODE_PPPoE_NAME = "PPPoE";
    public static final String LINK_MODE_PPPoE = "3" ;

    /**
     * VPN
     */
    public static final String LINK_MODE_VPN_NAME = "VPN";
    public static final String LINK_MODE_VPN = "4" ;

    /**
     * 链接状态
     */

    /**
     * 连接中
     */
    public static final String LINK_STATUS_CONNECTING = "0" ;

    /**
     * 已连接
     */
    public static final String LINK_STATUS_CONNECTED = "1" ;


    /**
     * 挂起
     */
    public static final String LINK_STATUS_SUSPENDED = "3" ;

    /**
     * 断开中
     */
    public static final String LINK_STATUS_DISCONNECTING = "4" ;

    /**
     * 已断开
     */
    public static final String LINK_STATUS_DISCONNECTED = "5" ;

    /**
     * 未知
     */
    public static final String LINK_STATUS_UNKNOWN = "7" ;

    /**
     * IP版本
     */
    /**
     * 移动网络-GPRS
     */
    public static final String VERSION_IPv4 = "1" ;

    /**
     * WIFI
     */
    public static final String VERSION_IPv6 = "2" ;

    /**
     * 全局配置字符串
     */
    /**
     * 链接模式
     */
    public static final String SETTINGS_ETHERNET_LINK_MODE = "ethernet_link_mode";
    /**
     * 静态IP地址
     */
    public static final String SETTINGS_ETHERNET_STATIC_IP = "ethernet_static_ip";
    /**
     * 静态IP子网掩码
     */
    public static final String SETTINGS_ETHERNET_STATIC_MASK = "ethernet_static_mask";
    /**
     * 静态IP网关
     */
    public static final String SETTINGS_ETHERNET_STATIC_GATEWAY = "ethernet_static_gateway";
    /**
     * 静态IP DNS1
     */
    public static final String SETTINGS_ETHERNET_STATIC_DNS1 = "ethernet_static_dns1";
    /**
     * 静态IP DNS2
     */
    public static final String SETTINGS_ETHERNET_STATIC_DNS2 = "ethernet_static_dns2";

    /**
     * 网络名称
     */
    public static final String SETTINGS_ETHERNET_NAME = "ethernet_name";

    /**
     * 默认的网卡名称
     */
    public static final String DEFAULT_ETHERNET_NAME = "eth0";
}
