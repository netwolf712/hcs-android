package com.hcs.android.business.constant;

/**
 * 继任主机优先级枚举
 * 不是指硬件型号
 * 而是主机与其它设备间的关系
 */
public enum StepMasterLevelEnum {
    /**
     * 一般等级
     */
    NORMAL(0),
    /**
     * 等级1
     */
    LEVEL1(1),
    /**
     * 等级2
     */
    LEVEL2(2),
    /**
     * 等级3
     */
    LEVEL3(3),
    /**
     * 等级4
     */
    LEVEL4(4),
    /**
     * 等级5
     */
    LEVEL5(5);
    /**
     * 枚举值
     */
    private final int value;

    StepMasterLevelEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static StepMasterLevelEnum findById(int id){
        for(StepMasterLevelEnum filterEnum : StepMasterLevelEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return NORMAL;
    }

    public int getValue(){
        return value;
    }
}
