package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.R;
import com.hcs.android.business.constant.BedScreenFunctionEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.FunctionBo;
import com.hcs.android.business.entity.HotkeyBo;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BedScreenConfig extends DeviceConfig{

    public BedScreenConfig(Context context){
        super(context);
    }

    @Override
    public DeviceTypeEnum getDeviceType(){
        return DeviceTypeEnum.BED_SCREEN;
    }

    @Override
    public String getCurrentTemplateConfigId(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_CURRENT_CONFIG_ID_PREFIX + getDeviceType().getName(),mContext.getString(R.string.default_current_config_id_device_type_bed_screen));
    }

    @Override
    public String getCurrentTemplateId(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_CURRENT_TEMPLATE_ID_PREFIX + getDeviceType().getName(),mContext.getString(R.string.default_current_template_id_device_type_bed_screen));
    }

    /**
     * 屏保显示语
     */
    public String getScreenSaver(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_SCREEN_SAVER,getKeyIndex(""),mContext.getString(R.string.default_bed_screen_screen_saver));
    }
    public void setScreenSaver(String screenSaver){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_SCREEN_SAVER,getKeyIndex(""),screenSaver);
    }

    /**
     * 屏保触发时间
     */
    public Integer getScreenSaverTime(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_BED_SCREEN_SCREEN_SAVER_TIME,getKeyIndex("")
                ,mContext.getResources().getInteger(R.integer.default_bed_screen_screen_saver_time));
    }
    public void setScreenSaverTime(Integer screenSaverTime){
        if(screenSaverTime != null) {
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_SCREEN_SAVER_TIME, getKeyIndex(""), screenSaverTime);
        }
    }

    /**
     * 息屏时间
     */
    public String getScreenShutTime(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_SCREEN_SHUT_TIME,getKeyIndex("")
                ,mContext.getString(R.string.default_bed_screen_screen_shut_time));
    }
    public void setScreenShutTime(String screenShutTime){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_SCREEN_SHUT_TIME,getKeyIndex(""),screenShutTime);
    }

    /**
     * 功能列表
     */
    public List<FunctionBo> getFunctionList(){
        List<FunctionBo> functionBoList = new ArrayList<>();
        int functionCount = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_BED_SCREEN_FUNCTION_COUNT,getKeyIndex("")
                ,mContext.getResources().getInteger(R.integer.default_bed_screen_function_count));
        if(functionCount > 0){
            for(int i = 0; i < functionCount; i++){
                FunctionBo functionBo = new FunctionBo();
                functionBo.setFunctionId(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_BED_SCREEN_FUNCTION_ID_PREFIX,getKeyIndex(String.valueOf(i)), BedScreenFunctionEnum.findById(i).getValue()));
                functionBo.setFunctionName(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_FUNCTION_NAME_PREFIX,getKeyIndex(String.valueOf(i)),BedScreenFunctionEnum.findById(i).getDisplayName(mContext)));
                functionBoList.add(functionBo);
            }
        }
        return functionBoList;
    }
    public void setFunctionList(List<FunctionBo> functionList){
        int functionCount = 0;
        if(!StringUtil.isEmpty(functionList)){
            functionCount = functionList.size();
            for(int i = 0; i < functionList.size(); i++){
                FunctionBo functionBo = functionList.get(i);
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_FUNCTION_ID_PREFIX,getKeyIndex(String.valueOf(i)),functionBo.getFunctionId());
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_FUNCTION_NAME_PREFIX,getKeyIndex(String.valueOf(i)),functionBo.getFunctionName());
            }
        }
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_FUNCTION_COUNT,getKeyIndex(""),functionCount);
    }

    /**
     * 外接按键
     */
    public HotkeyBo getExternalHotkey(){
        HotkeyBo hotkeyBo = new HotkeyBo();
        hotkeyBo.setButtonId(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_BUTTON_ID,getKeyIndex(""),
                mContext.getString(R.string.default_bed_screen_hotkey_external_button_id)));
        hotkeyBo.setCallName(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_CALL_NAME,getKeyIndex(""),
                mContext.getString(R.string.default_bed_screen_hotkey_external_call_name)));
        hotkeyBo.setEmergencyCall(SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_EMERGENCY_CALL,getKeyIndex(""),
                mContext.getResources().getBoolean(R.bool.default_bed_screen_hotkey_external_is_emergency_call)));
        return hotkeyBo;
    }

    public void setExternalHotkey(@NonNull HotkeyBo hotkey){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_BUTTON_ID,getKeyIndex(""),hotkey.getButtonId());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_CALL_NAME,getKeyIndex(""),hotkey.getCallName());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_EXTERNAL_EMERGENCY_CALL,getKeyIndex(""),hotkey.isEmergencyCall());
    }

    /**
     * 手持按键
     */
    public HotkeyBo getInHandHotkey(){
        HotkeyBo hotkeyBo = new HotkeyBo();
        hotkeyBo.setButtonId(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_BUTTON_ID,getKeyIndex(""),
                mContext.getString(R.string.default_bed_screen_hotkey_in_hand_button_id)));
        hotkeyBo.setCallName(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_CALL_NAME,getKeyIndex(""),
                mContext.getString(R.string.default_bed_screen_hotkey_in_hand_call_name)));
        hotkeyBo.setEmergencyCall(SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_EMERGENCY_CALL,getKeyIndex(""),
                mContext.getResources().getBoolean(R.bool.default_bed_screen_hotkey_in_hand_is_emergency_call)));
        return hotkeyBo;
    }

    public void setInHandHotkey(@NonNull HotkeyBo hotkey){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_BUTTON_ID,getKeyIndex(""),hotkey.getButtonId());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_CALL_NAME,getKeyIndex(""),hotkey.getCallName());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_BED_SCREEN_HOTKEY_IN_HAND_EMERGENCY_CALL,getKeyIndex(""),hotkey.isEmergencyCall());
    }


    /**
     * 获取整体配置
     * @param templateId 配置模板id，当为空时表示获取当前配置
     */
    public BedScreenTemplate getTemplate(String templateId){
        if(!StringUtil.isEmpty(templateId)) {
            setTemplateId(templateId);
        }
        BedScreenTemplate bedScreenTemplate = new BedScreenTemplate();
        bedScreenTemplate.setThemeId(getThemeId());
        bedScreenTemplate.setTemplateId(templateId);
        bedScreenTemplate.setExternalHotkey(getExternalHotkey());
        bedScreenTemplate.setInHandHotkey(getInHandHotkey());
        bedScreenTemplate.setScreenSaver(getScreenSaver());
        bedScreenTemplate.setScreenSaverTime(getScreenSaverTime());
        bedScreenTemplate.setScreenShutTime(getScreenShutTime());
        bedScreenTemplate.setFunctionList(getFunctionList());
        bedScreenTemplate.setConfigId(getCurrentTemplateConfigId());
        return bedScreenTemplate;
    }

    /**
     * 整体配置
     * @param templateId 配置模板id，当为空时表示获取当前配置
     */
    public void setTemplate(BedScreenTemplate bedScreenTemplate,String templateId){
        if(StringUtil.isEmpty(templateId)){
            setCurrentTemplateId(bedScreenTemplate.getTemplateId());
            setTemplateId(bedScreenTemplate.getTemplateId());
        }else {
            setTemplateId(templateId);
        }
        setExternalHotkey(bedScreenTemplate.getExternalHotkey());
        setInHandHotkey(bedScreenTemplate.getInHandHotkey());
        setFunctionList(bedScreenTemplate.getFunctionList());
        setScreenSaver(bedScreenTemplate.getScreenSaver());
        setScreenSaverTime(bedScreenTemplate.getScreenSaverTime());
        setScreenShutTime(bedScreenTemplate.getScreenShutTime());
        setThemeId(bedScreenTemplate.getThemeId());
        setCurrentConfigId(bedScreenTemplate.getConfigId());
    }
}
