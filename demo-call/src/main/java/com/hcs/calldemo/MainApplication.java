package com.hcs.calldemo;



import com.alibaba.android.arouter.launcher.ARouter;
import com.hcs.android.call.api.PhoneManager;
import com.hcs.android.common.BaseApplication;



public class MainApplication extends BaseApplication {
    @Override
    public void onCreate() {
        ARouter.init(this);
        super.onCreate();
        //创建呼叫服务
        PhoneManager.getInstance().init(BaseApplication.getAppContext());
        //启动呼叫服务
        PhoneManager.getInstance().start();
    }

}
