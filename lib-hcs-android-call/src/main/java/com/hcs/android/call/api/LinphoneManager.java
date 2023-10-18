package com.hcs.android.call.api;

/*
LinphoneManager.java
Copyright (C) 2018  Belledonne Communications, Grenoble, France

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

import static android.media.AudioManager.MODE_RINGTONE;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_VOICE_CALL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcs.android.call.LinphoneService;
import com.hcs.android.call.R;
import com.hcs.android.call.receivers.BluetoothManager;
import com.hcs.android.call.receivers.HookReceiver;
import com.hcs.android.call.receivers.OutgoingCallReceiver;
import com.hcs.android.call.util.LinphoneUtils;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.MediaScanner;
import com.hcs.android.common.util.PermissionCheckUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import org.linphone.core.Account;
import org.linphone.core.Address;
import org.linphone.core.AudioDevice;
import org.linphone.core.AuthInfo;
import org.linphone.core.AuthMethod;
import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.CallLog;
import org.linphone.core.CallParams;
import org.linphone.core.CallStats;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.ConfiguringState;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Factory;
import org.linphone.core.Friend;
import org.linphone.core.FriendList;
import org.linphone.core.GlobalState;
import org.linphone.core.InfoMessage;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Reason;
import org.linphone.core.RegistrationState;
import org.linphone.mediastream.Version;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration.AndroidCamera;
import org.linphone.mediastream.video.capture.hwconf.Hacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

/**
 * Manager of the low level LibLinphone stuff.<br>
 * Including:
 *
 * <ul>
 *   <li>Starting C liblinphone
 *   <li>Reacting to C liblinphone state changes
 *   <li>Calling Linphone android service listener methods
 *   <li>Interacting from Android GUI/service with low level SIP stuff/
 * </ul>
 *
 * <p>Add Service Listener to react to Linphone state changes.
 */
public class LinphoneManager extends CoreListenerStub{

    private static final int LINPHONE_VOLUME_STREAM = STREAM_VOICE_CALL;

    @SuppressLint("StaticFieldLeak")
    private static LinphoneManager sInstance;
    private static boolean sExited;

    public final String configFile;

    /** Called when the activity is first created. */
    private final String mLPConfigXsd;

    private final String mLinphoneFactoryConfigFile;
    private final String mLinphoneDynamicConfigFile, mDefaultDynamicConfigFile;
    private final String mRingSoundFile;
    private final String mFriendsDatabaseFile;
    private final String mUserCertsPath;
    private final Context mServiceContext;
    private Activity mActivityContext;
    private final AudioManager mAudioManager;
    private final Resources mResources;
    private final LinphonePreferences mPrefs;
    private Core mCore;
    private final String mBasePath;
    private boolean mAudioFocused;
    private boolean mEchoTesterIsRunning;
    private boolean mCallGsmON;
    private BroadcastReceiver mHookReceiver;
    private BroadcastReceiver mCallReceiver;
    private boolean mHandsetON = false;
    private final MediaScanner mMediaScanner;
    private Call mRingingCall;
    private MediaPlayer mRingerPlayer;
    private final Vibrator mVibrator;
    private boolean mIsRinging;

    private final Object mSynObj = new Object();

    public final Object getSynObj(){
        return mSynObj;
    }
    /**
     * 全局状态
     */
    private GlobalState mGlobalState = GlobalState.Startup;
    /**
     * 呼叫状态监听器
     */
    private ICallListener mCallListener = null;

    /**
     * 聊天信息监听器
     */
    private IChatMessageListener mChatMessageListener = null;


    /**
     * 全局状态监听器
     */
    private IGlobalStateListener mGlobalStateListener = null;

    /**
     * 组播放音监听器
     */
    private IMulticastPlayListener mMulticastPlayerListener = null;


    /**
     * 呼叫事件队列
     */
    private final ConcurrentLinkedDeque<CallEntity> mCallQueue = new ConcurrentLinkedDeque<>();

    /**
     * 呼叫事件产生信号量
     */
    private final Semaphore mCallEvent = new Semaphore(1);

    /**
     * 呼叫队列运行控制标记
     */
    private boolean mCallQueueRun = true;

