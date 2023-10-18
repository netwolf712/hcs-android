package com.hcs.app;


import com.alibaba.android.arouter.launcher.ARouter;
import com.hcs.android.business.BusinessApplication;

/**
 * Description: <><br>
 * Author:      mxdl<br>
 * Date:        2018/12/27<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class MainApplication extends BusinessApplication {
    @Override
    public void onCreate() {
        //在业务层初始化好前要先设置好
        ARouter.init(this);
        super.onCreate();
    }

}
