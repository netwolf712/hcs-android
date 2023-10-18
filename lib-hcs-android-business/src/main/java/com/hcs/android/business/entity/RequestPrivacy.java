package com.hcs.android.business.entity;

import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 隐私设置
 */
public class RequestPrivacy {
    /**
     * 是否启用隐私政策
     */
    private boolean enable;

    /**
     * 启用隐私政策的设备类型
     * 多个设备类型间用半角,隔开
     */
    private String deviceTypes;

    /**
     * 隐私处理的开始位置
     */
    private Integer hideStart;

    /**
     * 隐藏字数
     */
    private Integer hideLength;

    /**
     * 用于隐藏替换的字符
     */
    private String replaceCharacter;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(String deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public Integer getHideStart() {
        return hideStart;
    }

    public void setHideStart(Integer hideStart) {
        this.hideStart = hideStart;
    }

    public Integer getHideLength() {
        return hideLength;
    }

    public void setHideLength(Integer hideLength) {
        this.hideLength = hideLength;
    }

    public String getReplaceCharacter() {
        return replaceCharacter;
    }

    public void setReplaceCharacter(String replaceCharacter) {
        this.replaceCharacter = replaceCharacter;
    }

    /**
     * 将deviceTypes转换为枚举列表
     */
    public List<DeviceTypeEnum> getDeviceTypeList(){
        if(StringUtil.isEmpty(deviceTypes)){
            return null;
        }
        List<Object> objList = StringUtil.CutStringWithChar(deviceTypes,',');
        if(!StringUtil.isEmpty(objList)){
            List<DeviceTypeEnum> deviceTypeEnumList = new ArrayList<>();
            for(Object obj : objList){
                try {
                    deviceTypeEnumList.add(DeviceTypeEnum.findById(Integer.parseInt(obj.toString())));
                }catch (Exception e){
                    KLog.e(e);
                }
            }
            return deviceTypeEnumList;
        }
        return null;
    }

    /**
     * 将枚举值转换为字符串
     */
    public String formatDeviceTypeList(List<DeviceTypeEnum> deviceTypeEnumList){
        String tmpStr = "";
        if(!StringUtil.isEmpty(deviceTypeEnumList)){
            for(DeviceTypeEnum deviceTypeEnum : deviceTypeEnumList){
                if(!StringUtil.isEmpty(tmpStr)){
                    tmpStr += ",";
                }
                tmpStr += String.valueOf(deviceTypeEnum.getValue());
            }
        }
        return tmpStr;
    }
}
