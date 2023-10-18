package com.hcs.android.business.entity;

/**
 * 音量设置
 */
public class RequestVolumeSet {
    /**
     * 主机麦克风白天的音量
     */
    private Integer masterSpeakerDayVolume;

    /**
     * 主机振铃音白天的音量
     */
    private Integer masterRingDayVolume;

    /**
     * 主机按键白天的音量
     */
    private Integer masterKeyDayVolume;

    /**
     * 分机麦克风白天的音量
     */
    private Integer slaveSpeakerDayVolume;

    /**
     * 是否启用夜晚音量设置
     */
    private boolean nightVolumeSetEnable;

    /**
     * 主机麦克风夜晚的音量
     */
    private Integer masterSpeakerNightVolume;

    /**
     * 主机振铃音夜晚的音量
     */
    private Integer masterRingNightVolume;

    /**
     * 主机按键夜晚的音量
     */
    private Integer masterKeyNightVolume;

    /**
     * 分机麦克风夜晚的音量
     */
    private Integer slaveSpeakerNightVolume;

    public Integer getMasterSpeakerDayVolume() {
        return masterSpeakerDayVolume;
    }

    public void setMasterSpeakerDayVolume(Integer masterSpeakerDayVolume) {
        this.masterSpeakerDayVolume = masterSpeakerDayVolume;
    }

    public Integer getMasterRingDayVolume() {
        return masterRingDayVolume;
    }

    public void setMasterRingDayVolume(Integer masterRingDayVolume) {
        this.masterRingDayVolume = masterRingDayVolume;
    }

    public Integer getMasterKeyDayVolume() {
        return masterKeyDayVolume;
    }

    public void setMasterKeyDayVolume(Integer masterKeyDayVolume) {
        this.masterKeyDayVolume = masterKeyDayVolume;
    }

    public Integer getSlaveSpeakerDayVolume() {
        return slaveSpeakerDayVolume;
    }

    public void setSlaveSpeakerDayVolume(Integer slaveSpeakerDayVolume) {
        this.slaveSpeakerDayVolume = slaveSpeakerDayVolume;
    }

    public boolean isNightVolumeSetEnable() {
        return nightVolumeSetEnable;
    }

    public void setNightVolumeSetEnable(boolean nightVolumeSetEnable) {
        this.nightVolumeSetEnable = nightVolumeSetEnable;
    }

    public Integer getMasterSpeakerNightVolume() {
        return masterSpeakerNightVolume;
    }

    public void setMasterSpeakerNightVolume(Integer masterSpeakerNightVolume) {
        this.masterSpeakerNightVolume = masterSpeakerNightVolume;
    }

    public Integer getMasterRingNightVolume() {
        return masterRingNightVolume;
    }

    public void setMasterRingNightVolume(Integer masterRingNightVolume) {
        this.masterRingNightVolume = masterRingNightVolume;
    }

    public Integer getMasterKeyNightVolume() {
        return masterKeyNightVolume;
    }

    public void setMasterKeyNightVolume(Integer masterKeyNightVolume) {
        this.masterKeyNightVolume = masterKeyNightVolume;
    }

    public Integer getSlaveSpeakerNightVolume() {
        return slaveSpeakerNightVolume;
    }

    public void setSlaveSpeakerNightVolume(Integer slaveSpeakerNightVolume) {
        this.slaveSpeakerNightVolume = slaveSpeakerNightVolume;
    }
}
