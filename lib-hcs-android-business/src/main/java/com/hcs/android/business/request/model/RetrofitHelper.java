package com.hcs.android.business.request.model;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestLoginService;
import com.hcs.android.business.manager.HandlerBase;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.server.entity.User;
import com.hcs.android.server.service.UserService;

/**
 * 远端服务器访问管理器
 */
public class RetrofitHelper{
    private RobustTimer mTimer;
    /**
     * 重试次数
     */
    private final static int RETRY_TIMES = 5;
    private int mLoginTimes = 0;
    private Long mLastLoginTime = 0L;

    private HandlerBase mHandlerBase;
    public void setHandlerBase(HandlerBase handlerBase){
        mHandlerBase = handlerBase;
    }
    /**
     * 保持登录的时间间隔
     */
    private final static Long KEEP_LOGIN_SPAN = 5 * 60 * 1000L;

    public void start(String serviceURL){
        //todo: 程序启动操作
    }

    public void stop(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        //todo:其它释放操作
    }

    /**
     * 访问管理器是否已经正常工作
     * 否则不能访问
     */
    public boolean isActive(){
        //todo: 服务管理器是否已经正常
        return false;
    }
    public RetrofitHelper(){

    }

    private static RetrofitHelper mInstance = null;
    public static RetrofitHelper getInstance(){
        if(mInstance == null){
            synchronized (RetrofitHelper.class){
                if(mInstance == null) {
                    mInstance = new RetrofitHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 创建一个自定义服务
     */
    public <T> T getService(final Class<T> service){
        //todo:完善自定义服务内容
        return null;
    }

    /**
     * 检测是否需要重新登录
     * @return true需要重新登录，false不用重新登录
     */
    public boolean isNeedLogin(){
        //todo: 是否需要重新登录判断，可根据实际逻辑考虑实现或删除
        return true;
    }
    /**
     * 执行登录操作
     */
    private void login(){
        //登录过了就不要重新登录了
        if(!isNeedLogin()){
            return;
        }
        UserService userService = new UserService();
        //模拟设备登录
        User user = userService.serviceLogin();
        //偷懒，自制登录字符串
        RequestLoginService loginUser = new RequestLoginService();
        loginUser.setUsername(WorkManager.getInstance().getSelfUsername());
        loginUser.setPassword(WorkManager.getInstance().getSelfPassword());
        loginUser.setTokenForService(user.getToken());
        RequestDTO<RequestLoginService> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(), CommandEnum.REQ_LOGIN_DEVICE.getId());
        requestDTO.setData(loginUser);
        //todo: 执行注册操作
    }

    /**
     * 保持登录状态
     */
    private void keepLogin(){
        if(!isActive() || mTimer == null){
            return;
        }
        RobustTimerTask timerTask = new RobustTimerTask() {
            @Override
            public void run() {
                login();
            }
        };
        mTimer.schedule(timerTask, KEEP_LOGIN_SPAN, KEEP_LOGIN_SPAN);
    }

}
