package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 位置类型枚举
 */
public enum PlaceTypeEnum {
    /**
     * 整个医院
     */
    HOSPITAL(-1,"hospital"),
    /**
     * 病区
     */
    SECTION(0,"section"),
    /**
     * 病房
     */
    ROOM(1,"room"),
    /**
     * 病床
     */
    BED(2,"bed"),
    /**
     * 走廊
     */
    CORRIDOR(3,"corridor"),
    /**
     * 卫生间
     */
    WASH_ROOM(4,"wash_room"),
    /**
     * 工作站（护士台、医生办公室）
     */
    STATION(5,"station");
    /**
     * 枚举值
     */
    private final int value;

    /**
     * 类型名称
     */
    private final String name;

    PlaceTypeEnum(int value,String name){
        this.value = value;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static PlaceTypeEnum findById(int id){
        for(PlaceTypeEnum filterEnum : PlaceTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return BED;
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
