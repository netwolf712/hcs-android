package com.hcs.android.business.entity;
/**
 * 请求发送语音广播
 */
public class RequestAudioMulticast {
    /**
     * 要进行广播的分区
     */
    private Integer multicastGroup;

    public Integer getMulticastGroup() {
        return multicastGroup;
    }

    public void setMulticastGroup(Integer multicastGroup) {
        this.multicastGroup = multicastGroup;
    }

    /**
     * 广播地址
     */
    private String listenIP;

    public String getListenIP() {
        return listenIP;
    }

    public void setListenIP(String listenIP) {
        this.listenIP = listenIP;
    }

    /**
     * 广播端口
     */
    private Integer listenPort;

    public Integer getListenPort() {
        return listenPort;
    }

    public void setListenPort(Integer listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * 是否要求分机播放准备开始广播提示音，true是，false否
     */
    private boolean playReadyCode;

    public boolean isPlayReadyCode() {
        return playReadyCode;
    }

    public void setPlayReadyCode(boolean playReadyCode) {
        this.playReadyCode = playReadyCode;
    }

    /**
     * 编码类型（可忽略，统一指定编码格式）
     */
    private String codecType;

    public String getCodecType() {
        return codecType;
    }

    public void setCodecType(String codecType) {
        this.codecType = codecType;
    }



    /**
     * 是否正在播放音频
     */
    public boolean playing;
    public boolean isPlaying() {
        return playing;
    }
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * 是否正在讲话
     */
    private boolean talking;
    public boolean isTalking() {
        return talking;
    }

    public void setTalking(boolean talking) {
        this.talking = talking;
    }
}