    private LinphoneManager(@NonNull Context c) {
        sExited = false;
        mEchoTesterIsRunning = false;
        mServiceContext = c;
        mBasePath = c.getFilesDir().getAbsolutePath();
        mLPConfigXsd = mBasePath + "/lpconfig.xsd";
        mLinphoneFactoryConfigFile = mBasePath + "/linphonerc";
        configFile = mBasePath + "/.linphonerc";
        mLinphoneDynamicConfigFile = mBasePath + "/linphone_assistant_create.rc";
        mDefaultDynamicConfigFile = mBasePath + "/default_assistant_create.rc";
        //String mChatDatabaseFile = mBasePath + "/linphone-history.db";
        //String mCallLogDatabaseFile = mBasePath + "/linphone-log-history.db";
        mFriendsDatabaseFile = mBasePath + "/linphone-friends.db";
        mRingSoundFile = mBasePath + "/share/sounds/linphone/rings/notes_of_the_optimistic.mkv";
        mUserCertsPath = mBasePath + "/user-certs";

        mPrefs = LinphonePreferences.instance();
        mAudioManager = ((AudioManager) c.getSystemService(Context.AUDIO_SERVICE));
        mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        mResources = c.getResources();

        File f = new File(mUserCertsPath);
        if (!f.exists()) {
            if (!f.mkdir()) {
                KLog.e("[Manager] " + mUserCertsPath + " can't be created.");
            }
        }
        mMediaScanner = new MediaScanner(c);

        //开启呼叫处理线程
        startCallWork();
    }


    public static synchronized void createAndStart(Context c, boolean isPush) {
        if (sInstance != null) {
            KLog.e(
                    "[Manager] Linphone Manager is already initialized ! Destroying it and creating a new one...");
            destroy();
        }

        sInstance = new LinphoneManager(c);
        sInstance.startLibLinphone(c, isPush);

    }

    public static synchronized LinphoneManager getInstance() {
        if (sInstance != null) return sInstance;

        if (sExited) {
            throw new RuntimeException(
                    "[Manager] Linphone Manager was already destroyed. "
                            + "Better use getLcIfManagerNotDestroyedOrNull and check returned value");
        }

        throw new RuntimeException("[Manager] Linphone Manager should be created before accessed");
    }

    public static synchronized Core getLc() {
        return getInstance().mCore;
    }

    private static void BluetoothManagerDestroy() {
        BluetoothManager.getInstance().destroy();
    }

    public static synchronized void destroy() {
        if (sInstance == null) return;
        sInstance.mMediaScanner.destroy();
        sExited = true;
        sInstance.destroyCore();
        sInstance = null;
    }


    @Nullable
    public static synchronized Core getLcIfManagerNotDestroyedOrNull() {
        if (sExited || sInstance == null) {
            // Can occur if the UI thread play a posted event but in the meantime the
            // LinphoneManager was destroyed
            // Ex: stop call and quickly terminate application.
            return null;
        }
        return getLc();
    }

    public static boolean isInstanced() {
        return sInstance != null;
    }

    private void routeAudioToSpeakerHelper(boolean speakerOn) {
        KLog.w(
                "[Manager] Routing audio to "
                        + (speakerOn ? "speaker" : "earpiece")
                        + ", disabling bluetooth audio route");
        BluetoothManager.getInstance().disableBluetoothSCO();

        enableSpeaker(speakerOn);
    }

    public boolean isSpeakerEnabled() {
        return mAudioManager != null && mAudioManager.isSpeakerphoneOn();
    }

    public void enableSpeaker(boolean enable) {
        mAudioManager.setSpeakerphoneOn(enable);
    }

    public void routeAudioToSpeaker() {
        routeAudioToSpeakerHelper(true);
    }

    public void routeAudioToReceiver() {
        routeAudioToSpeakerHelper(false);
    }

    public Call newOutgoingCall(Address to) {
        if (to == null) return null;
        //检查权限
        PermissionCheckUtil.checkAndRequestRecordVideoPermissions(mActivityContext,null);

        boolean isLowBandwidthConnection =
                !LinphoneUtils.isHighBandwidthConnection(
                        LinphoneService.instance().getApplicationContext());

        synchronized (mSynObj) {
//            if (mCore.isNetworkReachable()) {
                if (Version.isVideoCapable()) {
                    boolean prefVideoEnable = mPrefs.isVideoEnabled();
                    boolean prefInitiateWithVideo = mPrefs.shouldInitiateVideoCall();
                    return CallEasier.getInstance()
                            .inviteAddress(
                                    to,
                                    prefVideoEnable && prefInitiateWithVideo,
                                    isLowBandwidthConnection);
                } else {
                    return CallEasier.getInstance().inviteAddress(to, false, isLowBandwidthConnection);
                }
//            } else {
//                KLog.e("[Manager] Error: " + getString(R.string.error_network_unreachable));
//            }
        }
//        return null;
    }

    /**
     * 获取呼叫地址
     * @param to 电话号码
     * @param displayName 显示名称
     * @return 呼叫地址
     */
    @Nullable
    private Address getCallAddress(@NonNull String to, String displayName){
        synchronized (mSynObj) {
            // If to is only a username, try to find the contact to get an alias if existing
            if (!to.startsWith("sip:") || !to.contains("@")) {
                //Friend friend = getFriendByPhoneNumber(to);
                Friend friend = LinphoneManager.getInstance().getFriendByRefId(to);
                if (friend != null && friend.getAddress() != null) {
                    to = friend.getAddress().asString();
                }
            }

            Address lAddress;
            lAddress = mCore.interpretUrl(to); // InterpretUrl does normalizePhoneNumber
            if (lAddress == null) {
                KLog.e("[Manager] Couldn't convert to String to Address : " + to);
                return null;
            }

            if (displayName != null) {
                lAddress.setDisplayName(displayName);
            }
            return lAddress;
        }
    }
    public Call newOutgoingCall(String to, String displayName) {
        return newOutgoingCall(getCallAddress(to, displayName));
    }

