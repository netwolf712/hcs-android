package com.hcs.android.maintain.constant;

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
    COMMAND_START("maintain-command-start"),
    /**
     * 未知命令
     */
    UNKNOWN("maintain-command-unknown"),
    /**
     * 获取文件列表
     */
    REQ_GET_FILE_LIST("maintain-req-get-file-list"),
    /**
     * 文件打包
     */
    REQ_PACKAGE_FILE("maintain-req-package-file"),
    /**
     * 获取配置
     */
    REQ_LOG_GET_CONFIG("maintain-req-log-get-config"),
    /**
     * 获取配置回复
     */
    RSP_LOG_GET_CONFIG("maintain-rsp-log-get-config"),
    /**
     * 设置配置
     */
    REQ_LOG_SET_CONFIG("maintain-req-log-set-config"),
    /**
     * 下载文件
     */
    REQ_LOG_DOWNLOAD_FILE("maintain-req-download-file"),
    /**
     * 文件操作
     */
    REQ_OPERATE_FILE("maintain-req-operate-file"),
    /**
     * 获取设备信息
     */
    REQ_DEVICE_INFO("maintain-req-device-info"),
    /**
     * 重启应用
     */
    REQ_RESTART_APP("maintain-req-restart-app"),
    /**
     * 重启系统
     */
    REQ_RESTART_SYSTEM("maintain-req-restart-system"),
    /**
     * 恢复出厂设置
     */
    REQ_RESET_SYSTEM("maintain-req-reset-system"),
    /**
     * 还原配置
     */
    REQ_RECOVER_CONFIG("maintain-req-recover-config"),
    /**
     * 升级
     */
    REQ_UPGRADE("maintain-req-upgrade"),
    /**
     * 升级状态
     */
    REQ_UPGRADE_STATUS("maintain-req-upgrade-status"),
    /**
     * 查询是否存活
     */
    REQ_IS_ALIVE("maintain-req-is-alive"),
    /**
     * 测试
     */
    REQ_TEST("maintain-req-test"),
    /**
     * 测试状态
     */
    REQ_TEST_STATUS("maintain-req-test-status"),
    /**
     * 请求获取设备基本信息
     */
    REQ_DEVICE_BASE_INFO("maintain-req-device-base-info"),
    /**
     * 请求更新ip地址
     */
    REQ_UPDATE_IP_ADDRESS("maintain-req-update-ip-address"),
    /**
     * 请求更新时间
     */
    REQ_UPDATE_TIME("maintain-req-update-time"),

    /**
     * 请求获取网络配置信息
     */
    REQ_NET_CONFIG("maintain-req-net-config"),

    /**
     * 请求获取时间配置信息
     */
    REQ_TIME_CONFIG("maintain-req-time-config"),
    /**
     * 请求进行截屏
     */
    REQ_SCREEN_CAPTURE("maintain-req-screen-capture"),
    /**
     * 命令枚举结束
     * 无实际业务意义
     */
    COMMAND_END("maintain-command-end");


    /**
     * 命令id
     */
    private final String id;

    CommandEnum(String id){
        this.id = id;
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
}
