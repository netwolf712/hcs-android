package com.hcs.android.maintain;

import android.annotation.SuppressLint;
import android.content.Context;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.reboot.RestartAPPTool;
import com.hcs.android.maintain.constant.CommandEnum;
import com.hcs.android.maintain.controller.WebController;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.server.web.AjaxResult;
import com.hcs.android.server.web.WebServer;

/**
 * 运维管理器
 * 运维模块总入口
 */
public class MaintainManager {
    private final Context mContext;
    private final WebController mWebController;
    private MaintainManager(){
        mContext = BaseApplication.getAppContext();
        mWebController = new WebController();
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final MaintainManager mInstance = new MaintainManager();
    }

    public static MaintainManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    /**
     * 初始化运维管理器
     */
    public void init(){
        //日志先行
        LogManager.getInstance().init(mContext);

        //开启文件管理
        FileManager.getInstance().start();

        //注册restful事件
        WebServer.getInstance().setMaintainController(mWebController);
    }

    /**
     * 采用异步可订阅的形式发送内部请求
     * @param requestDTO 请求内容
     * @return 可订阅对象
     */
    public ObservableData<AjaxResult> sendInnerRequestObservable(String requestDTO){
        ObservableData<AjaxResult> observableAjaxResult = new ObservableData<>();
        new Thread(()->{
            AjaxResult ajaxResult = mWebController.dispatchMessage(requestDTO);
            observableAjaxResult.setT(ajaxResult);
        }).start();
        return observableAjaxResult;
    }

    /**
     * 获取请求命令id
     */
    public CommandEnum getCommand(String requestDTO){
        return mWebController.getCommand(requestDTO);
    }
}
