package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RoomScreenTemplate;

import org.simple.eventbus.EventBus;

/**
 * 门口屏特有功能
 */
public class RoomScreenManager extends SlaveManager{
    private RoomScreenConfig mConfig;
    public RoomScreenManager(Context context){
        super(context);
    }

    @Override
    public DeviceConfig getDeviceConfig(){
        synchronized (mSynObj) {
            if(mConfig == null) {
                mConfig = (RoomScreenConfig) DeviceConfigFactory.getInstance().getDeviceConfig(mContext, DeviceTypeEnum.ROOM_SCREEN);
            }
        }
        return mConfig;
    }

    /**
     * 更新模板
     */
    @Override
    public void updateRoomTemplate(@NonNull RequestDTO<RoomScreenTemplate> requestDTO){
        if(requestDTO.getData() != null) {
            RoomScreenTemplate roomScreenTemplate = requestDTO.getData();
            ((RoomScreenConfig)getDeviceConfig()).setTemplate(roomScreenTemplate, "");
            //通知界面层缓存更新了
            EventBus.getDefault().post(roomScreenTemplate, EventBusConstant.HANDLE_TEMPLATE_UPDATED);
        }
    }

    /**
     * 获取自己是什么型号的设备
     */
    @Override
    public DeviceTypeEnum getSelfDeviceType(){
        return DeviceTypeEnum.ROOM_SCREEN;
    }
}
