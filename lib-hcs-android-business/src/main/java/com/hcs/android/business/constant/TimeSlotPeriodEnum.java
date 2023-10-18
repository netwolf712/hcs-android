package com.hcs.android.business.constant;

/**
 * 时间段工作周期枚举
 */
public enum TimeSlotPeriodEnum {
    /**
     * 星期天
     */
    SUNDAY(0),
    /**
     * 星期一
     */
    MONDAY(1),
    /**
     * 星期二
     */
    TUESDAY(2),
    /**
     * 星期三
     */
    WEDNESDAY(3),
    /**
     * 星期四
     */
    THURSDAY(4),
    /**
     * 星期五
     */
    FRIDAY(5),
    /**
     * 星期六
     */
    SATURDAY(6);
    /**
     * 枚举值
     */
    private final int value;

    TimeSlotPeriodEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static TimeSlotPeriodEnum findById(int id){
        for(TimeSlotPeriodEnum filterEnum : TimeSlotPeriodEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return MONDAY;
    }

    public int getValue(){
        return value;
    }
}
