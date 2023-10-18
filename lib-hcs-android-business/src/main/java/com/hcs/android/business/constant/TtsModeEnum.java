package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * TTS内容组装形式枚举
 */
public enum TtsModeEnum {
    /**
     * 无TTS
     */
    NONE(0,"tts_mode_none"),
    /**
     * 播放位置
     */
    PLACE_NAME(1,"tts_mode_place_name"),
    /**
     * 播放患者姓名
     */
    PATIENT_NAME(2,"tts_mode_patient_name"),
    /**
     * 播放分机号
     */
    PHONE_NUMBER(3,"tts_mode_phone_number");
    /**
     * 枚举值
     */
    private final int value;

    /**
     * 类型名称
     */
    private final String name;

    TtsModeEnum(int value,String name){
        this.value = value;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static TtsModeEnum findById(int id){
        for(TtsModeEnum filterEnum : TtsModeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return NONE;
    }

    public int getValue(){
        return value;
    }

    public String getName(){
        return name;
    }

    /**
     * 返回该类型的文字说明
     */
    public String getDisplayName(@NonNull Context context){
        return context.getString(ResourceUtil.getStringId(context,name.replaceAll("-","_")));
    }
}
