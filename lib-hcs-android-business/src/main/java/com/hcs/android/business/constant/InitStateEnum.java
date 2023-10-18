package com.hcs.android.business.constant;

/**
 * 初始化状态枚举
 */
public enum InitStateEnum {
    /**
     * 还未初始化
     */
    NONE(0),
    /**
     * 初始化中
     */
    INITIALIZING(1),
    /**
     * 初始化完成
     */
    INITIALIZED(3),
    /**
     * 初始化失败
     */
    FAILED(4),
    /**
     * 分机号码不能为空
     */
    WAIT_PHONE_NO(5),
    /**
     * 主机号码不能为空
     */
    WAIT_PARENT_NO(6),
    /**
     * 等待网络设置
     */
    WAIT_NETWORK_CONFIG(7);
    /**
     * 枚举值
     */
    private int value;

    InitStateEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static InitStateEnum findById(int id){
        for(InitStateEnum filterEnum : InitStateEnum.values()){
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
