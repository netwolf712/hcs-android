package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * TTS播放速度枚举
 */
public enum TtsSpeedEnum {
    /**
     * 慢
     */
    SLOW(0,0.5f,"tts_speed_slow"),
    /**
     * 正常
     */
    NORMAL(1,1.0f,"tts_speed_normal"),
    /**
     * 快
     */
    FAST(2,1.5f,"tts_speed_fast");
    /**
     * 枚举值
     */
    private final int value;

    private final float speed;

    /**
     * 类型名称
     */
    private final String name;
    TtsSpeedEnum(int value,float speed,String name){
        this.value = value;
        this.speed = speed;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static TtsSpeedEnum findById(int id){
        for(TtsSpeedEnum filterEnum : TtsSpeedEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return NORMAL;
    }

    public int getValue(){
        return value;
    }
    public float getSpeed(){
        return speed;
    }

    /**
     * 根据传入的语速判断属于哪个档次
     */
    public static TtsSpeedEnum parseSpeed(float speed){
        if(speed <= (SLOW.speed + (NORMAL.speed - SLOW.speed) / 2)){
            return SLOW;
        }else if(speed > (SLOW.speed + (NORMAL.speed - SLOW.speed)) / 2 && speed <= (NORMAL.speed + (FAST.speed - NORMAL.speed) / 2)){
            return NORMAL;
        }else{
            return FAST;
        }
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
