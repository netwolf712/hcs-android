package com.hcs.android.business.manager;

import android.content.Context;

import com.hcs.android.business.R;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.SlaveTemplate;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.StringUtil;

/**
 * 设备配置管理器
 */
public abstract class DeviceConfig {

    /**
     * 模板id
     * 读取配置模板时有用
     */
    private String templateId = "";
    public void setTemplateId(String templateId){
        this.templateId = templateId;
    }
    public String getTemplateId(){
        return templateId;
    }
    /**
     * 默认的配置、模板id
     */
    private final static String DEFAULT_CONFIG_ID = "0";
    private final static String DEFAULT_TEMPLATE_ID = "0";

    protected final Context mContext;
    public DeviceConfig(Context context){
        mContext = context;
    }

    /**
     * 获取设备类型
     */
    public abstract DeviceTypeEnum getDeviceType();

    public String getCurrentTemplateConfigId(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_CURRENT_CONFIG_ID_PREFIX + getDeviceType().getName(),DEFAULT_CONFIG_ID);
    }
    public void setCurrentConfigId(String configId){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_CURRENT_CONFIG_ID_PREFIX + getDeviceType().getName(),configId);
    }

    public String getCurrentTemplateId(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_CURRENT_TEMPLATE_ID_PREFIX + getDeviceType().getName(),DEFAULT_TEMPLATE_ID);
    }
    public void setCurrentTemplateId(String templateId){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_CURRENT_TEMPLATE_ID_PREFIX + getDeviceType().getName(),templateId);
    }

    public String getKeyIndex(String index){
        if(StringUtil.isEmpty(templateId)){
            return index;
        }else{
            return "template_" + templateId + "_" + index;
        }
    }

    /**
     * 获取主题id
     */
    public Integer getThemeId(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_THEME,getKeyIndex("")
                ,mContext.getResources().getInteger(R.integer.default_theme));
    }
    public void setThemeId(Integer themeId){
        if(themeId != null) {
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_THEME, getKeyIndex(""), themeId);
        }
    }

}
