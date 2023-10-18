package com.hcs.android.onvif.onvif;

import android.content.Context;

import com.hcs.android.onvif.onvifBean.MediaProfile;
import com.hcs.android.onvif.onvifBean.OnvifDevice;
import com.hcs.android.onvif.util.HttpUtil;
import com.hcs.android.onvif.util.XmlDecodeUtil;


/**
 * 获取摄像机图像参数
 */
public class GeNetWorkInterfaceThread extends Thread {
    private static String tag = "OnvifSdk";

    private OnvifDevice device;
    private Context context;
    private GetNetWorkInterfaceCallBack callBack;
    private MediaProfile mediaProfile;


    public GeNetWorkInterfaceThread(OnvifDevice device, Context context, GetNetWorkInterfaceCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
        if (device.getProfiles() != null && device.getProfiles().size() > 0) {
            this.mediaProfile = device.getProfiles().iterator().next();
        }
//        util = new WriteFileUtil("onvif.txt");
    }



    @Override
    public void run() {
        super.run();
        try {
            if (mediaProfile == null || mediaProfile.getVideSource() == null) {
                callBack.getNetWorkInterfaceThreadResult(false, device, "未获取到设备token");
                return;
            }
            //SetImagingSettingsThread，需要鉴权
            String getNetworkInterface =  OnvifUtils.getPostString("getNetworkInterface.xml", context, device, true);
            String getNetworkInterfaceReturn = HttpUtil.postRequest(device.getServiceUrl(), getNetworkInterface);
            XmlDecodeUtil.getNetworkInterface(getNetworkInterfaceReturn, device);
            callBack.getNetWorkInterfaceThreadResult(true, device, getNetworkInterface);
        } catch (Exception e) {
            e.printStackTrace();
            callBack.getNetWorkInterfaceThreadResult(false, device, e.toString());
        }
    }


    public interface GetNetWorkInterfaceCallBack {
        void getNetWorkInterfaceThreadResult(boolean isSuccess, OnvifDevice device, String errorMsg);
    }

}