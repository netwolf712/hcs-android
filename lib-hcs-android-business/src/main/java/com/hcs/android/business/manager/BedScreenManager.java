package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestPrivacy;
import com.hcs.android.common.util.StringUtil;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * 床头分机特有功能
 */
public class BedScreenManager extends SlaveManager{
    private BedScreenConfig mConfig;
    public BedScreenManager(Context context){
        super(context);
    }

    @Override
    public DeviceConfig getDeviceConfig(){
        synchronized (mSynObj) {
            if(mConfig == null) {
                mConfig = (BedScreenConfig) DeviceConfigFactory.getInstance().getDeviceConfig(mContext, DeviceTypeEnum.BED_SCREEN);
            }
        }
        return mConfig;
    }

    /**
     * 更新模板
     */
    @Override
    public void updateBedTemplate(@NonNull RequestDTO<BedScreenTemplate> requestDTO){
        if(requestDTO.getData() != null) {
            BedScreenTemplate bedScreenTemplate = requestDTO.getData();
            ((BedScreenConfig)getDeviceConfig()).setTemplate(bedScreenTemplate, "");
            //通知界面层缓存更新了
            EventBus.getDefault().post(bedScreenTemplate, EventBusConstant.HANDLE_TEMPLATE_UPDATED);
        }
    }

    /**
     * 获取自己是什么型号的设备
     */
    @Override
    public DeviceTypeEnum getSelfDeviceType(){
        return DeviceTypeEnum.BED_SCREEN;
    }
}
