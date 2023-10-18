package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 门口屏功能列表
 */
public enum RoomScreenFunctionEnum {
    /**
     * 呼叫解除
     */
    CALL_CLEAR(0,"call_clear"),
    /**
     * 呼叫护士
     */
    CALL_NURSE(1,"call_nurse"),
    /**
     * 进入护理
     */
    IN_NURSING(2,"in_nursing");

    private final String name;

    /**
     * 枚举值
     */
    private final int value;

    RoomScreenFunctionEnum(int value, String name){
        this.value = value;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static RoomScreenFunctionEnum findById(int id){
        for(RoomScreenFunctionEnum filterEnum : RoomScreenFunctionEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return CALL_CLEAR;
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
}
