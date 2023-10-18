package com.hcs.android.business.request.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RoomScreenTemplate;
import com.hcs.android.business.entity.SlaveTemplate;
import com.hcs.android.business.manager.RoomScreenConfig;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;

/**
 * 病房门口屏模板请求模块
 */
public class RoomScreenTemplateRequestModel implements ITemplateRequestModel {

    private final Context mContext;

    private final RoomScreenConfig mRoomScreenConfig;

    public RoomScreenTemplateRequestModel(Context context){
        mContext = context;
        mRoomScreenConfig = new RoomScreenConfig(context);
    }
    /**
     * 获取当前配置的模板
     */
    @Override
    public RoomScreenTemplate getCurrentTemplate(){
        return mRoomScreenConfig.getTemplate(mRoomScreenConfig.getCurrentTemplateId());
    }

    /**
     * 根据模板id获取模板
     */
    @Override
    public RoomScreenTemplate getTemplate(String templateId){
        return mRoomScreenConfig.getTemplate(templateId);
    }

    /**
     * 保存模板配置
     */
    @Override
    public void saveTemplate(@NonNull SlaveTemplate roomScreenTemplate){
        mRoomScreenConfig.setTemplate((RoomScreenTemplate)roomScreenTemplate,roomScreenTemplate.getTemplateId());
    }

    /**
     * 设置当前工作的配置模板
     */
    @Override
    public RoomScreenTemplate setCurrentTemplate(String templateId){
        mRoomScreenConfig.setCurrentTemplateId(templateId);
        DeviceModel selfModel = WorkManager.getInstance().getSelfInfo();
        if (selfModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()) {
            //主机需要把这个配置推送给其它人
            RequestDTO<RoomScreenTemplate> requestDTO = new RequestDTO<>(selfModel, CommandEnum.REQ_UPDATE_ROOM_TEMPLATE.getId());
            requestDTO.setData(mRoomScreenConfig.getTemplate(templateId));
            WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
        }
        return getCurrentTemplate();
    }
}
