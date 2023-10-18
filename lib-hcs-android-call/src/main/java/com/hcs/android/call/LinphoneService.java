package com.hcs.android.call;

/*
LinphoneService.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

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

import com.hcs.android.call.api.LinphoneManager;
import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.android.call.receivers.BluetoothManager;
import com.hcs.android.call.view.LinphoneGL2JNIViewOverlay;
import com.hcs.android.call.view.LinphoneOverlay;
import com.hcs.android.call.view.LinphoneTextureViewOverlay;
import com.hcs.android.common.util.DateUtil;
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
import java.util.Date;

/**
 * Linphone service, reacting to Incoming calls, ...<br>
 *
 * <p>Roles include:
 *
 * <ul>
 *   <li>Initializing LinphoneManager
 *   <li>Starting C libLinphone through LinphoneManager
 *   <li>Reacting to LinphoneManager state changes
 *   <li>Delegating GUI state change actions to GUI listener
 */
public final class LinphoneService extends Service {
    /* Listener needs to be implemented in the Service as it calls
     * setLatestEventInfo and startActivity() which needs a context.
     */
    private static final String START_LINPHONE_LOGS = " ==== Phone information dump ====";

    private static LinphoneService sInstance;

    public final Handler handler = new Handler();

    private boolean mTestDelayElapsed = true;
    private CoreListenerStub mListener;
    private WindowManager mWindowManager;
    private LinphoneOverlay mOverlay;
    private Application.ActivityLifecycleCallbacks mActivityCallbacks;
    //呼入时弹出的activity
    private Activity mIncomingReceivedActivity;
    private LoggingServiceListener mJavaLoggingService =
            new LoggingServiceListener() {
                @Override
                public void onLogMessageWritten(
                        LoggingService logService, String domain, LogLevel lev, String message) {
                    switch (lev) {
                        case Debug:
                            //android.util.Log.d(domain, message);
                            KLog.d(domain, DateUtil.formatDate(new Date(System.currentTimeMillis()), DateUtil.FormatType.yyyyMMddHHmmss) + " " + message);
                            break;
                        case Message:
                            //android.util.Log.i(domain, message);
                            KLog.i(domain,DateUtil.formatDate(new Date(System.currentTimeMillis()), DateUtil.FormatType.yyyyMMddHHmmss) + " "  + message);
                            break;
                        case Warning:
                            //android.util.Log.w(domain, message);
                            KLog.w(domain,DateUtil.formatDate(new Date(System.currentTimeMillis()), DateUtil.FormatType.yyyyMMddHHmmss) + " "  + message);
                            break;
                        case Error:
                            //android.util.Log.e(domain, message);
                            //break;
                        case Fatal:
                        default:
                            //android.util.Log.wtf(domain, message);
                            KLog.e(domain,DateUtil.formatDate(new Date(System.currentTimeMillis()), DateUtil.FormatType.yyyyMMddHHmmss) + " "  + message);
                            break;
                    }
                }
            };

    public LoggingServiceListener getJavaLoggingService() {
        return mJavaLoggingService;
    }

    public static boolean isReady() {
        return sInstance != null && sInstance.mTestDelayElapsed;
    }

    public static LinphoneService instance() {
        if (isReady()) return sInstance;

        throw new RuntimeException("LinphoneService not instantiated yet");
    }

