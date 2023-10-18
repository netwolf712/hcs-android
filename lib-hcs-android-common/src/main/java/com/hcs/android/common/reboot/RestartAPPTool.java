package com.hcs.android.common.reboot;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.log.KLog;


public class RestartAPPTool {
    /**
     * 重启整个APP
     * @param delayed 延迟多少毫秒
     */

    public static void restartAPP(long delayed){

        //杀死整个进程
        new Thread(()->{
            try{
                Thread.sleep(delayed);
                android.os.Process.killProcess(android.os.Process.myPid());
            }catch (Exception e){
                KLog.e(e);
            }
        }).start();


    }

    /**
     * 重启整个APP
     */
    public static void restartAPP(){
        restartAPP(SettingsHelper.getInstance(BaseApplication.getAppContext()).getLong(RebootConstant.PREF_KEY_REBOOT_DELAY_TIME,RebootConstant.DEFAULT_REBOOT_DELAY_TIME));
    }

}



