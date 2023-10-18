package com.hcs.android.onvif.onvif;

import android.content.Context;

import com.hcs.android.onvif.onvifBean.MediaProfile;
import com.hcs.android.onvif.onvifBean.OnvifDevice;
import com.hcs.android.onvif.util.HttpUtil;
import com.hcs.android.onvif.util.XmlDecodeUtil;

/**
 * 获取摄像机图像参数
 */
public class GetImagingSettingsThread extends Thread {
    private static String tag = "OnvifSdk";

    private OnvifDevice device;
    private Context context;
    private GetImagingSettingsCallBack callBack;
    private MediaProfile mediaProfile;


    public GetImagingSettingsThread(OnvifDevice device, Context context, GetImagingSettingsCallBack callBack) {
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
                callBack.getImagingSettingsThreadResult(false, device, "未获取到设备token");
                return;
            }
            //SetImagingSettingsThread，需要鉴权
            String postString = OnvifUtils.getPostString("getImagingSettings.xml", context, device, true,
                    mediaProfile.getVideSource().getVideoSourceToken());
            String getImagingSettings = HttpUtil.postRequest(device.getImageUrl(), postString);
            XmlDecodeUtil.getGetImageSetting(getImagingSettings, device);
            callBack.getImagingSettingsThreadResult(true, device, getImagingSettings);
        } catch (Exception e) {
            e.printStackTrace();
            callBack.getImagingSettingsThreadResult(false, device, e.toString());
        }
    }


    public interface GetImagingSettingsCallBack {
        void getImagingSettingsThreadResult(boolean isSuccess, OnvifDevice device, String errorMsg);
    }

}