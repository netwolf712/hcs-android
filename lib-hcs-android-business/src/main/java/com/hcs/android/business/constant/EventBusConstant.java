package com.hcs.android.business.constant;

/**
 * EventBus用到的tag
 */
public class EventBusConstant {
    /**
     * 工作管理器初始化完成
     */
    public final static String WORK_MANAGER_INIT_OK = "WorkManager.init.OK";

    /**
     * 处理内部发送的请求
     */
    public final static String HANDLE_INNER_REQUEST = "WorkManager.handleInnerRequest";

    /**
     * 提示界面要开始语音广播了
     */
    public final static String AUDIO_MULTICAST_NOTICE = "SlaveManager.handleStartAudioMulticast";

    /**
     * 提示界面有呼叫进入
     */
    public final static String INCOMING_CALL_NOTICE = "SERVICE.incoming_call";

    /**
     * 更新了缓存
     */
    public final static String HANDLE_CACHE_UPDATED = "DeviceManage.cache_updated";


    /**
     * 通知分机更新了模板
     */
    public final static String HANDLE_TEMPLATE_UPDATED = "SlaveManage.template_updated";

    /**
     * 通知分机状态改变了
     */
    public final static String HANDLE_STATE_CHANGED = "SlaveManage.state_changed";

    /**
     * 更新了分机的摄像头信息
     */
    public final static String HANDLE_IPC_UPDATED = "DeviceManage.ipc_updated";

    /**
     * 通知分机更新了位置信息
     */
    public final static String HANDLE_PLACE_UPDATED = "SlaveManage.place_updated";
}
