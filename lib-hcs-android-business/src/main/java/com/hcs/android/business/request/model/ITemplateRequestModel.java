package com.hcs.android.business.request.model;

import android.content.Context;

import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.SlaveTemplate;
import com.hcs.android.business.manager.DeviceConfig;

/**
 * 各种设备的模板请求基类
 * 开放给主机调用的接口
 */
public interface ITemplateRequestModel {
    /**
     * 获取当前在用的模板
     */
    SlaveTemplate getCurrentTemplate();

    /**
     * 根据模板id获取模板
     */
    SlaveTemplate getTemplate(String templateId);

    /**
     * 保存模板配置
     */
    void saveTemplate(SlaveTemplate slaveTemplate);

    /**
     * 设置当前工作的配置模板
     */
    SlaveTemplate setCurrentTemplate(String templateId);
}
