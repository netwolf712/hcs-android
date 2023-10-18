package com.hcs.android.business.constant;

/**
 * 确认类型枚举
 * 对应rsp-get-config
 */
public enum ConfirmTypeEnum {
    /**
     * 正确
     */
    OK(0),
    /**
     * 错误的床号
     */
    WRONG_BED(1),
    /**
     * 错误的配置请求
     */
    WRONG_CONFIG_REQ(2),
    /**
     * 错误的门口号
     */
    WRONG_ROOM(3);

    /**
     * 枚举值
     */
    private final int value;

    ConfirmTypeEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static ConfirmTypeEnum findById(int id){
        for(ConfirmTypeEnum filterEnum : ConfirmTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return OK;
    }

    public int getValue(){
        return value;
    }
}
