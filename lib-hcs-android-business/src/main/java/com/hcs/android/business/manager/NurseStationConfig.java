package com.hcs.android.business.manager;

import android.content.Context;

import com.hcs.android.business.constant.DeviceTypeEnum;

public class NurseStationConfig extends DeviceConfig{
    public NurseStationConfig(Context context){
        super(context);
    }

    @Override
    public DeviceTypeEnum getDeviceType(){
        return DeviceTypeEnum.NURSE_STATION_MASTER;
    }
}
