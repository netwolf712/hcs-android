package com.hcs.android.business.constant;

/**
 * 时间段工作类型枚举
 */
public enum TimeSlotTypeEnum {
    /**
     * 上级主机禁止上传呼叫的时间段
     */
    SUPERIOR_FORBID_UPLOAD_CALL(1),
    /**
     * 主动托管给托管主机的时间段
     */
    AUTO_TRUST(2),
    /**
     * 门禁常开的时间段
     */
    DOOR_OPEN(3),
    /**
     * 夜晚时间段
     */
    NIGHT(4),
    /**
     * 自动音频广播
     */
    AUTO_AUDIO_MULTICAST(5);
    /**
     * 枚举值
     */
    private final int value;

    TimeSlotTypeEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static TimeSlotTypeEnum findById(int id){
        for(TimeSlotTypeEnum filterEnum : TimeSlotTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return SUPERIOR_FORBID_UPLOAD_CALL;
    }

    public int getValue(){
        return value;
    }
}
