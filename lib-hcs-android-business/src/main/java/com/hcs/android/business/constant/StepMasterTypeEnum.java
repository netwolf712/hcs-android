package com.hcs.android.business.constant;

/**
 * 继任主机类型枚举
 * 不是指硬件型号
 * 而是主机与其它设备间的关系
 */
public enum StepMasterTypeEnum {
    /**
     * 上级主机
     */
    SUPERIOR(1),
    /**
     * 附加主机
     */
    APPEND(2),
    /**
     * 增援主机
     */
    REINFORCE(3),
    /**
     * 托管主机
     */
    TRUST(4),
    /**
     * 被托管的主机
     */
    BE_TRUST(5);
    /**
     * 枚举值
     */
    private final int value;

    StepMasterTypeEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static StepMasterTypeEnum findById(int id){
        for(StepMasterTypeEnum filterEnum : StepMasterTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return SUPERIOR;
    }

    public int getValue(){
        return value;
    }
}
