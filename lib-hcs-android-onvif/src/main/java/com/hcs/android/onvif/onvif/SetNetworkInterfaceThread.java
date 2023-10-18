package com.hcs.android.onvif.onvif;

import android.content.Context;

import com.hcs.android.onvif.onvifBean.NetworkInterface;
import com.hcs.android.onvif.onvifBean.OnvifDevice;
import com.hcs.android.onvif.util.HttpUtil;

/**
 *
 * 修改摄像头Ip
 */

public class SetNetworkInterfaceThread extends Thread {

    private OnvifDevice device;
    private Context context;
    private String interfaceToken;
    private String newIpAddress;
    private String prefixLength;
    private SetNetworkInterfaceCallBack callBack;

//    private WriteFileUtil util;

    public SetNetworkInterfaceThread(OnvifDevice device, Context context, String ipAddress, SetNetworkInterfaceCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
        this.newIpAddress = ipAddress;
    }

    @Override
    public void run() {
        super.run();
        try {
            //getCapabilities，不需要鉴权
            NetworkInterface networkInterface = device.getNetworkInterface();
            String postString = OnvifUtils.getPostString("setNetworkInterface.xml", context, device, true,
                    networkInterface.getInterfaceToken(), networkInterface.getMtu(), newIpAddress, networkInterface.getIpvtPrefixLength());
            String caps = HttpUtil.postRequest(device.getServiceUrl(), postString);
            //解析返回的xml数据获取存在的url
//            XmlDecodeUtil.getCapabilitiesUrl(caps, device);

            callBack.getDeviceInfoResult(true, device, "NO_ERROR");

//            postString = getPostString("getConfigOptions.xml", true);
//            caps = HttpUtil.postRequest(device.getPtzUrl(), postString);
//            util.writeData(caps.getBytes());

//            util.finishWrite();
        } catch (Exception e) {
            e.printStackTrace();
            callBack.getDeviceInfoResult(false,device, e.toString());
        }
    }

    /**
     * Author ： BlackHao
     * Time : 2018/1/11 14:24
     * Description : 获取 device 信息回调
     */
    public interface SetNetworkInterfaceCallBack {
        void getDeviceInfoResult(boolean isSuccess, OnvifDevice device, String errorMsg);
    }
}
