package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.common.settings.SettingsHelper;

/**
 * 设备工厂
 * 由它产生对应类型的设备
 */
public class DeviceFactory {
    private static DeviceFactory mInstance = null;
    public static DeviceFactory getInstance(){
        if(mInstance == null){
            synchronized (DeviceFactory.class){
                if(mInstance == null) {
                    return mInstance = new DeviceFactory();
                }
            }
        }
        return mInstance;
    }
    /**
     * 设置设备类型
     */
    public void setDeviceType(Context context, @NonNull DeviceTypeEnum deviceType){
        SettingsHelper.getInstance(context).putData(PreferenceConstant.PREF_KEY_DEVICE_TYPE,deviceType.getValue());
    }

    public DeviceTypeEnum getDeviceType(Context context){
        return DeviceTypeEnum.findById(SettingsHelper.getInstance(context).getInt(PreferenceConstant.PREF_KEY_DEVICE_TYPE,DeviceTypeEnum.NURSE_STATION_MASTER.getValue()));
    }
    /**
     * 获取对应类型的设备管理器
     */
    public DeviceManager getDeviceManager(Context context, @NonNull DeviceTypeEnum deviceTypeEnum){
        switch (deviceTypeEnum){
            case NURSE_STATION_MASTER:
                return new NurseStationManager(context);
            case BED_SCREEN:
                return new BedScreenManager(context);
            case ROOM_SCREEN:
                return new RoomScreenManager(context);
            default:
                return new NurseStationManager(context);
        }
    }
}
