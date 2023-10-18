package com.hcs.android.business;


import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;

import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.request.model.RetrofitHelper;
import com.hcs.android.call.api.LinphoneManager;
import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.android.call.receivers.BluetoothManager;
import com.hcs.android.call.view.LinphoneGL2JNIViewOverlay;
import com.hcs.android.call.view.LinphoneOverlay;
import com.hcs.android.call.view.LinphoneTextureViewOverlay;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.GlobalState;
import org.linphone.core.LogCollectionState;
import org.linphone.core.LogLevel;
import org.linphone.core.LoggingService;
import org.linphone.core.LoggingServiceListener;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;

import java.util.ArrayList;


public final class DeviceService extends Service {

    private static DeviceService sInstance;
    public static DeviceService instance() {
        if (isReady()) return sInstance;

        throw new RuntimeException("DeviceService not instantiated yet");
    }

    public final Handler handler = new Handler();


    public static boolean isReady() {
        return sInstance != null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(sInstance != null){
            //如果已经启动过了，则不需要重复启动
            return START_STICKY;
        }
        sInstance = this;
        return START_STICKY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public synchronized void onDestroy() {

    }


    /**
     * 驱动服务
     */
    public static void startService(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("command", 1); //COMMAND_CHECK_LOCAL_UPDATING
        intent.putExtra("delay", 5000);
        ComponentName cn = new ComponentName("com.hcs.android.business",
                "com.hcs.android.business.DeviceService");
        intent.setComponent(cn);
        context.startService(intent);
    }
}
