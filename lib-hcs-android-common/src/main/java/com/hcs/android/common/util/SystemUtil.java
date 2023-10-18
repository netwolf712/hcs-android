package com.hcs.android.common.util;

import android.content.Intent;
import android.os.RecoverySystem;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.log.KLog;

import java.io.File;

/**
 * 系统帮助
 */
public class SystemUtil {
    /**
     * 恢复出厂设置，需要系统权限，以及系统签名
     */
    public static void resetSystem() {
//        Intent intent = new Intent("android.intent.action.FACTORY_RESET");
//        //8.0
//        // intent = new Intent("android.intent.action.MASTER_CLEAR");
//        //9.0
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        intent.setPackage("android");
//
//        //以上区分不同系统
//        intent.putExtra("android.intent.extra.REASON", "FactoryMode");
//        //是否擦除SdCard
//        intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
//        intent.putExtra("android.intent.extra.EXTRA_WIPE_ESIMS", true);
//        BaseApplication.getAppContext().sendBroadcast(intent);
        try {
            //删除data区域数据
            ExeCommand.executeSuCmd("rm -rf " + FileUtil.getAppDataDir());
            RecoverySystem.rebootWipeUserData(BaseApplication.getAppContext());
        }catch (Exception e){
            KLog.e(e);
        }
    }
}
