package com.hcs.android.common.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.log.KLog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Description: <PermissionCheckUtil><br>
 * Author:      mxdl<br>
 * Date:        2019/3/29<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class PermissionCheckUtil {
    /**
     * 权限申请弹出框的返回值
     */
    public static final int PERMISSION_RTN_CODE = 900001;
    public static void check(FragmentActivity activity,List<String> permissionList,PermissionListener permissionListener){
        String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        new RxPermissions(activity).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if(permissionListener != null){
                    permissionListener.onAccept(b);
                }
            }
        });
    }

    /**
     * 检查是否有权限
     * @param permission 权限内容
     * @return 是否有权限
     */
    public static boolean checkPermission(String permission){
        if (ActivityCompat.checkSelfPermission(BaseApplication.getAppContext(), permission) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }

    /**
     * 申请权限
     * @param context
     * @param permission  Manifest.permission
     */
    public static void requestPermissions(Activity context, String permission){
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(context, new String[]{permission},
                    PERMISSION_RTN_CODE);
        }
    }

    public static void checkAndRequestCameraVideoPermissions(Activity activity,PermissionListener permissionListener) {
        boolean camera = PermissionCheckUtil.checkPermission(Manifest.permission.CAMERA);
        KLog.i(
                "[Permission] Camera permission is "
                        + (camera ? "granted" : "denied"));
        if (!camera) {
            KLog.i("[Permission] Asking for record camera");
            List<String> permissionList = new ArrayList<>();
            permissionList.add(Manifest.permission.CAMERA);
            check((FragmentActivity)activity,permissionList,permissionListener);
        }else{
            if(permissionListener != null){
                permissionListener.onAccept(true);
            }
        }
    }
    public static void checkAndRequestRecordVideoPermissions(Activity activity,PermissionListener permissionListener) {
        List<String> permissionList = new ArrayList<>();
        boolean recordAudio = PermissionCheckUtil.checkPermission(Manifest.permission.RECORD_AUDIO);
        KLog.i(
                "[Permission] Record audio permission is "
                        + (recordAudio ? "granted" : "denied"));

        if (!recordAudio) {
            KLog.i("[Permission] Asking for record audio");
            //PermissionCheckUtil.requestPermissions(activity,Manifest.permission.RECORD_AUDIO);
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        boolean storeRecord = PermissionCheckUtil.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(!storeRecord){
            KLog.i("[Permission] Asking for store record");
            //PermissionCheckUtil.requestPermissions(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        boolean camera = PermissionCheckUtil.checkPermission(Manifest.permission.CAMERA);
        KLog.i(
                "[Permission] Camera permission is "
                        + (camera ? "granted" : "denied"));
        if (!camera) {
            KLog.i("[Permission] Asking for record camera");
            permissionList.add(Manifest.permission.CAMERA);
        }
        if(permissionList.size() > 0){
            check((FragmentActivity)activity,permissionList,permissionListener);
        }else{
            if(permissionListener != null){
                permissionListener.onAccept(true);
            }
        }
    }

    public static void checkAndRequestRecordAudioPermissions(Activity activity,PermissionListener permissionListener) {

        List<String> permissionList = new ArrayList<>();
        boolean recordAudio = PermissionCheckUtil.checkPermission(Manifest.permission.RECORD_AUDIO);
        KLog.i(
                "[Permission] Record audio permission is "
                        + (recordAudio ? "granted" : "denied"));

        if (!recordAudio) {
            KLog.i("[Permission] Asking for record audio");
            //PermissionCheckUtil.requestPermissions(activity,Manifest.permission.RECORD_AUDIO);
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        boolean storeRecord = PermissionCheckUtil.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(!storeRecord){
            KLog.i("[Permission] Asking for store record");
            //PermissionCheckUtil.requestPermissions(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(permissionList.size() > 0){
            check((FragmentActivity)activity,permissionList,permissionListener);
        }else{
            if(permissionListener != null){
                permissionListener.onAccept(true);
            }
        }
    }

    public static void checkAndRequestMulticastPermissions(Activity activity,PermissionListener permissionListener){
        boolean perm = PermissionCheckUtil.checkPermission(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);
        if(!perm){
            KLog.i("[Permission] Asking for wifi multicast");
            List<String> permissionList = new ArrayList<>();
            permissionList.add(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);
            check((FragmentActivity)activity,permissionList,permissionListener);
        }else{
            if(permissionListener != null){
                permissionListener.onAccept(true);
            }
        }
    }

    public static void checkChangeNetworkPermission(Activity activity,PermissionListener permissionListener){
        List<String> permissionList = new ArrayList<>();
        boolean writeSecureSettings = PermissionCheckUtil.checkPermission(Manifest.permission.WRITE_SECURE_SETTINGS);
        if(!writeSecureSettings){
            KLog.i("[Permission] Asking for write secure settings");
            permissionList.add(Manifest.permission.WRITE_SECURE_SETTINGS);

        }

//        boolean connectivityInternal = PermissionCheckUtil.checkPermission(Manifest.permission.CONNECTIVITY_INTERNAL);
//        if(!connectivityInternal){
//            KLog.i("[Permission] Asking for CONNECTIVITY INTERNAL");
//            permissionList.add(Manifest.permission.CONNECTIVITY_INTERNAL);
//        }
        if(permissionList.size() > 0){
            check((FragmentActivity)activity,permissionList,permissionListener);
        }else{
            if(permissionListener != null){
                permissionListener.onAccept(true);
            }
        }
    }

    public static void checkBasePermission(Activity activity,PermissionListener permissionListener){
        List<String> permissionList = new ArrayList<>();
        boolean writeSecureSettings = PermissionCheckUtil.checkPermission(Manifest.permission.WRITE_SECURE_SETTINGS);
        if(!writeSecureSettings){
            KLog.i("[Permission] Asking for write secure settings");
            permissionList.add(Manifest.permission.WRITE_SECURE_SETTINGS);

        }
        boolean recordAudio = PermissionCheckUtil.checkPermission(Manifest.permission.RECORD_AUDIO);
        KLog.i(
                "[Permission] Record audio permission is "
                        + (recordAudio ? "granted" : "denied"));

        if (!recordAudio) {
            KLog.i("[Permission] Asking for record audio");
            //PermissionCheckUtil.requestPermissions(activity,Manifest.permission.RECORD_AUDIO);
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        boolean storeRecord = PermissionCheckUtil.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(!storeRecord){
            KLog.i("[Permission] Asking for store record");
            //PermissionCheckUtil.requestPermissions(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(permissionList.size() > 0){
            check((FragmentActivity)activity,permissionList,permissionListener);
        }else{
            if(permissionListener != null){
                permissionListener.onAccept(true);
            }
        }
    }
    /**
     * 权限监听器
     */
    public interface PermissionListener{
        /**
         * 是否接受
         * @param accept true 接受，false 不接受
         */
        void onAccept(boolean accept);
    }
}
