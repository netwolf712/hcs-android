package com.hcs.android.business.constant;

public enum WorkMessageEnum {
    /**
     * 消息枚举开始
     * 无实际业务意义
     */
    MESSAGE_START(0),
    /**
     * 初始化成功
     */
    MESSAGE_LOGIN_OK(1),
    /**
     * linphone初始化成功
     */
    MESSAGE_LINPHONE_OK(2),
    /**
     * 获取设备列表成功
     */
    READ_DEVICE_LIST_OK(3),
    /**
     * 发送广播数据
     */
    SEND_MULTICAST(4),
    /**
     * 更新linphone好友
     */
    UPDATE_LINPHONE_FRIEND(5),
    /**
     * 消息枚举结束
     * 无实际业务意义
     */
    MESSAGE_END(999);

    WorkMessageEnum(int message){
        this.message = message;
    }
    private int message;
    public int getMessage(){
        return message;
    }

    /**
     * 通过id查找枚举
     * @param message 消息id
     * @return 枚举
     */
    public static WorkMessageEnum findByInt(int message){
        for(WorkMessageEnum workMessageEnum :WorkMessageEnum.values()){
            if(message == workMessageEnum.getMessage()){
                return workMessageEnum;
            }
        }
        return MESSAGE_START;
    }
}
