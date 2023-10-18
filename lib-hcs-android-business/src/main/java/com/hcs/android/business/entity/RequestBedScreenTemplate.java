package com.hcs.android.business.entity;

/**
 * 请求获取床头屏的模板信息
 */
public class RequestBedScreenTemplate {
    /**
     * 模板id
     * 为空表示获取当前正在使用的模板
     */
    private String templateId;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
