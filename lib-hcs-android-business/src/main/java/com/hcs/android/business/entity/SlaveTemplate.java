package com.hcs.android.business.entity;

public class SlaveTemplate {
    /**
     * 模板id
     */
    private String templateId;
    public String getTemplateId(){
        return templateId;
    }
    public void setTemplateId(String templateId){
        this.templateId = templateId;
    }


    /**
     * 主题（显示风格）id
     */
    private Integer themeId;

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    /**
     * 配置标识
     * （时间戳）
     */
    private String configId;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }
}
