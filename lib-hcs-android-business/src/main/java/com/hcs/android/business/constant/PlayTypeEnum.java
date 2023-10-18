package com.hcs.android.business.constant;

/**
 * 播放方式枚举
 */
public enum PlayTypeEnum {
    /**
     * 顺序播放
     */
    IN_TURN(0),
    /**
     * 顺序循环播放
     */
    LOOP_IN_TURN(1),
    /**
     * 单曲循环播放
     */
    LOOP_ONE(2),
    /**
     * 随机循环播放
     */
    LOOP_RANDOM(3);
    /**
     * 枚举值
     */
    private int value;

    PlayTypeEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static PlayTypeEnum findById(int id){
        for(PlayTypeEnum filterEnum : PlayTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return IN_TURN;
    }

    public int getValue(){
        return value;
    }
}
