package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;
import com.hcs.android.common.util.StringUtil;

/**
 * 命令枚举
 */
public enum CommandEnum {
    /**
     * 命令枚举开始
     * 无实际业务意义
     */
    COMMAND_START("command-start",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 未知命令
     */
    UNKNOWN("command-unknown",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 注册
     */
    REQ_LOGIN_DEVICE("req-login-device",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 注册回复
     */
    RSP_LOGIN_DEVICE("rsp-login-device",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求所有设备列表
     */
    REQ_LIST_DEVICE("req-list-device",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 返回设备列表
     */
    RSP_LIST_DEVICE("rsp-list-device",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 获取最新配置
     */
    REQ_GET_CONFIG("req-get-config",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 返回最新配置
     */
    RSP_GET_CONFIG("rsp-get-config",Constant.COMMAND_TYPE_DATA),
    /**
     * 向床头分机发送显示模板更新命令
     */
    REQ_UPDATE_BED_TEMPLATE("req-update-bed-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向床头分机发送显示获取模板命令
     */
    REQ_GET_BED_TEMPLATE("req-get-bed-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向病房门口机发送显示模板更新命令
     */
    REQ_UPDATE_ROOM_TEMPLATE("req-update-room-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向病房门口屏发送显示获取模板命令
     */
    REQ_GET_ROOM_TEMPLATE("req-get-room-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向病区门口机发送显示模板更新命令
     */
    REQ_UPDATE_DOOR_TEMPLATE("req-update-door-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向一览表发送显示模板更新命令
     */
    REQ_UPDATE_PANEL_TEMPLATE("req-update-panel-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向一览表发送显示模板更新命令
     */
    REQ_UPDATE_CORRIDOR_TEMPLATE("req-update-corridor-template",Constant.COMMAND_TYPE_DATA),
    /**
     * 向所有其它设备通知设备自身信息变更
     */
    REQ_CHANGE_DEVICE_INFO("req-change-device-info",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 更新字典
     */
    REQ_UPDATE_DICT("req-update-dict",Constant.COMMAND_TYPE_DATA),
    /**
     * 主机向其它设备更新病员信息
     */
    REQ_UPDATE_PATIENT_INFO("req-update-patient-info",Constant.COMMAND_TYPE_DATA),
    /**
     * 请求所有病员列表
     */
    REQ_LIST_PATIENT("req-list-patient",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 返回病员列表
     */
    RSP_LIST_PATIENT("rsp-list-patient",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求位置列表
     */
    REQ_LIST_PLACE("req-list-place",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 返回位置列表
     */
    RSP_LIST_PLACE("rsp-list-place",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 向病区门口机更新信息
     */
    REQ_UPDATE_DOOR_INFO("req-update-door-info",Constant.COMMAND_TYPE_DATA),
    /**
     * 请求获取病区门口机的开门记录
     */
    REQ_LIST_DOOR_OPEN_HISTORY("req-list-door-open-history",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 病区门口机回复开门记录
     */
    RSP_LIST_DOOR_OPEN_HISTORY("rsp-list-door-open-history",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求病区门口机开门
     */
    REQ_OPEN_DOOR("req-open-door",Constant.COMMAND_TYPE_DATA),
    /**
     * 请求获取病区门口机配置信息
     */
    REQ_GET_DOOR_INFO("req-get-door-info",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 病区门口机回复配置信息
     */
    RSP_GET_DOOR_INFO("rsp-get-door-info",Constant.COMMAND_TYPE_DATA),
    /**
     * 呼叫
     */
    REQ_SEND_CALL("req-send-call",Constant.COMMAND_TYPE_CALL),
    /**
     * 通知其它设备更新通话状态-只作显示
     */
    REQ_UPDATE_CALL_INFO("req-update-call-info",Constant.COMMAND_TYPE_CALL),
    /**
     * 病房门口机通知病房分机清理呼叫
     */
    REQ_CLEAR_CALL("req-clear-call",Constant.COMMAND_TYPE_CALL),
    /**
     * 主机通知其下的所有设备上级主机更改
     */
    REQ_UPDATE_LISTEN_DEVICE("req-update-listen-device",Constant.COMMAND_TYPE_DATA),
    /**
     * 主机通知其下的设备其对应信息显示设备的更改
     */
    REQ_UPDATE_SHOW_DEVICE("req-update-show-device",Constant.COMMAND_TYPE_DATA),
    /**
     * 其它设备告知主机其当前能力
     * （比如有没网络摄像头）
     */
    REQ_UPDATE_CAPABILITY("req-update-capability",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 主机向设备播放消息提醒要开始广播了
     */
    REQ_MULTICAST_START("req-multicast-start",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 主机向设备播放消息告知广播结束
     */
    REQ_MULTICAST_STOP("req-multicast-stop",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 主机向其它主机发送托管请求
     */
    REQ_TRUST("req-trust",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 其它主机对托管请求的回复
     */
    RSP_TRUST("rsp-trust",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 设备状态查询
     */
    REQ_GET_STATUS("req-get-status",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 设备状态查询回复
     */
    RSP_GET_STATUS("rsp-get-status",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 通知设备进行重启操作
     */
    REQ_REBOOT_DEVICE("req-reboot-device",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 通知设备进行升级操作
     */
    REQ_UPGRADE_DEVICE("req-upgrade-device",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 通知改变分区划分方法
     */
    REQ_BIND_GROUP("req-bind-group",Constant.COMMAND_TYPE_DATA),
    /**
     * 通知改变位置绑定
     */
    REQ_BIND_PLACE("req-bind-place",Constant.COMMAND_TYPE_DATA),
    /**
     * 通知改变位置重新绑定父节点
     */
    REQ_BIND_PLACE_PARENT("req-bind-place-parent",Constant.COMMAND_TYPE_DATA),
    /**
     * 通知设备更新文件到服务器
     */
    REQ_ASK_UPLOAD_DEVICE_FILE("req-ask-upload-device-file",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 服务器向设备推送文件
     */
    REQ_UPLOAD_DEVICE_FILE("req-upload-device-file",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 抓包控制
     */
    REQ_CAPTURE("req-capture",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 修改设备账号
     */
    REQ_CHANGE_ACCOUNT("req-change-account",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 修改设备优化方案
     */
    REQ_UPDATE_OPTIMIZE("req-update-optimize",Constant.COMMAND_TYPE_DATA),
    /**
     * 服务器向主机发送IPC更改命令
     */
    REQ_UPDATE_IPC("req-update-ipc",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 设备推送文件到服务器
     */
    REQ_UPLOAD_FILE_TO_SERVICE("req-upload-file-to-service",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 主机更新设备扩展信息到分机
     */
    REQ_UPDATE_DEVICE_EXTEND("req-update-device-extend",Constant.COMMAND_TYPE_DATA),

    /**
     * 主机更新位置信息到分机
     */
    REQ_UPDATE_PLACE_INFO("req-update-place-info",Constant.COMMAND_TYPE_DATA),
    /**
     * 更新分区信息
     */
    REQ_UPDATE_GROUP_INFO("req-update-group-info",Constant.COMMAND_TYPE_DATA),
    /**
     * 心跳
     */
    REQ_HEART_BEAT("req-heart-beat",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病床列表
     */
    REQ_LIST_BED("req-list-bed",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 病床列表请求回复
     */
    RSP_LIST_BED("rsp-list-bed",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病房列表
     */
    REQ_LIST_ROOM("req-list-room",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 病房列表请求回复
     */
    RSP_LIST_ROOM("rsp-list-room",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病区列表
     */
    REQ_LIST_SECTION("req-list-section",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 病区列表请求回复
     */
    RSP_LIST_SECTION("rsp-list-section",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取设备信息
     */
    REQ_GET_DEVICE_INFO("req-get-device-info",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 获取设备信息请求回复
     */
    RSP_GET_DEVICE_INFO("rsp-get-device-info",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病床详细信息
     */
    REQ_GET_BED_DETAIL("req-get-bed-detail",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 获取病床详细信息请求回复
     */
    RSP_GET_BED_DETAIL("rsp-get-bed-detail",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病房详细信息
     */
    REQ_GET_ROOM_DETAIL("req-get-room-detail",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 获取病房详细信息请求回复
     */
    RSP_GET_ROOM_DETAIL("rsp-get-room-detail",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病区详细信息
     */
    REQ_GET_SECTION_DETAIL("req-get-section-detail",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 获取病区详细信息请求回复
     */
    RSP_GET_SECTION_DETAIL("rsp-get-section-detail",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取字典数据
     */
    REQ_LIST_DICT("req-list-dict",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求更新附件
     */
    REQ_UPDATE_ATTACHMENT("req-update-attachment",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取附件列表
     */
    REQ_LIST_ATTACHMENT("req-list-attachment",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 更新继任主机
     */
    REQ_UPDATE_STEP_MASTER("req-update-step-master",Constant.COMMAND_TYPE_DATA),
    /**
     * 请求获取继任主机列表
     */
    REQ_LIST_STEP_MASTER("req-list-step-master",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 更新时间段
     */
    REQ_UPDATE_TIME_SLOT("req-update-time-slot",Constant.COMMAND_TYPE_DATA),
    /**
     * 请求获取时间段列表
     */
    REQ_LIST_TIME_SLOT("req-list-time-slot",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 更新时间段
     */
    REQ_UPDATE_BED_FUNCTION("req-update-bed-function",Constant.COMMAND_TYPE_DATA),
    /**
     * 请求获取床头屏功能列表
     */
    REQ_LIST_BED_FUNCTION("req-list-bed-function",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取病房门口机功能列表
     */
    REQ_LIST_ROOM_FUNCTION("req-list-room-function",Constant.COMMAND_TYPE_OTHERS),

    /**
     * 更新隐私设置
     */
    REQ_UPDATE_PRIVACY("req-update-privacy",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取隐私设置
     */
    REQ_GET_PRIVACY("req-get-privacy",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 更新声音设置
     */
    REQ_UPDATE_VOLUME("req-update-volume",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 请求获取声音设置
     */
    REQ_GET_VOLUME("req-get-volume",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 通知其它设备本机已经起来
     */
    NOTICE_DEVICE_STARTED("notice-device-started",Constant.COMMAND_TYPE_OTHERS),
    /**
     * 命令枚举结束
     * 无实际业务意义
     */
    COMMAND_END("command-end",Constant.COMMAND_TYPE_OTHERS);


    /**
     * 命令id
     */
    private final String id;

    /**
     * 命令类型
     */
    private final int type;
    CommandEnum(String id,int type){
        this.id = id;
        this.type = type;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static CommandEnum findById(String id){
        if(StringUtil.isEmpty(id)){
            return UNKNOWN;
        }
        for(CommandEnum filterEnum : CommandEnum.values()){
            if(id.equalsIgnoreCase(filterEnum.getId())){
                return filterEnum;
            }
        }
        return UNKNOWN;
    }

    /**
     * 返回该命令的文字说明
     */
    public String getName(@NonNull Context context){
        return context.getString(ResourceUtil.getStringId(context,id.replaceAll("-","_")));
    }

    public String getId(){
        return id;
    }

    public int getType(){
        return type;
    }
}
