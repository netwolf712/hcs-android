package com.hcs.android.business.request.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.SlaveTemplate;
import com.hcs.android.business.manager.BedScreenConfig;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;

/**
 * 床头屏模板请求模块
 */
public class BedScreenTemplateRequestModel implements ITemplateRequestModel {

    private final Context mContext;

    private final BedScreenConfig mBedScreenConfig;

    public BedScreenTemplateRequestModel(Context context){
        mContext = context;
        mBedScreenConfig = new BedScreenConfig(context);
    }
    /**
     * 获取当前配置的模板
     */
    @Override
    public BedScreenTemplate getCurrentTemplate(){
        return mBedScreenConfig.getTemplate(mBedScreenConfig.getCurrentTemplateId());
    }

    /**
     * 根据模板id获取模板
     */
    @Override
    public BedScreenTemplate getTemplate(String templateId){
        return mBedScreenConfig.getTemplate(templateId);
    }

    /**
     * 保存模板配置
     */
    @Override
    public void saveTemplate(@NonNull SlaveTemplate bedScreenTemplate){
        mBedScreenConfig.setTemplate((BedScreenTemplate)bedScreenTemplate,bedScreenTemplate.getTemplateId());
    }

    /**
     * 设置当前工作的配置模板
     */
    @Override
    public BedScreenTemplate setCurrentTemplate(String templateId){
        mBedScreenConfig.setCurrentTemplateId(templateId);
        DeviceModel selfModel = WorkManager.getInstance().getSelfInfo();
        if (selfModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()) {
            //主机需要把这个配置推送给其它人
            RequestDTO<BedScreenTemplate> requestDTO = new RequestDTO<>(selfModel, CommandEnum.REQ_UPDATE_BED_TEMPLATE.getId());
            requestDTO.setData(mBedScreenConfig.getTemplate(templateId));
            WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
        }
        return getCurrentTemplate();
    }
}
