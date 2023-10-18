package com.hcs.android.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.log.KLog;

import static com.hcs.android.common.util.NetUtil.NetType.ETHERNET;
import static com.hcs.android.common.util.NetUtil.NetType.NET_4G;
import static com.hcs.android.common.util.NetUtil.NetType.NO_NET;
import static com.hcs.android.common.util.NetUtil.NetType.WIFI;

import androidx.annotation.NonNull;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 网络操作通用类库
 */
public class NetUtil {

    /**
     * 环回地址
     */
    private final static String LOOP_IP = "127.0.0.1";

    public static boolean checkNet() {
        Context context = BaseApplication.getInstance();
        //此处待修改，还有有线网络
        //return isWifiConnection(context) || isStationConnection(context);
        if(isNetWorkState(context) != NO_NET) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean checkNetToast() {
        boolean isNet = checkNet();
        if (!isNet) {
            ToastUtil.showToast("网络不给力哦！");
        }
        return isNet;
    }

    /**
     * 是否使用基站联网
     *
     * @param context
     * @return
     */
    public static boolean isStationConnection(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            return networkInfo.isAvailable() && networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 是否使用WIFI联网
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnection(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            return networkInfo.isAvailable() && networkInfo.isConnected();
        }
        return false;
    }
    public static NetType isNetWorkState(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // Logger.v(TAG, "当前WiFi连接可用 ");
                    return WIFI;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // Logger.v(TAG, "当前移动网络连接可用 ");
                    return NET_4G;
                }else if (activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    // Logger.v(TAG, "当前移动网络连接可用 ");
                    return ETHERNET;
                }
            } else {
                // Logger.v(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                return NO_NET;
            }
        } else {
            // Logger.v(TAG, "当前没有网络连接，请确保你已经打开网络 ");
            return NO_NET;
        }
        return NO_NET;
    }
    public enum NetType{WIFI,NET_4G,ETHERNET,NO_NET};

    /**
     * 将int类型的ip地址转为string类型
     */
    public static String int2Ip(int intIp){
        byte[] bytes = ByteStringUtils.int2byte(intIp);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++){
            sb.append(bytes[i] & 0xFF);
            if(i < 3){
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * 将string类型的ip转为int类型
     */
    public static int Ip2Int(String strIp){
        String[] s = strIp.split("\\.");
        if(s.length != 4){
            return 0;
        }
        byte[] bytes = new byte[s.length];
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = (byte)Integer.parseInt(s[i]);
        }
        return ByteStringUtils.byte2Int(bytes);

    }
    public static byte[] Ip2Byte(String strIp){
        String[] s = strIp.split("\\.");
        if(s.length != 4){
            return null;
        }
        byte[] bytes = new byte[s.length];
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = (byte)Integer.parseInt(s[i]);
        }
        return bytes;
    }

    /**
     * 获取本机有效所有网卡地址
     * (IPv4)
     * @return  List<NetworkInterface> ip列表
     * @throws
     */
    public static List<NetworkInterface> getNetworkInterfaces() throws Exception {
        List<NetworkInterface> localIPlist = new ArrayList<>();
        Enumeration<NetworkInterface> interfs =
                NetworkInterface.getNetworkInterfaces();
        if (interfs == null) {
            return null;
        }
        while (interfs.hasMoreElements()) {
            NetworkInterface interf = interfs.nextElement();
            Enumeration<InetAddress> addres = interf.getInetAddresses();
            while (addres.hasMoreElements()) {
                InetAddress in = addres.nextElement();
                if (in instanceof Inet4Address) {
                    if (!LOOP_IP.equals(in.getHostAddress())){
                        localIPlist.add(interf);
                    }
                }
            }
        }
        return localIPlist;
    }

    public static List<NetworkInterface> getNetworkInterfaceList() throws Exception {
        List<NetworkInterface> localIPlist = new ArrayList<>();
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        localIPlist.add(intf);
                        break;
                    }

            }
        }
        return localIPlist;
    }

    /**
     * Get local Ip address.
     */
    public static InetAddress getLocalIPAddress() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                NetworkInterface nif = enumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                if (inetAddresses != null) {
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            return inetAddress;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Ipv4 address check.
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * Check if valid IPV4 address.
     *
     * @param input the address string to check for validity.
     *
     * @return True if the input parameter is a valid IPv4 address.
     */
    public static boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * 将IP地址、MAC地址转换成字符串形式
     * @param bAddress
     * @param dot
     * @return
     */
    public static String convertByteToIPStr(byte[] bAddress,String dot){
        String strAddress = "";
        for(byte b : bAddress){
            if(strAddress.length() > 0)
                strAddress += dot;
            if(":".equals(dot)) {
                strAddress += String.format("%02x", b);
            }else{
                strAddress += String.format("%d", b & 0xFFL);
            }
        }

        return strAddress;
    }
}