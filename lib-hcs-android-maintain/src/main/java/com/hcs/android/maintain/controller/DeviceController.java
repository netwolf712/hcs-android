package com.hcs.android.maintain.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.network.NetConfig;
import com.hcs.android.common.reboot.RebootSystemTool;
import com.hcs.android.common.reboot.RestartAPPTool;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.SystemUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.DeviceBaseManager;
import com.hcs.android.maintain.FileManager;
import com.hcs.android.maintain.R;
import com.hcs.android.maintain.SNTPManager;
import com.hcs.android.maintain.UpgradeManager;
import com.hcs.android.maintain.entity.DeviceBaseInfo;
import com.hcs.android.maintain.entity.RequestConfirm;
import com.hcs.android.maintain.entity.RequestDTO;
import com.hcs.android.maintain.entity.RequestRecoverConfig;
import com.hcs.android.maintain.entity.RequestUpgrade;
import com.hcs.android.maintain.entity.ResponseAlive;
import com.hcs.android.maintain.entity.Server;
import com.hcs.android.maintain.entity.TimeConfig;
import com.hcs.android.server.service.UserService;
import com.hcs.android.server.web.AjaxResult;

/**
 * 设备控制相关
 */
@CommandMapping
public class DeviceController {
    /**
     * 请求获取设备信息
     */
    @CommandId("maintain-req-device-info")
    public AjaxResult getDeviceInfo(String str){
        try {
            Server server = new Server();
            server.copyTo();
            return AjaxResult.success("", server);
        }catch (Exception e){
            KLog.e(e);
        }
        return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_get_device_info_failed));
    }

    /**
     * 请求重启应用
     */
    @CommandId("maintain-req-restart-app")
    public AjaxResult restartApp(String str){
        try {
            RequestDTO<RequestConfirm> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestConfirm.class});
            if(requestDTO.getData() != null){
                RequestConfirm requestConfirm = requestDTO.getData();
                UserService userService = new UserService();
                if(userService.checkPassword(requestConfirm.getToken(),requestConfirm.getPassword())){
                    RestartAPPTool.restartAPP();
                    return AjaxResult.success(BaseApplication.getAppContext().getString(R.string.maintain_restart_success));
                }else{
                    return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_password_confirm_failed));
                }
            }

        }catch (Exception e){
            KLog.e(e);
        }
        return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_restart_failed));
    }

    /**
     * 请求重启系统
     */
    @CommandId("maintain-req-restart-system")
    public AjaxResult restartSystem(String str){
        try {
            RequestDTO<RequestConfirm> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestConfirm.class});
            if(requestDTO.getData() != null){
                RequestConfirm requestConfirm = requestDTO.getData();
                UserService userService = new UserService();
                if(userService.checkPassword(requestConfirm.getToken(),requestConfirm.getPassword())){
                    RebootSystemTool.reboot();
                    return AjaxResult.success(BaseApplication.getAppContext().getString(R.string.maintain_restart_success));
                }else{
                    return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_password_confirm_failed));
                }
            }
        }catch (Exception e){
            KLog.e(e);
        }
        return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_restart_failed));
    }

    /**
     * 询问设备是否存活
     */
    @CommandId("maintain-req-is-alive")
    public AjaxResult isAlive(String str){
        return AjaxResult.success("",new ResponseAlive(BaseApplication.getAppStartTime()));
    }

    /**
     * 恢复出厂设置
     */
    @CommandId("maintain-req-reset-system")
    public AjaxResult resetSystem(String str){
        try {
            RequestDTO<RequestConfirm> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestConfirm.class});
            if(requestDTO.getData() != null){
                RequestConfirm requestConfirm = requestDTO.getData();
                UserService userService = new UserService();
                if(userService.checkPassword(requestConfirm.getToken(),requestConfirm.getPassword())){
                    SystemUtil.resetSystem();
                    //重置完毕后需要重启应用
                    RestartAPPTool.restartAPP();
                    return AjaxResult.success(BaseApplication.getAppContext().getString(R.string.maintain_reset_success));
                }else{
                    return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_password_confirm_failed));
                }
            }
        }catch (Exception e){
            KLog.e(e);
        }
        return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_restart_failed));
    }

    /**
     * 还原设置
     */
    @CommandId("maintain-req-recover-config")
    public AjaxResult recoverConfig(String str){
        try {
            RequestDTO<RequestRecoverConfig> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestRecoverConfig.class});
            if(requestDTO.getData() != null){
                //还原配置
                FileManager.getInstance().recoverConfig(requestDTO.getData());
                //重置完毕后需要重启应用
                RestartAPPTool.restartAPP();
                return AjaxResult.success(BaseApplication.getAppContext().getString(R.string.maintain_recover_success));
            }
        }catch (Exception e){
            KLog.e(e);
        }
        return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_restart_failed));
    }

    /**
     * 升级
     */
    @CommandId("maintain-req-upgrade")
    public AjaxResult upgrade(String str){
        try {
            RequestDTO<RequestUpgrade> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestUpgrade.class});
            if(requestDTO.getData() != null){
                //开始升级
                UpgradeManager.getInstance().handleUpgrade(requestDTO.getData());
                return AjaxResult.success(BaseApplication.getAppContext().getString(R.string.maintain_start_upgrade));
            }
        }catch (Exception e){
            KLog.e(e);
        }
        return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_restart_failed));
    }

    /**
     * 获取升级状态
     */
    @CommandId("maintain-req-upgrade-status")
    public AjaxResult getUpgradeStatus(String str){
        return AjaxResult.success("",UpgradeManager.getInstance().getUpgradeStatus());
    }

    /**
     * 获取设备基本信息
     */
    @CommandId("maintain-req-device-base-info")
    public AjaxResult getDeviceBaseInfo(String str){
        return AjaxResult.success("", DeviceBaseManager.getInstance().getDeviceInfo());
    }

    /**
     * 获取网络配置信息
     */
    @CommandId("maintain-req-net-config")
    public AjaxResult getNetConfig(String str){
        return AjaxResult.success("", DeviceBaseManager.getInstance().getNetConfig());
    }

    /**
     * 获取时间配置信息
     */
    @CommandId("maintain-req-time-config")
    public AjaxResult getTimeConfig(String str){
        return AjaxResult.success("", SNTPManager.getInstance().getTimeConfig());
    }
    /**
     * 更新IP地址
     */
    @CommandId("maintain-req-update-ip-address")
    public AjaxResult updateIpAddress(String str){
        RequestDTO<NetConfig> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,NetConfig.class});
        if(requestDTO.getData() != null){
            //先比较下是否有修改的必要
            DeviceBaseInfo deviceBaseInfo = DeviceBaseManager.getInstance().getDeviceInfo();
            if(!deviceBaseInfo.getNetConfig().compare(requestDTO.getData())) {
                // 若不一样则进行修改操作
                // 延时处理
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        DeviceBaseManager.getInstance().changeNetConfig(requestDTO.getData());
                        //修改完毕后需要重启应用
                        RestartAPPTool.restartAPP();
                    } catch (Exception e) {
                        KLog.e(e);
                    }
                }).start();
            }else{
                //一样则无须修改
                return AjaxResult.error(BaseApplication.getAppContext().getString(R.string.maintain_network_nothing_change));
            }
        }
        return AjaxResult.success("");
    }

    /**
     * 更新时间
     */
    @CommandId("maintain-req-update-time")
    public AjaxResult updateTime(String str){
        RequestDTO<TimeConfig> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,TimeConfig.class});
        if(requestDTO.getData() != null){
            //更新NTP服务器
            SNTPManager.getInstance().updateNTPServer(requestDTO.getData());
        }
        return AjaxResult.success("");
    }

    /**
     * 请求进行截屏
     */
    @CommandId("maintain-req-screen-capture")
    public AjaxResult reqScreenCapture(String str){
        return AjaxResult.success("",FileManager.getInstance().handleScreenCapture());
    }
}
