package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 呼叫类型枚举
 */
public enum CallTypeEnum {
    /**
     * 普通呼叫
     */
    NORMAL(1,"call_type_normal",CallMediaTypeEnum.STREAM),
    /**
     * 吸氧
     */
    OXYGEN_INHALATION(2,"call_type_oxygen_inhalation",CallMediaTypeEnum.MESSAGE),
    /**
     * 用药
     */
    MEDICATION(3,"call_type_medication",CallMediaTypeEnum.MESSAGE),
    /**
     * 输液
     */
    INFUSION(4,"call_type_infusion",CallMediaTypeEnum.MESSAGE),
    /**
     * 皮试
     */
    SHIN_TEST(5,"call_type_shin_test",CallMediaTypeEnum.MESSAGE),
    /**
     * 护理
     */
    NURSING(6,"call_type_nursing",CallMediaTypeEnum.MESSAGE),
    /**
     * blue code呼叫
     */
    BLUE_CODE(7,"call_type_blue_code",CallMediaTypeEnum.MESSAGE),
    /**
     * 呼叫增援
     */
    REINFORCE(8,"call_type_reinforce",CallMediaTypeEnum.MESSAGE),
    /**
     * 紧急呼叫
     */
    EMERGENCY(9,"call_type_emergency",CallMediaTypeEnum.MESSAGE),
    /**
     * 视频通话
     */
    VIDEO_CALL(10,"call_type_video",CallMediaTypeEnum.STREAM);
    /**
     * 对应的字符资源id
     */
    private final String name;

    /**
     * 枚举值
     */
    private final int value;

    /**
     * 此呼叫使用的媒体类型
     */
    private final CallMediaTypeEnum mediaType;

    CallTypeEnum(int value, String name,CallMediaTypeEnum mediaType){
        this.value = value;
        this.name = name;
        this.mediaType = mediaType;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static CallTypeEnum findById(int id){
        for(CallTypeEnum filterEnum : CallTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return NORMAL;
    }

    /**
     * 返回该设备类型的文字说明
     */
    public String getName(){
        return name;
    }

    public String getDisplayName(@NonNull Context context){
        return context.getString(ResourceUtil.getStringId(context,name));
    }

    public int getValue(){
        return value;
    }

    public CallMediaTypeEnum getMediaType(){
        return mediaType;
    }
}