    /**
     * 通过好友索引找到好友进行呼叫
     * @param refId 好友索引
     * @param displayName 显示名称
     */
    public Call newOutgoingCallByRefId(String refId, String displayName) {
        Friend friend = getFriendByRefId(refId);
        if(friend != null) {
            Address address = friend.getAddress();
            if (displayName != null && address != null) {
                address.setDisplayName(displayName);
            }
            return newOutgoingCall(address);
        }
        return null;
    }

    /**
     * 通过电话号码找到好友
     * @param phoneNumber 电话号码
     * @return 好友
     */
    public Friend getFriendByPhoneNumber(String phoneNumber){
        synchronized (mSynObj) {
            if (phoneNumber == null) {
                return null;
            }
            Core lc = getLcIfManagerNotDestroyedOrNull();
//        ProxyConfig lpc = null;
//        if (lc != null) {
//            lpc = lc.getDefaultProxyConfig();
//        }
//        if (lpc == null) {
//            return null;
//        }
//        String normalized = lpc.normalizePhoneNumber(phoneNumber);
//        if (normalized == null) {
//            normalized = phoneNumber;
//        }
//
//        Address addr = lpc.normalizeSipUri(normalized);
//        if (addr == null) {
//            return null;
//        }
//        addr.setUriParam("user", "phone");
//        return lc.findFriend(addr); // Without this, the hashmap inside liblinphone won't find it...
            if (lc != null) {
                return lc.findFriendByPhoneNumber(phoneNumber);
            } else {
                return null;
            }
        }
    }

    public Friend getFriendByRefId(String refId){
        synchronized (mSynObj) {
            Core lc = getLcIfManagerNotDestroyedOrNull();
            if (lc == null) {
                return null;
            }
            return lc.getFriendByRefKey(refId);
        }
    }

    public FriendList getFriendListByName(String name){
        synchronized (mSynObj) {
            Core lc = getLcIfManagerNotDestroyedOrNull();
            if (lc == null) {
                return null;
            }
            return lc.getFriendListByName(name);
        }
    }

    private void resetCameraFromPreferences() {
        synchronized (mSynObj) {
            boolean useFrontCam = mPrefs.useFrontCam();
            int camId = 0;
            AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
            for (AndroidCamera androidCamera : cameras) {
                if (androidCamera.frontFacing == useFrontCam) {
                    camId = androidCamera.id;
                    break;
                }
            }
            String[] devices = getLc().getVideoDevicesList();
            if (camId >= devices.length) {
                KLog.e(
                        "[Manager] Trying to use a camera id that's higher than the linphone's devices list, using 0 to prevent crash...");
                camId = 0;
            }
            String newDevice = devices[camId];

            LinphoneManager.getLc().setVideoDevice(newDevice);
        }
    }

    private void enableCamera(Call call, boolean enable) {
        synchronized (mSynObj) {
            if (call != null) {
                call.setCameraEnabled(enable);
            }
        }
    }

    private void terminateCall() {
        if (mCore.inCall()) {
            Objects.requireNonNull(mCore.getCurrentCall()).terminate();
        }
    }

    private synchronized void destroyCore() {
        KLog.w("[Manager] Destroying Core");
        sExited = true;
        BluetoothManagerDestroy();
        try {
            destroyLinphoneCore();
        } catch (RuntimeException e) {
            KLog.e("[Manager] Destroy Core Runtime Exception: " + e);
        } finally {
            try {
                mServiceContext.unregisterReceiver(mHookReceiver);
            } catch (Exception e) {
                KLog.e("[Manager] unregister receiver exception: " + e);
            }
            try {
                mServiceContext.unregisterReceiver(mCallReceiver);
            } catch (Exception e) {
                KLog.e("[Manager] unregister receiver exception: " + e);
            }
            mCore = null;
        }
    }

    public void restartCore() {
        synchronized (mSynObj) {
            mCore.stop();
            mCore.start();
        }
    }

    private synchronized void startLibLinphone(Context c, boolean isPush) {
        try {
            copyAssetsFromPackage();
            // traces alway start with traces enable to not missed first initialization
            mCore = Factory.instance().createCore(configFile, mLinphoneFactoryConfigFile, c);
            mCore.addListener(this);
            mCore.setAutoIterateBackgroundSchedule(20);
            mCore.enterForeground();
            mCore.setAutoIterateEnabled(true);
            mCore.start();
            mPrefs.enableDeviceRingtone(false);
//            mLinphoneThread.startWork(20L,()->{
//                if (mCore != null) {
//                    mCore.iterate();
//                }
//            });
        } catch (Exception e) {
            KLog.e(e.getMessage() + "[Manager] Cannot start linphone");
        }
    }

