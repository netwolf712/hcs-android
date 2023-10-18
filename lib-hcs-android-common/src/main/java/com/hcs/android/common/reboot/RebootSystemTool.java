package com.hcs.android.common.reboot;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.log.KLog;

public class RebootSystemTool {
    /**
     * 重启系统
     */
    public static void reboot(){
        long delayTime = SettingsHelper.getInstance(BaseApplication.getAppContext()).getLong(RebootConstant.PREF_KEY_REBOOT_DELAY_TIME,RebootConstant.DEFAULT_REBOOT_DELAY_TIME);
        //重启系统
        new Thread(()->{
            try{
                Thread.sleep(delayTime);
                ExeCommand.executeSuCmd("reboot");
            }catch (Exception e){
                KLog.e(e);
            }
        }).start();
    }
}
