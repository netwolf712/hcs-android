package com.hcs.android.business.manager;

import android.content.Context;


import com.hcs.android.business.R;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.RoomScreenFunctionEnum;
import com.hcs.android.business.entity.FunctionBo;
import com.hcs.android.business.entity.RoomScreenTemplate;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RoomScreenConfig extends DeviceConfig{

    public RoomScreenConfig(Context context){
        super(context);
    }

    @Override
    public DeviceTypeEnum getDeviceType(){
        return DeviceTypeEnum.ROOM_SCREEN;
    }

    @Override
    public String getCurrentTemplateConfigId(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_CURRENT_CONFIG_ID_PREFIX + getDeviceType().getName(),mContext.getString(R.string.default_current_config_id_device_type_room_screen));
    }

    @Override
    public String getCurrentTemplateId(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_CURRENT_TEMPLATE_ID_PREFIX + getDeviceType().getName(),mContext.getString(R.string.default_current_template_id_device_type_room_screen));
    }

    /**
     * 屏保显示语
     */
    public String getScreenSaver(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_ROOM_SCREEN_SCREEN_SAVER,getKeyIndex(""),mContext.getString(R.string.default_room_screen_screen_saver));
    }
    public void setScreenSaver(String screenSaver){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_ROOM_SCREEN_SCREEN_SAVER,getKeyIndex(""),screenSaver);
    }

    /**
     * 屏保触发时间
     */
    public Integer getScreenSaverTime(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_ROOM_SCREEN_SCREEN_SAVER_TIME,getKeyIndex("")
                ,mContext.getResources().getInteger(R.integer.default_room_screen_screen_saver_time));
    }
    public void setScreenSaverTime(Integer screenSaverTime){
        if(screenSaverTime != null) {
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_ROOM_SCREEN_SCREEN_SAVER_TIME, getKeyIndex(""), screenSaverTime);
        }
    }

    /**
     * 息屏时间
     */
    public String getScreenShutTime(){
        return SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_ROOM_SCREEN_SCREEN_SHUT_TIME,getKeyIndex("")
                ,mContext.getString(R.string.default_room_screen_screen_shut_time));
    }
    public void setScreenShutTime(String screenShutTime){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_ROOM_SCREEN_SCREEN_SHUT_TIME,getKeyIndex(""),screenShutTime);
    }

    /**
     * 功能列表
     */
    public List<FunctionBo> getFunctionList(){
        List<FunctionBo> functionBoList = new ArrayList<>();
        int functionCount = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_ROOM_SCREEN_FUNCTION_COUNT,getKeyIndex("")
                ,mContext.getResources().getInteger(R.integer.default_room_screen_function_count));
        if(functionCount > 0){
            for(int i = 0; i < functionCount; i++){
                FunctionBo functionBo = new FunctionBo();
                functionBo.setFunctionId(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_ROOM_SCREEN_FUNCTION_ID_PREFIX,getKeyIndex(String.valueOf(i)), RoomScreenFunctionEnum.findById(i).getValue()));
                functionBo.setFunctionName(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_ROOM_SCREEN_FUNCTION_NAME_PREFIX,getKeyIndex(String.valueOf(i)),RoomScreenFunctionEnum.findById(i).getDisplayName(mContext)));
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
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_ROOM_SCREEN_FUNCTION_ID_PREFIX,getKeyIndex(String.valueOf(i)),functionBo.getFunctionId());
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_ROOM_SCREEN_FUNCTION_NAME_PREFIX,getKeyIndex(String.valueOf(i)),functionBo.getFunctionName());
            }
        }
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_ROOM_SCREEN_FUNCTION_COUNT,getKeyIndex(""),functionCount);
    }


    /**
     * 获取整体配置
     * @param templateId 配置模板id，当为空时表示获取当前配置
     */
    public RoomScreenTemplate getTemplate(String templateId){
        if(!StringUtil.isEmpty(templateId)) {
            setTemplateId(templateId);
        }
        RoomScreenTemplate roomScreenTemplate = new RoomScreenTemplate();
        roomScreenTemplate.setThemeId(getThemeId());
        roomScreenTemplate.setTemplateId(templateId);
        roomScreenTemplate.setScreenSaver(getScreenSaver());
        roomScreenTemplate.setScreenSaverTime(getScreenSaverTime());
        roomScreenTemplate.setScreenShutTime(getScreenShutTime());
        roomScreenTemplate.setFunctionList(getFunctionList());
        roomScreenTemplate.setConfigId(getCurrentTemplateConfigId());
        return roomScreenTemplate;
    }

    /**
     * 整体配置
     * @param templateId 配置模板id，当为空时表示获取当前配置
     */
    public void setTemplate(RoomScreenTemplate roomScreenTemplate,String templateId){
        if(StringUtil.isEmpty(templateId)){
            setCurrentTemplateId(roomScreenTemplate.getTemplateId());
            setTemplateId(roomScreenTemplate.getTemplateId());
        }else {
            setTemplateId(templateId);
        }
        setFunctionList(roomScreenTemplate.getFunctionList());
        setScreenSaver(roomScreenTemplate.getScreenSaver());
        setScreenSaverTime(roomScreenTemplate.getScreenSaverTime());
        setScreenShutTime(roomScreenTemplate.getScreenShutTime());
        setThemeId(roomScreenTemplate.getThemeId());
        setCurrentConfigId(roomScreenTemplate.getConfigId());
    }
}
