package com.hcs.android.common.util;

import android.widget.Toast;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.log.KLog;

/**
 * Description: <吐司工具类><br>
 * Author: mxdl<br>
 * Date: 2018/6/11<br>
 * Version: V1.0.0<br>
 * Update: <br>
 */
public class ToastUtil {

    public static void showToast(String message) {
        try {
            Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            KLog.e(e);
        }
    }

    public static void showToast(int resid) {
        try {
            Toast.makeText(BaseApplication.getInstance(), BaseApplication.getInstance().getString(resid), Toast.LENGTH_SHORT)
                    .show();
        }catch (Exception e){
            KLog.e(e);
        }
    }
}