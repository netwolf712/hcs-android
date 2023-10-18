package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 呼叫所使用的媒体类型
 */
public enum CallMediaTypeEnum {
    /**
     * 数据流
     * 音频、视频
     */
    STREAM(1),
    /**
     * 消息
     * 报文
     */
    MESSAGE(2);
    /**
     * 枚举值
     */
    private final int value;

    CallMediaTypeEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static CallMediaTypeEnum findById(int id){
        for(CallMediaTypeEnum filterEnum : CallMediaTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return STREAM;
    }

    public int getValue(){
        return value;
    }
}
