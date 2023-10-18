package com.hcs.android.common.multicast;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.common.util.PermissionCheckUtil;
import com.hcs.android.common.util.log.KLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.List;

public class MulticastHelper {
    private static final int MAX_DATA_LEN = 100 * 1024;
    //定义接收网络数据的字符数组
    byte[] mInBuff = new byte[MAX_DATA_LEN];
    //以指定字节数组创建准备接受的DatagramPacket对象
    private DatagramPacket mInPacket = new DatagramPacket(mInBuff , mInBuff.length);
    //定义本程序的MulticastSocket实例
    private MulticastSocket mSocket = null;
    //定义广播的IP地址和端口
    private InetAddress mMulticastAddress = null;
    private int mMulticastPort;
    private IReceiveListener mReceiveListener;
    //定义一个用于发送的DatagramPacket对象
    private DatagramPacket mOutPacket = null;
    private ReadBroad mReadBroad;
    private boolean mRun = false;
    private WifiManager.MulticastLock mMulticastLock;
    public MulticastHelper(String multicastAddress, int multicastPort, IReceiveListener receiveListener){
        mReceiveListener = receiveListener;
        try {
            mMulticastAddress = InetAddress.getByName(multicastAddress);
            mMulticastPort = multicastPort;
        }catch (Exception e){
            KLog.e(e);
        }
    }

    /**
     * 开启组播监听
     */
    public void start(){
        try {
            if(mRun){
                return;
            }

            mRun = true;
            mSocket = new MulticastSocket(mMulticastPort);
            //如果有无线，则使用无线网络作为组播网络
            if(NetworkInterface.getByName("wlan0") != null){
                mSocket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
                WifiManager wifiManager = (WifiManager) BaseApplication.getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mMulticastLock = wifiManager.createMulticastLock("multicast.test");
                mMulticastLock.acquire();
            }
            //获取有效网卡地址
            //            List<NetworkInterface> addressList = NetUtil.getNetworkInterfaceList();
            //            //端口不冲突就行
            //            for (NetworkInterface networkInterface : addressList) {
            //                InetSocketAddress inetSocketAddress = new
            //                        InetSocketAddress(mMulticastAddress, mMulticastPort);
            //                //将有效网卡加入组播
            //                mSocket.joinGroup(inetSocketAddress, networkInterface);
            //            }
            //将该socket加入指定的多点广播地址
            mSocket.joinGroup(mMulticastAddress);
            //设置本MultcastSocket发送的数据报将被送到本身
            //true表示不接收自己发送的消息
            mSocket.setLoopbackMode(true);
            //初始化发送用的DatagramSocket，它包含一个长度为0的字节数组
            mOutPacket = new DatagramPacket(new byte[0], 0, mMulticastAddress, mMulticastPort);
            mReadBroad = new ReadBroad();
            mReadBroad.start();
        }catch (Exception e){
            KLog.e(e);
        }

    }

    /**
     * 关闭组播监听
     */
    public void stop(){
        try {
            mRun = false;
            if(mMulticastLock != null){
                mMulticastLock.release();
            }
            if (mReadBroad != null) {
                mReadBroad.join();
                mReadBroad = null;
            }

            if(mSocket != null){
                mSocket.leaveGroup(mMulticastAddress);
                mSocket = null;
            }
        }catch (Exception e){
            KLog.e(e);
        }
    }
    private DatagramPacket getOutPacket(){
        if(mOutPacket == null) {
            mOutPacket = new DatagramPacket(new byte[0], 0, mMulticastAddress, mMulticastPort);
        }
        return mOutPacket;
    }

    /**
     * 发送数据
     */
    public void sendData(byte[] data){
        new Thread(()->{
            try
            {
                //设置发送用的DatagramPacket里的字节数组
                DatagramPacket outPacket = getOutPacket();
                if(outPacket == null){
                    return;
                }
                outPacket.setData(data);
                outPacket.setLength(data.length);
                //发送数据
                if(mSocket != null) {
                    mSocket.send(outPacket);
                }
            }
            catch (IOException e)
            {
                KLog.e(e);
            }
        }).start();

    }
    //持续读取MulticastSocket的线程
    private class ReadBroad extends Thread
    {
        public void run()
        {
            while (mRun)
            {
                try
                {
                    //读取Socket中的数据
                    if(mSocket != null) {
                        mSocket.receive(mInPacket);
                        byte[] data = Arrays.copyOfRange(mInPacket.getData(), 0, mInPacket.getLength());
                        if (mReceiveListener != null) {
                            mReceiveListener.onReceiveData(data, mInPacket.getLength());
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 数据接收监听器
     */
    public interface IReceiveListener{
        /**
         * 接收到数据
         * @param buff 数据体
         * @param len 数据长度
         */
        void onReceiveData(byte[] buff,int len);
    }
}
