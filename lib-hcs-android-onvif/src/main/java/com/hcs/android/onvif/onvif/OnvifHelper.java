package com.hcs.android.onvif.onvif;


import android.content.Context;

import com.hcs.android.common.util.ISimpleCustomer;

public class OnvifHelper {
    public static void buildManager(Context context, String host, String username, String password, ISimpleCustomer<OnvifManager> simpleCustomer){
        OnvifManager onvifManager = new OnvifManager(host, username, password);
        onvifManager.createDevice(context,simpleCustomer);
    }
}
