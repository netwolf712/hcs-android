package com.hcs.android.maintain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.network.NetConfig;
import com.hcs.android.common.network.NetworkManager;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.maintain.constant.PreferenceConstant;
import com.hcs.android.maintain.entity.DeviceBaseInfo;
import com.hcs.android.maintain.entity.server.Sys;

/**
 * 设备基本信息管理器
 */
public class DeviceBaseManager {

    private DeviceBaseInfo mDeviceBaseInfo;
    /**
     * 设备信息改变监听器
     */
    private ISimpleCustomer<DeviceBaseInfo> mDeviceInfoChangeListener;

    public void setDeviceInfoChangeListener(ISimpleCustomer<DeviceBaseInfo> deviceInfoChangeListener){
        mDeviceInfoChangeListener = deviceInfoChangeListener;
    }

    @SuppressLint("StaticFieldLeak")
    private static DeviceBaseManager mInstance = null;
    public static DeviceBaseManager getInstance(){
        if(mInstance == null){
            synchronized (DeviceBaseManager.class) {
                if(mInstance == null) {
                    mInstance = new DeviceBaseManager();
                }
            }
        }
        return mInstance;
    }

    private DeviceBaseManager(){
        mDeviceBaseInfo = getDeviceInfo(BaseApplication.getAppContext());
    }
    /**
     * 获取本机信息
     */
    @NonNull
    @SuppressLint("HardwareIds")
    private DeviceBaseInfo getDeviceInfo(Context context) {
        DeviceBaseInfo deviceBaseInfo = new DeviceBaseInfo();
        //先获取设备类型
        deviceBaseInfo.setProductName(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_PRODUCT_NAME,context.getString(R.string.default_maintain_produce_name)));
        deviceBaseInfo.setDeviceId(android.os.Build.SERIAL);
        deviceBaseInfo.setSystemVersion(Build.VERSION.RELEASE);
        deviceBaseInfo.setModule(Build.MODEL);
        deviceBaseInfo.setKernelVersion(ExeCommand.executeSuCmd("uname -r"));
        deviceBaseInfo.setUbootVersion(Build.BOOTLOADER);
        deviceBaseInfo.setAppRunTime(System.currentTimeMillis() -  BaseApplication.getAppStartTime());
        deviceBaseInfo.setSystemRunTime(SystemClock.elapsedRealtime());
        deviceBaseInfo.setTimeConfig(SNTPManager.getInstance().getTimeConfig());
        NetworkManager networkManager = new NetworkManager();
        NetConfig netConfig = networkManager.getNetConfig(context);
        deviceBaseInfo.setNetConfig(netConfig);
        return deviceBaseInfo;
    }

    /**
     * 修改网络配置
     * @param netConfig 网络配置
     */
    public void changeNetConfig(NetConfig netConfig){
        NetworkManager networkManager = new NetworkManager();
        networkManager.saveConfig(BaseApplication.getAppContext(), netConfig);
        mDeviceBaseInfo.setNetConfig(netConfig);
        if(mDeviceInfoChangeListener != null){
            mDeviceInfoChangeListener.accept(mDeviceBaseInfo);
        }
    }

    public NetConfig getNetConfig(){
        NetworkManager networkManager = new NetworkManager();
        return networkManager.getNetConfig(BaseApplication.getAppContext());
    }
    /**
     * 获取设备信息
     */
    public DeviceBaseInfo getDeviceInfo(){
        //这三个时间是会变的，每次获取时需要重新更新
        mDeviceBaseInfo.setAppRunTime(System.currentTimeMillis() -  BaseApplication.getAppStartTime());
        mDeviceBaseInfo.setSystemRunTime(SystemClock.elapsedRealtime());
        mDeviceBaseInfo.setTimeConfig(SNTPManager.getInstance().getTimeConfig());
        return mDeviceBaseInfo;
    }
}
