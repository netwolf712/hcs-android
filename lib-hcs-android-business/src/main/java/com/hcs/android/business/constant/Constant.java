package com.hcs.android.business.constant;

/**
 * 一些常用的常量
 */
public class Constant {
    /**
     * web服务器的地址
     */
    public static final String SERVER_ADDRESS = "0.0.0.0";

    /**
     * web服务器端口
     */
    public static final Integer SERVER_PORT = 8080;

    /**
     * 不作限制
     */
    public final static int INVALID_LIMIT = -1;

    /**
     * 最大的消息体大小
     */
    public final static int MAX_MESSAGE_BODY_SIZE = 1000;

    /**
     * 呼叫类型的命令
     */
    public final static int COMMAND_TYPE_CALL = 0;

    /**
     * 数据相关的命令
     */
    public final static int COMMAND_TYPE_DATA = 1;

    /**
     * 其它类型的命令
     */
    public final static int COMMAND_TYPE_OTHERS = 2;

    /**
     * 存储的是音频文件
     */
    public final static int APPEND_FILE_AUDIO = 1;

    /**
     * 存储的是视频文件
     */
    public final static int APPEND_FILE_VIDEO = 2;

    /**
     * 信息未读
     */
    public final static int MESSAGE_UNREAD = 1;

    /**
     * 信息已读
     */
    public final static int MESSAGE_READ = 2;
}
