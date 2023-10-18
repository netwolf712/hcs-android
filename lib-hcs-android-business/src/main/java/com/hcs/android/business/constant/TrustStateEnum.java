package com.hcs.android.business.constant;

/**
 * 托管状态枚举
 */
public enum TrustStateEnum {
    /**
     * 没有托管
     */
    NONE(0),
    /**
     * 请求开始托管
     */
    REQUEST_START_TRUST(1),
    /**
     * 托管中
     */
    TRUSTING(2),
    /**
     * 请求结束托管
     */
    REQUEST_STOP_TRUST(3);
    /**
     * 枚举值
     */
    private final int value;

    TrustStateEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static TrustStateEnum findById(int id){
        for(TrustStateEnum filterEnum : TrustStateEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return NONE;
    }

    public int getValue(){
        return value;
    }
}
