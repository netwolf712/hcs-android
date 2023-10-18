package com.hcs.android.business.manager;

import android.content.Context;

import com.hcs.android.business.constant.DeviceTypeEnum;

/**
 * 设备配置器工厂
 * 根据不同类型的设备获取对应的配置器
 */
public class DeviceConfigFactory {
    private static DeviceConfigFactory mInstance = null;
    public static DeviceConfigFactory getInstance(){
        if(mInstance == null){
            synchronized (DeviceConfigFactory.class){
                if(mInstance == null) {
                    mInstance = new DeviceConfigFactory();
                }
            }
        }
        return mInstance;
    }

    public DeviceConfig getDeviceConfig(Context context, DeviceTypeEnum deviceTypeEnum){
        switch (deviceTypeEnum){
            case NURSE_STATION_MASTER:
                return new NurseStationConfig(context);
            case BED_SCREEN:
                return new BedScreenConfig(context);
            case ROOM_SCREEN:
                return new RoomScreenConfig(context);
            default:
                return new NurseStationConfig(context);
        }
    }
}