    private synchronized void initLiblinphone(Core lc) {
        mCore = lc;

        mCore.setZrtpSecretsFile(mBasePath + "/zrtp_secrets");
        String deviceName = mPrefs.getDeviceName();
        String appName = mServiceContext.getResources().getString(R.string.user_agent);
        String userAgent = appName + "/" + " (" + deviceName + ") LinphoneSDK";

        mCore.setUserAgent(userAgent, getString(R.string.user_agent));

        // mCore.setChatDatabasePath(mChatDatabaseFile);
        //mCore.setCallLogsDatabasePath(mCallLogDatabaseFile);
        mCore.setFriendsDatabasePath(mFriendsDatabaseFile);
        mCore.setUserCertificatesPath(mUserCertsPath);
        // mCore.setCallErrorTone(Reason.NotFound, mErrorToneFile);
        //关闭铃声功能
        //enableDeviceRingtone(mPrefs.isDeviceRingtoneEnabled());
        //关闭好友订阅功能
        mPrefs.enabledFriendlistSubscription(false);
        int availableCores = Runtime.getRuntime().availableProcessors();
        KLog.w("[Manager] MediaStreamer : " + availableCores + " cores detected and configured");

        mCore.migrateLogsFromRcToDb();

        IntentFilter mCallIntentFilter = new IntentFilter("android.intent.action.ACTION_NEW_OUTGOING_CALL");
        mCallIntentFilter.setPriority(99999999);
        mCallReceiver = new OutgoingCallReceiver();
        try {
            mServiceContext.registerReceiver(mCallReceiver, mCallIntentFilter);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        IntentFilter mHookIntentFilter = new IntentFilter("com.base.module.phone.HOOKEVENT");
        mHookIntentFilter.setPriority(999);
        mHookReceiver = new HookReceiver();
        mServiceContext.registerReceiver(mHookReceiver, mHookIntentFilter);
        if(LinphonePreferences.instance().isVideoEnabled()) {
            //需要用视频的时候才启动
            resetCameraFromPreferences();
        }

        mCallGsmON = false;

    }

    public GlobalState getGlobalState(){
        return mGlobalState;
    }

    public void setHandsetMode(Boolean on) {
        synchronized (mSynObj) {
            if (mCore.isIncomingInvitePending() && on) {
                mHandsetON = true;
                acceptCall(mCore.getCurrentCall());
            } else if (on) {
                mHandsetON = true;
            } else {
                mHandsetON = false;
                LinphoneManager.getInstance().terminateCall();
            }
        }
    }

    public boolean isHandsetModeOn() {
        return mHandsetON;
    }

    private void copyAssetsFromPackage() throws IOException {
        copyIfNotExist(R.raw.linphonerc_default, configFile);
        copyFromPackage(R.raw.linphonerc_factory, new File(mLinphoneFactoryConfigFile).getName());
        copyIfNotExist(R.raw.lpconfig, mLPConfigXsd);
        copyFromPackage(
                R.raw.default_assistant_create, new File(mDefaultDynamicConfigFile).getName());
        copyFromPackage(
                R.raw.linphone_assistant_create, new File(mLinphoneDynamicConfigFile).getName());
    }

    private void copyIfNotExist(int ressourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.getName());
        }
    }

    private void copyFromPackage(int resourceId, String target) throws IOException {
        FileOutputStream lOutputStream = mServiceContext.openFileOutput(target, 0);
        InputStream lInputStream = mResources.openRawResource(resourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

    private void destroyLinphoneCore() {
        mCore.stop();
    }

    private String getString(int key) {
        return mResources.getString(key);
    }

    @Override
    public void onImeeUserRegistration(@NonNull Core core, boolean b, @NonNull String s, @NonNull String s1) {

    }

    @Override
    public void onEcCalibrationAudioInit(@NonNull Core lc) {}

    @Override
    public void onDtmfReceived(@NonNull Core lc,@NonNull Call call, int dtmf) {
        KLog.d("[Manager] DTMF received: " + dtmf);
    }

    @Override
    public void onMessageReceived(@NonNull Core lc, @NonNull final ChatRoom cr, @NonNull final ChatMessage message) {
        //synchronized (mSynObj) {
            KLog.i("received message " + message.getUtf8Text());
            message.getErrorInfo();
            if (message.getErrorInfo().getReason() == Reason.UnsupportedContent) {
                KLog.w("[Manager] Message received but content is unsupported, do not notify it");
                return;
            }

            if (StringUtil.isEmpty(message.getUtf8Text()) && message.getFileTransferInformation() == null) {
                KLog.w(
                        "[Manager] Message has no text or file transfer information to display, ignoring it...");
                return;
            }

            if (message.isOutgoing()) {
                return;
            }

            if (mChatMessageListener != null) {
                mChatMessageListener.onChatMessageReceived(cr, message);
            }
        //}

    }

    @Override
    public void onAccountRegistrationStateChanged(@NonNull Core core, @NonNull Account account, RegistrationState registrationState, @NonNull String s) {

    }

    @Override
    public void onEcCalibrationResult(@NonNull Core lc, EcCalibratorStatus status, int delay_ms) {
        ((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
                .setMode(AudioManager.MODE_NORMAL);
        mAudioManager.abandonAudioFocus(null);
        KLog.i("[Manager] Set audio mode on 'Normal'");
    }

    public void onGlobalStateChanged(@NonNull final Core lc, final GlobalState state, @NonNull final String message) {
        synchronized (mSynObj) {
            KLog.i("New global state [", state, "]");
            if (state == GlobalState.On) {
                try {
                    initLiblinphone(lc);
                } catch (IllegalArgumentException iae) {
                    KLog.e("[Manager] Global State Changed Illegal Argument Exception: " + iae);
                }
            }
            mGlobalState = state;
            if (mGlobalStateListener != null) {
                mGlobalStateListener.onGlobalStateChanged(state, message);
            }
        }
    }

    public void onRegistrationStateChanged(
            @NonNull final Core lc,
            @NonNull final ProxyConfig proxy,
            final RegistrationState state,
            @NonNull final String message) {
        synchronized (mSynObj) {
            KLog.i("[Manager] New registration state [" + state + "]");

            if (state == RegistrationState.Failed) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager)
                                mServiceContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                KLog.i("[Manager] Active network type: " + activeNetworkInfo.getTypeName());
                if (activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
                    KLog.i("[Manager] Active network is available");
                }
                KLog.i(
                        "[Manager] Active network reason and extra info: "
                                + activeNetworkInfo.getReason()
                                + " / "
                                + activeNetworkInfo.getExtraInfo());
                KLog.i(
                        "[Manager] Active network state "
                                + activeNetworkInfo.getState()
                                + " / "
                                + activeNetworkInfo.getDetailedState());
            }
        }
    }

    public Context getContext() {
        try {
            return BaseApplication.getAppContext();
        } catch (Exception e) {
            KLog.e(e);
        }
        return null;
    }

    public void setAudioManagerModeNormal() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    private void setAudioManagerInCallMode() {
        if (mAudioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            KLog.w("[Manager][AudioManager] already in MODE_IN_COMMUNICATION, skipping...");
            return;
        }
        KLog.d("[Manager][AudioManager] Mode: MODE_IN_COMMUNICATION");

        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 设置呼叫状态监听器
     */
    public void setCallListener(ICallListener callListener){
        this.mCallListener = callListener;
    }

    /**
     * 设置聊天信息监听器
     */
    public void setChatMessageListener(IChatMessageListener chatMessageListener){
        this.mChatMessageListener = chatMessageListener;
    }

    /**
     * 设置全局状态监听器
     */
    public void setGlobalStateListener(IGlobalStateListener globalStateListener){
        this.mGlobalStateListener = globalStateListener;
    }

    @SuppressLint("Wakelock")
    public void onCallStateChanged(
            @NonNull final Core lc, @NonNull final Call call, final State state, @NonNull final String message) {
        //将事件压入呼叫处理队列
        //addCallEvent(lc, call, state, message);
        onASynCallStateChanged(lc,call,state,message);
    }

    private void startBluetooth() {
        if (BluetoothManager.getInstance().isBluetoothHeadsetAvailable()) {
            BluetoothManager.getInstance().routeAudioToBluetooth();
        }
    }

    public void onCallStatsUpdated(@NonNull final Core lc, @NonNull final Call call, @NonNull final CallStats stats) {}

    @Override
    public void onChatRoomStateChanged(@NonNull Core lc, @NonNull ChatRoom cr, ChatRoom.State state) {}

    @Override
    public void onQrcodeFound(@NonNull Core lc, String result) {}

    @Override
    public void onAudioDeviceChanged(@NonNull Core core, @NonNull AudioDevice audioDevice) {

    }

    @Override
    public void onAudioDevicesListUpdated(@NonNull Core core) {

    }

    @Override
    public void onLastCallEnded(@NonNull Core core) {

    }

    public void onCallEncryptionChanged(
            @NonNull Core lc, @NonNull Call call, boolean encrypted, String authenticationToken) {}

    public void startEcCalibration() {
        synchronized (mSynObj) {
            routeAudioToSpeaker();
            setAudioManagerInCallMode();
            KLog.i("[Manager] Set audio mode on 'Voice Communication'");
            requestAudioFocus(STREAM_VOICE_CALL);
            int oldVolume = mAudioManager.getStreamVolume(STREAM_VOICE_CALL);
            int maxVolume = mAudioManager.getStreamMaxVolume(STREAM_VOICE_CALL);
            mAudioManager.setStreamVolume(STREAM_VOICE_CALL, maxVolume, 0);
            mCore.startEchoCancellerCalibration();
            mAudioManager.setStreamVolume(STREAM_VOICE_CALL, oldVolume, 0);
        }
    }

    public int startEchoTester() {
        synchronized (mSynObj) {
            routeAudioToSpeaker();
            setAudioManagerInCallMode();
            KLog.i("[Manager] Set audio mode on 'Voice Communication'");
            requestAudioFocus(STREAM_VOICE_CALL);
            int maxVolume = mAudioManager.getStreamMaxVolume(STREAM_VOICE_CALL);
            int sampleRate;
            mAudioManager.setStreamVolume(STREAM_VOICE_CALL, maxVolume, 0);
            String sampleRateProperty =
                    mAudioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            sampleRate = Integer.parseInt(sampleRateProperty);
            mCore.startEchoTester(sampleRate);
            mEchoTesterIsRunning = true;
        }
        return 1;
    }

    public int stopEchoTester() {
        synchronized (mSynObj) {
            mEchoTesterIsRunning = false;
            mCore.stopEchoTester();
            routeAudioToReceiver();
            ((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
                    .setMode(AudioManager.MODE_NORMAL);
            KLog.i("[Manager] Set audio mode on 'Normal'");
        }
        return 1; // status;
    }

    public boolean getEchoTesterStatus() {
        return mEchoTesterIsRunning;
    }

    private void requestAudioFocus(int stream) {
        //synchronized (mSynObj) {
            if (!mAudioFocused) {
                int res =
                        mAudioManager.requestAudioFocus(
                                null, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
                KLog.d(
                        "[Manager] Audio focus requested: "
                                + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                                ? "Granted"
                                : "Denied"));
                if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mAudioFocused = true;
            }
        //}
    }

    public void enableDeviceRingtone(boolean use) {
        synchronized (mSynObj) {
            if (use) {
                mCore.setRing(null);
            } else {
                mCore.setRing(mRingSoundFile);
            }
        }
    }

    private synchronized void startRinging() {
        //synchronized (mSynObj) {
            if (!LinphonePreferences.instance().isDeviceRingtoneEnabled()) {
                // Enable speaker audio route, linphone library will do the ringing itself automatically
                routeAudioToSpeaker();
                return;
            }

            routeAudioToSpeaker(); // Need to be able to ear the ringtone during the early media

            // if (Hacks.needGalaxySAudioHack())
            mAudioManager.setMode(MODE_RINGTONE);

            try {
                if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
                        || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
                        && mVibrator != null
                        && LinphonePreferences.instance().isIncomingCallVibrationEnabled()) {
                    long[] patern = {0, 1000, 1000};
                    mVibrator.vibrate(patern, 1);
                }
                if (mRingerPlayer == null) {
                    requestAudioFocus(STREAM_RING);
                    mRingerPlayer = new MediaPlayer();
                    mRingerPlayer.setAudioStreamType(STREAM_RING);

                    String ringtone =
                            LinphonePreferences.instance()
                                    .getRingtone(Settings.System.DEFAULT_RINGTONE_URI.toString());
                    try {
                        if (ringtone.startsWith("content://")) {
                            mRingerPlayer.setDataSource(mServiceContext, Uri.parse(ringtone));
                        } else {
                            FileInputStream fis = new FileInputStream(ringtone);
                            mRingerPlayer.setDataSource(fis.getFD());
                            fis.close();
                        }
                    } catch (IOException e) {
                        KLog.e(e.getMessage() + "[Manager] Cannot set ringtone");
                    }

                    mRingerPlayer.prepare();
                    mRingerPlayer.setLooping(true);
                    mRingerPlayer.start();
                } else {
                    KLog.w("[Manager] Already ringing");
                }
            } catch (Exception e) {
                KLog.e(e.getMessage() + "[Manager] Cannot handle incoming call");
            }
            mIsRinging = true;
        //}
    }

    private synchronized void stopRinging() {
        //synchronized (mSynObj) {
            if (mRingerPlayer != null) {
                mRingerPlayer.stop();
                mRingerPlayer.release();
                mRingerPlayer = null;
            }
            if (mVibrator != null) {
                mVibrator.cancel();
            }

            if (Hacks.needGalaxySAudioHack()) mAudioManager.setMode(AudioManager.MODE_NORMAL);

            mIsRinging = false;
            // You may need to call galaxys audio hack after this method
            if (!BluetoothManager.getInstance().isBluetoothHeadsetAvailable()) {
                KLog.d("[Manager] Stopped ringing, routing back to earpiece");
                routeAudioToReceiver();
            }
        //}
    }

    private static boolean reinviteWithVideo() {
        return CallEasier.getInstance().reinviteWithVideo();
    }
    private static boolean reinviteWithoutVideo() {
        return CallEasier.getInstance().reinviteWithoutVideo();
    }
    /** @return false if already in video call. */
    public boolean addVideo() {
        synchronized (mSynObj) {
            Call call = mCore.getCurrentCall();
            enableCamera(call, true);
            return reinviteWithVideo();
        }
    }
    public boolean removeVideo() {
        synchronized (mSynObj) {
            Call call = mCore.getCurrentCall();
            enableCamera(call, false);
            return reinviteWithoutVideo();
        }
    }
    /**
     * 接受呼叫
     */
    public boolean acceptCall(Call call) {
        if (call == null) {
            return false;
        }
        //synchronized (mSynObj) {
            //检查权限
            PermissionCheckUtil.checkAndRequestRecordVideoPermissions(mActivityContext, accept -> {
                CallParams params = LinphoneManager.getLc().createCallParams(call);

                boolean isLowBandwidthConnection =
                        !LinphoneUtils.isHighBandwidthConnection(
                                LinphoneService.instance().getApplicationContext());

                if (params != null) {
                    params.setLowBandwidthEnabled(isLowBandwidthConnection);
                    params.setRecordFile(
                            LinphoneUtils.getCallRecordingFilename(getContext(), call.getRemoteAddress()));
                } else {
                    KLog.e("[Manager] Could not create call params for call");
                    return;
                }
                call.acceptWithParams(params);
            });
        //}

        return true;
    }

    public void adjustVolume(int i) {
        //synchronized (mSynObj) {
            // starting from ICS, volume must be adjusted by the application, at least for
            // STREAM_VOICE_CALL volume stream
            mAudioManager.adjustStreamVolume(
                    LINPHONE_VOLUME_STREAM,
                    i < 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI);
        //}
    }

    public boolean getCallGsmON() {
        return mCallGsmON;
    }

    public void setCallGsmON(boolean on) {
        mCallGsmON = on;
    }

    public void setActivityContext(final Activity context){
        mActivityContext = context;
    }


    private void acceptCallUpdate(boolean accept) {

        synchronized (mSynObj) {
            Call call = getLc().getCurrentCall();
            if (call == null) {
                return;
            }

            CallParams params = getLc().createCallParams(call);
            if (accept) {
                if (params != null) {
                    params.setVideoEnabled(true);
                }
                getLc().setVideoCaptureEnabled(true);
                getLc().setVideoDisplayEnabled(true);
            }
            call.acceptWithParams(params);
        }
    }

    public void setMulticastPlayerListener(IMulticastPlayListener multicastPlayerListener){
        mMulticastPlayerListener = multicastPlayerListener;
    }

    @Override
    public void onTransferStateChanged(@NonNull Core lc, @NonNull Call call, State new_call_state) {}

    @Override
    public void onInfoReceived(@NonNull Core lc, @NonNull Call call, @NonNull InfoMessage info) {
        KLog.d("[Manager] Info message received from " + call.getRemoteAddress().asString());
        Content ct = info.getContent();
        if (ct != null) {
            KLog.d(
                    "[Manager] Info received with body with mime type "
                            + ct.getType()
                            + "/"
                            + ct.getSubtype()
                            + " and data ["
                            + ct.getStringBuffer()
                            + "]");
        }
    }

    @Override
    public void onChatRoomRead(@NonNull Core core,@NonNull  ChatRoom chatRoom) {}

    @Override
    public void onCallLogUpdated(@NonNull Core lc,@NonNull  CallLog newcl) {}

    @Override
    public void onMessageReceivedUnableDecrypt(@NonNull Core lc, @NonNull ChatRoom room, @NonNull ChatMessage message) {}

    @Override
    public void onConfiguringStatus(@NonNull Core lc, @NonNull ConfiguringState state, String message) {
        KLog.d("[Manager] Remote provisioning status = " + state.toString() + " (" + message + ")");

    }

    @Override
    public void onCallCreated(@NonNull Core lc, @NonNull Call call) {}

    @Override
    public void onChatRoomSubjectChanged(@NonNull Core core, @NonNull ChatRoom chatRoom) {}

    /**
     * 组播放音结束回调
     */
    @Override
    public void onMulticastPlayFinished(@NonNull Core core, long l) {
        if(mMulticastPlayerListener != null){
            mMulticastPlayerListener.onPlayFinished(l);
        }
    }
    @Override
    public void onCallIdUpdated(@NonNull Core core, @NonNull String s, @NonNull String s1) {

    }

    @Override
    public void onNetworkReachable(@NonNull Core lc, boolean enable) {}

    @Override
    public void onFirstCallStarted(@NonNull Core core) {

    }

    @Override
    public void onAuthenticationRequested(@NonNull Core lc, @NonNull AuthInfo authInfo, @NonNull AuthMethod method) {

    }

    @Override
    public void onMessageSent(@NonNull Core var1, @NonNull ChatRoom var2, @NonNull ChatMessage var3) {}


    public void onASynCallStateChanged(@NonNull final Core lc, @NonNull final Call call, final State state, @NonNull final String message) {
        //synchronized (mSynObj) {
        KLog.i("[Manager] New call state [", state, "]");
        if (state == State.IncomingReceived && !call.equals(lc.getCurrentCall())) {
            if (call.getReplacedCall() != null) {
                // attended transfer
                // it will be accepted automatically.
                return;
            }
        }
        if (mCallListener != null) {
            mCallListener.onCallStateChanged(call, state, message);
        }

        if ((state == State.IncomingReceived || state == State.IncomingEarlyMedia)
                && getCallGsmON()) {
            call.decline(Reason.Busy);
        } else if (state == State.IncomingReceived
                && (LinphonePreferences.instance().isAutoAnswerEnabled())
                && !getCallGsmON()) {
            acceptCall(call);
        } else if (state == State.IncomingReceived
                || (state == State.IncomingEarlyMedia)) {
            // Brighten screen for at least 10 seconds
            //if (mCore.getCallsNb() == 1) {
                mRingingCall = call;
                //关闭呼叫振铃的功能
                //requestAudioFocus(STREAM_RING);
                //startRinging();
                // otherwise there is the beep
            //}
        } else if (call == mRingingCall && mIsRinging) {
            // previous state was ringing, so stop ringing
            //stopRinging();
        }

        if (state == State.Connected) {
            //if (mCore.getCallsNb() == 1) {
                // It is for incoming calls, because outgoing calls enter MODE_IN_COMMUNICATION
                // immediately when they start.
                // However, incoming call first use the MODE_RINGING to play the local ring.
                if (call.getDir() == Call.Dir.Incoming) {
                    setAudioManagerInCallMode();
                    // mAudioManager.abandonAudioFocus(null);
                    requestAudioFocus(STREAM_VOICE_CALL);
                }
            //}

            if (Hacks.needSoftvolume()) {
                KLog.w("[Manager] Using soft volume audio hack");
                adjustVolume(0); // Synchronize
            }
        }

        if (state == State.End || state == State.Error) {
            //if (mCore.getCallsNb() == 0) {
                // Disabling proximity sensor
                Context activity = getContext();
                if (mAudioFocused) {
                    int res = mAudioManager.abandonAudioFocus(null);
                    KLog.d(
                            "[Manager] Audio focus released a bit later: "
                                    + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                                    ? "Granted"
                                    : "Denied"));
                    mAudioFocused = false;
                }
            //}

        }
        if (state == State.UpdatedByRemote) {
            // If the correspondent proposes video while audio call
            if (call.getRemoteParams() != null) {
                boolean remoteVideo = call.getRemoteParams().isVideoEnabled();
                boolean localVideo = call.getCurrentParams().isVideoEnabled();
                boolean autoAcceptCameraPolicy =
                        LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests();
                if (remoteVideo
                        && !localVideo
                        && !autoAcceptCameraPolicy
                        && LinphoneManager.getLc().getConference() == null) {
                    call.deferUpdate();
                } else {
                    PermissionCheckUtil.checkAndRequestRecordVideoPermissions(mActivityContext, accept -> acceptCallUpdate(true));
                }
            }
        }
        if (state == State.OutgoingInit) {
            // Enter the MODE_IN_COMMUNICATION mode as soon as possible, so that ringback
            // is heard normally in earpiece or bluetooth receiver.
            setAudioManagerInCallMode();
            requestAudioFocus(STREAM_VOICE_CALL);
            startBluetooth();
        }

        if (state == State.StreamsRunning) {
            startBluetooth();
            setAudioManagerInCallMode();
        }
        //}
    }

    /**
     * 开始呼叫处理工作
     */
    private void startCallWork(){
        Thread t = new Thread(()->{
           try{
               callEventWorker();
           }catch (Exception e){
               KLog.e(e);
           }

        });

        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    /**
     * 关闭呼叫处理工作
     */
    private void stopCallWork(){
        mCallQueueRun = false;
        mCallEvent.release();
    }

    /**
     * 添加呼叫事件到工作线程
     */
    public void addCallEvent(@NonNull final Core lc, @NonNull final Call call, final State state, @NonNull final String message){
        CallEntity callEntity = new CallEntity(lc,call,state,message);
        mCallQueue.add(callEntity);
        mCallEvent.release();
    }

    /**
     * 呼叫处理工作线程
     */
    private void callEventWorker() throws InterruptedException{
        while (mCallQueueRun){
            mCallEvent.acquire();
            while(!mCallQueue.isEmpty()){
                CallEntity entity = mCallQueue.poll();
                onASynCallStateChanged(entity.getLc(),entity.getCall(),entity.getState(),entity.getMessage());
            }
        }
    }
}
