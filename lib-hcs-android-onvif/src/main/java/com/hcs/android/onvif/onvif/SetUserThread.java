package com.hcs.android.onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.hcs.android.onvif.onvifBean.MediaProfile;
import com.hcs.android.onvif.onvifBean.OnvifDevice;
import com.hcs.android.onvif.util.HttpUtil;


/**
 * 设置摄像机参数
 */
public class SetUserThread extends Thread {
    private static String tag = "OnvifSdk";

    private OnvifDevice device;
    private Context context;
    private SetUserCallBack callBack;
    private MediaProfile mediaProfile;
    private String userName;
    private String password;


//    private WriteFileUtil util;

    public SetUserThread(OnvifDevice device, Context context, String userName, String password, SetUserCallBack callBack) {
        this.device = device;
        this.context = context;
        this.userName = userName;
        this.password = password;
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
            //SetImagingSettingsThread，需要鉴权
            String postString = OnvifUtils.getPostString("setUser.xml", context, device, true, userName, password);
            String setUser = HttpUtil.postRequest(device.getServiceUrl(), postString);
            Log.v(tag, setUser);
            callBack.setUserThreadResult(true, device, "");
        } catch (Exception e) {
            e.printStackTrace();
            callBack.setUserThreadResult(false, device, e.toString());
        }
    }


    public interface SetUserCallBack {
        void setUserThreadResult(boolean isSuccess, OnvifDevice device, String errorMsg);
    }

}