    private void onBackgroundMode() {
        Log.i("[Service] App has entered background mode");
        if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
            LinphoneManager.getLcIfManagerNotDestroyedOrNull().enterBackground();
        }
    }

    private void onForegroundMode() {
        Log.i("[Service] App has left background mode");
        if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
            LinphoneManager.getLcIfManagerNotDestroyedOrNull().enterForeground();
        }
    }

    private void setupActivityMonitor() {
        if (mActivityCallbacks != null) return;
        getApplication()
                .registerActivityLifecycleCallbacks(mActivityCallbacks = new ActivityMonitor());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        boolean isPush = false;
        if (intent != null && intent.getBooleanExtra("PushNotification", false)) {
            Log.i("[Service] [Push Notification] LinphoneService started because of a push");
            isPush = true;
        }

        if (sInstance != null) {
            Log.w("[Service] Attempt to start the LinphoneService but it is already running !");
            return START_STICKY;
        }

        LinphoneManager.createAndStart(this, isPush);

        sInstance = this; // sInstance is ready once linphone manager has been created
//        LinphoneManager.getLc()
//                .addListener(
//                        mListener =
//                                new CoreListenerStub() {
//                                    @Override
//                                    public void onCallStateChanged(
//                                            Core lc, Call call, State state, String message) {
//                                        if (sInstance == null) {
//                                            Log.i(
//                                                    "[Service] Service not ready, discarding call state change to ",
//                                                    state.toString());
//                                            return;
//                                        }
//
//                                        if (state == State.IncomingReceived
//                                                || state == State.IncomingEarlyMedia) {
//                                            if (!LinphoneManager.getInstance().getCallGsmON())
//                                                onIncomingReceived();
//                                        }
//
//                                        if (state == State.End
//                                                || state == State.Released
//                                                || state == State.Error) {
//                                            destroyOverlay();
//                                        }
//
//                                        if (state == State.Released
//                                                && call.getCallLog().getStatus()
//                                                        == Call.Status.Missed) {
//
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onGlobalStateChanged(
//                                            Core lc, GlobalState state, String message) {
//                                    }
//
//                                    @Override
//                                    public void onRegistrationStateChanged(
//                                            Core lc,
//                                            ProxyConfig cfg,
//                                            RegistrationState state,
//                                            String smessage) {
//                                    }
//                                });



        if (!mTestDelayElapsed) {
            // Only used when testing. Simulates a 5 seconds delay for launching service
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            mTestDelayElapsed = true;
                        }
                    },
                    5000);
        }

        BluetoothManager.getInstance().initBluetooth();

        return START_STICKY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        setupActivityMonitor();

        // Needed in order for the two next calls to succeed, libraries must have been loaded first
        LinphonePreferences.instance().setContext(getBaseContext());
        Factory.instance().setLogCollectionPath(getFilesDir().getAbsolutePath());
        //boolean isDebugEnabled = LinphonePreferences.instance().isDebugEnabled();
        //configureLoggingService(true, getString(R.string.app_name));
        // LinphoneService isn't ready yet so we have to manually set up the Java logging service
        //if (LinphonePreferences.instance().useJavaLogger()) {
            Factory.instance().getLoggingService().addListener(mJavaLoggingService);
        //}

        // Dump some debugging information to the logs
        Log.i(START_LINPHONE_LOGS);
        dumpDeviceInformation();
        dumpInstalledLinphoneInformation();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    public void createOverlay() {
        if (mOverlay != null) destroyOverlay();

        Core core = LinphoneManager.getLc();
        Call call = core.getCurrentCall();
        if (call == null || !call.getCurrentParams().isVideoEnabled()) return;

        if ("MSAndroidOpenGLDisplay".equals(core.getVideoDisplayFilter())) {
            mOverlay = new LinphoneGL2JNIViewOverlay(this);
        } else {
            mOverlay = new LinphoneTextureViewOverlay(this);
        }
        WindowManager.LayoutParams params = mOverlay.getWindowManagerLayoutParams();
        params.x = 0;
        params.y = 0;
        mOverlay.addToWindowManager(mWindowManager, params);
    }

    public void destroyOverlay() {
        if (mOverlay != null) {
            mOverlay.removeFromWindowManager(mWindowManager);
            mOverlay.destroy();
        }
        mOverlay = null;
    }

    private void dumpDeviceInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("DEVICE=").append(Build.DEVICE).append("\n");
        sb.append("MODEL=").append(Build.MODEL).append("\n");
        sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
        sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Supported ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi).append(", ");
        }
        sb.append("\n");
        Log.i(sb.toString());
    }

    private void dumpInstalledLinphoneInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException nnfe) {
            Log.e(nnfe);
        }

        if (info != null) {
            Log.i(
                    "[Service] Linphone version is ",
                    info.versionName + " (" + info.versionCode + ")");
        } else {
            Log.i("[Service] Linphone version is unknown");
        }
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
        if (mActivityCallbacks != null) {
            getApplication().unregisterActivityLifecycleCallbacks(mActivityCallbacks);
            mActivityCallbacks = null;
        }
        destroyOverlay();

        Core lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
            lc = null; // To allow the gc calls below to free the Core
        }

        sInstance = null;
        LinphoneManager.destroy();
        if (LinphonePreferences.instance().useJavaLogger()) {
            Factory.instance().getLoggingService().removeListener(mJavaLoggingService);
        }

        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    public void setActivityToLaunchOnIncomingReceived(Activity activity) {
        mIncomingReceivedActivity = activity;
    }

    private void onIncomingReceived() {
        if(mIncomingReceivedActivity != null) {
            Intent intent = new Intent().setClass(this, mIncomingReceivedActivity.getClass());
            // This flag is required to start an Activity from a Service context
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /*Believe me or not, but knowing the application visibility state on Android is a nightmare.
    After two days of hard work I ended with the following class, that does the job more or less reliabily.
    */
    class ActivityMonitor implements Application.ActivityLifecycleCallbacks {
        private final ArrayList<Activity> activities = new ArrayList<>();
        private boolean mActive = false;
        private int mRunningActivities = 0;
        private InactivityChecker mLastChecker;

        @Override
        public synchronized void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i("[Service] Activity created:" + activity);
            if (!activities.contains(activity)) activities.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i("Activity started:" + activity);
        }

        @Override
        public synchronized void onActivityResumed(Activity activity) {
            Log.i("[Service] Activity resumed:" + activity);
            if (activities.contains(activity)) {
                mRunningActivities++;
                Log.i("[Service] runningActivities=" + mRunningActivities);
                checkActivity();
            }
        }

        @Override
        public synchronized void onActivityPaused(Activity activity) {
            Log.i("[Service] Activity paused:" + activity);
            if (activities.contains(activity)) {
                mRunningActivities--;
                Log.i("[Service] runningActivities=" + mRunningActivities);
                checkActivity();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i("[Service] Activity stopped:" + activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public synchronized void onActivityDestroyed(Activity activity) {
            Log.i("[Service] Activity destroyed:" + activity);
            activities.remove(activity);
        }

        void startInactivityChecker() {
            if (mLastChecker != null) mLastChecker.cancel();
            LinphoneService.this.handler.postDelayed(
                    (mLastChecker = new InactivityChecker()), 2000);
        }

        void checkActivity() {
            if (mRunningActivities == 0) {
                if (mActive) startInactivityChecker();
            } else if (mRunningActivities > 0) {
                if (!mActive) {
                    mActive = true;
                    LinphoneService.this.onForegroundMode();
                }
                if (mLastChecker != null) {
                    mLastChecker.cancel();
                    mLastChecker = null;
                }
            }
        }

        class InactivityChecker implements Runnable {
            private boolean isCanceled;

            void cancel() {
                isCanceled = true;
            }

            @Override
            public void run() {
                synchronized (LinphoneService.this) {
                    if (!isCanceled) {
                        if (ActivityMonitor.this.mRunningActivities == 0 && mActive) {
                            mActive = false;
                            LinphoneService.this.onBackgroundMode();
                        }
                    }
                }
            }
        }
    }

    private void configureLoggingService(boolean isDebugEnabled, String appName) {
        Factory.instance().enableLogcatLogs(true);
//        if (!LinphonePreferences.instance().useJavaLogger()) {
//            Factory.instance().enableLogCollection(LogCollectionState.Enabled);
//            Factory.instance().setDebugMode(isDebugEnabled, appName);
//        } else
        {
            Factory.instance().setDebugMode(isDebugEnabled, appName);
            Factory.instance()
                    .enableLogCollection(LogCollectionState.EnabledWithoutPreviousLogHandler);
            if (isDebugEnabled) {
                //if (isReady()) {
                    Factory.instance()
                            .getLoggingService()
                            .addListener(getJavaLoggingService());
                //}
            } else {
                if (isReady()) {
                    Factory.instance()
                            .getLoggingService()
                            .removeListener(getJavaLoggingService());
                }
            }
        }
    }

    /**
     * 驱动服务
     */
    public static void startService(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("command", 1); //COMMAND_CHECK_LOCAL_UPDATING
        intent.putExtra("delay", 5000);
        ComponentName cn = new ComponentName("com.hcs.android.call",
                "com.hcs.android.call.LinphoneService");
        intent.setComponent(cn);
        context.startService(intent);
    }
}
