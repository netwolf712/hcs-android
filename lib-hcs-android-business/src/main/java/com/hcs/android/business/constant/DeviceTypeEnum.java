package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 设备类型枚举
 */
public enum DeviceTypeEnum {
    /**
     * 服务器
     */
    SERVICE(0,"device_type_service"),
    /**
     * 护士站主机
     */
    NURSE_STATION_MASTER(1,"device_type_nurse_station_master"),
    /**
     * 床头屏（床旁分机）
     */
    BED_SCREEN(2,"device_type_bed_screen"),
    /**
     * 病房门口屏（病房门口机）
     */
    ROOM_SCREEN(3,"device_type_room_screen"),
    /**
     * 病区门口机
     */
    DOOR_CONTROL(4,"device_type_door_control"),
    /**
     * 护士站大屏（病员信息一览表）
     */
    NURSE_STATION_PANEL(5,"device_type_nurse_station_panel"),
    /**
     * 走廊屏
     */
    CORRIDOR_SCREEN(6,"device_type_corridor_screen"),
    /**
     * 用户通过web发送的请求
     */
    WEB(7,"device_type_web");

    /**
     * 设备名称对应的字符资源id
     */
    private String name;

    /**
     * 枚举值
     */
    private int value;

    DeviceTypeEnum(int value,String name){
        this.value = value;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static DeviceTypeEnum findById(int id){
        for(DeviceTypeEnum filterEnum : DeviceTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return SERVICE;
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
