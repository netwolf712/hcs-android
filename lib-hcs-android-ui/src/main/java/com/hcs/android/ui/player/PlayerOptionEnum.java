package com.hcs.android.ui.player;


import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器默认配置
 */
public enum PlayerOptionEnum {

    /**
     * 是否开启MediaCodec
     * mediacodec
     * 默认true
     */
    MEDIACODEC(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"mediacodec","1",Long.class),

    /**
     * 是否使用MediaCodec的自动翻转功能
     * mediacodec-auto-rotate
     * 默认true
     */
    AUTO_ROTATE(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"mediacodec-auto-rotate","1",Long.class),

    /**
     * 是否处理分辨率改变事件
     * mediacodec-handle-resolution-change
     * 默认true
     */
    HANDLE_RESOULUTION_CHANGE(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"mediacodec-handle-resolution-change","1",Long.class),

    /**
     * 色彩格式
     * overlay-format
     * 默认：SDL_FCC_RV32
     */
    //OVERLAY_FORMAT(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"overlay-format",String.valueOf(IjkMediaPlayer.SDL_FCC_RV32),Long.class),

    /**
     * 是否开启丢帧功能
     * framedrop
     * 默认true
     */
    FRAMEDROP(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framedrop","1",Long.class),

    /**
     * 在准备好后自动开始播放
     * start-on-prepared
     * 默认false
     */
    START_ON_PREPARED(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"start-on-prepared","0",Long.class),

    //HTTP_DETECT_RANGE_SUPPORT(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"http-detect-range-support","0",Long.class),

    /**
     * 对指定帧跳过环路滤波
     * skip_loop_filter
     * 默认48
     */
    //SKIP_LOOP_FILTER(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"skip_loop_filter","48",Long.class),

    /**
     * 设置http请求时的user-agent
     * 默认sdplayer
     */
    USER_AGENT(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"user-agent","sdplayer",String.class),

    /**
     * 链接超时时间，单位毫秒
     * timeout
     * 默认30000
     */
    //TIMEOUT(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"timeout","30000",Long.class),

    /**
     * 是否自动重连
     * reconnect
     * 默认true
     */
    //RECONNECT(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect","1",Long.class),

    /**
     * 最大帧率
     * max-fps
     * 默认30
     */
    //MAX_FPS(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"max-fps","30",Long.class),

    /**
     * 是否关闭音频
     * an
     * 默认false
     */
    AN(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"an","0",Long.class),

    /**
     * 是否关闭视频
     * vn
     * 默认false
     */
    VN(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"vn","0",Long.class),

    /**
     * 循环次数
     * loop
     * 原来0表示永远循环，尴尬了。
     * 给了具体数值之后才表示实际的播放次数
     * 所以，默认1，表示只播放一次，循环由应用程序控制
     */
    LOOP(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"loop","1",Long.class),

    /**
     * 音频处理使用AudioTrack还是OpenSLES（没错，是OpenSL ES：Open Sound Library for Embedded Systems）
     * opensles
     * 默认0：audioTrack
     */
    AUDIO_TRACK_OR_OPEN_SL_ES(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"opensles","0",Long.class),

    /**
     * 视频源格式
     * format
     * 默认为空，表示自动识别
     * 当为usb摄像头时需要为v4l2,usb声卡时需要alsa
     */
    FORMAT(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"format","",String.class);

    /**
     * 属于哪个大类的配置
     */
    private Integer category;

    /**
     * 配置名称
     */
    private String name;

    /**
     * 默认配置值
     */
    private String defaultValue;

    /**
     * 配置类型
     */
    private Class<?> valueType;

    PlayerOptionEnum(Integer category,String name,String defaultValue,Class<?> valueType){
        this.category = category;
        this.name = name;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
    }

    /**
     * 通过名称查找枚举
     * @param name 名称
     * @return 枚举
     */
    public static PlayerOptionEnum findByName(String name){
        for(PlayerOptionEnum playerOptionEnum : PlayerOptionEnum.values()){
            if(playerOptionEnum.name.equalsIgnoreCase(name)){
                return playerOptionEnum;
            }
        }
        return null;
    }

    public Integer getCategory(){
        return category;
    }

    public String getName(){
        return name;
    }

    public String getDefaultValue(){
        return defaultValue;
    }

    public Class<?> getValueType(){
        return valueType;
    }
}
