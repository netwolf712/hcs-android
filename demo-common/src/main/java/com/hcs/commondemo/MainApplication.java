package com.hcs.commondemo;



import com.alibaba.android.arouter.launcher.ARouter;
import com.hcs.android.common.BaseApplication;


public class MainApplication extends BaseApplication {
    @Override
    public void onCreate() {
        ARouter.init(this);
        super.onCreate();
    }

}
