package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 设备状态枚举
 */
public enum StateEnum {
    /**
     * 离线
     */
    UNKNOWN(-1,"device_unknown"),
    /**
     * 离线
     */
    OFFLINE(0,"device_state_offline"),
    /**
     * 在线
     */
    ONLINE(1,"device_state_online"),
    /**
     * 通话中
     */
    TALKING(2,"device_state_talking"),
    /**
     * 呼叫中（尚未接通）
     */
    CALLING(3,"device_state_calling"),
    /**
     * 呼叫结束
     */
    CALL_END(4,"device_state_call_end"),
    /**
     * 计时开始
     */
    CALL_TIMING_START(5,"device_state_call_timing_start"),
    /**
     * 请求配置（分机刚启动时需要等待请求配置）
     */
    REQ_CONFIG(6,"device_state_req_config"),
    /**
     * 初始化中
     */
    INIT(7,"device_state_init");
    /**
     * 对应的字符资源id
     */
    private final String name;

    /**
     * 枚举值
     */
    private final int value;

    StateEnum(int value, String name){
        this.value = value;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static StateEnum findById(int id){
        for(StateEnum filterEnum : StateEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return OFFLINE;
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
