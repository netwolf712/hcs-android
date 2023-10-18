package com.hcs.android.ui.player;

/**
 * 播放状态
 */
public enum PlayStatusEnum {
    /**
     * 空闲（待命）中
     */
    STANDBY,
    /**
     * 准备中（还不能开始播放）
     */
    PREPARING,
    /**
     * 已经准备完毕（可以开始播放了）
     */
    PREPARED,
    /**
     * 播放中
     */
    PLAYING,
    /**
     * 已经暂停
     */
    PAUSED,
    /**
     * 已停止
     */
    STOPPED;
    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static PlayStatusEnum findById(int id){
        for(PlayStatusEnum alignEnum : PlayStatusEnum.values()){
            if(id == alignEnum.ordinal()){
                return alignEnum;
            }
        }
        return STANDBY;
    }
}
