package com.hcs.android.maintain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RecoverySystem;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.entity.RequestUpgrade;
import com.hcs.android.maintain.entity.ResponseUpgradeStatus;

import java.io.File;

/**
 * 升级管理器
 */
public class UpgradeManager {
    private final Context mContext;

    /**
     * 升级状态
     */
    private final ResponseUpgradeStatus mUpgradeStatus;

    private UpgradeManager(Context context){
        mContext = context;
        mUpgradeStatus = new ResponseUpgradeStatus();
        mUpgradeStatus.setStatus(ResponseUpgradeStatus.STATUS_IDLE);
    }

    @SuppressLint("StaticFieldLeak")
    private static UpgradeManager mInstance = null;
    public static UpgradeManager getInstance(){
        if(mInstance == null){
            synchronized (UpgradeManager.class) {
                if(mInstance == null) {
                    mInstance = new UpgradeManager(BaseApplication.getAppContext());
                }
            }
        }
        return mInstance;
    }

    /**
     * 升级操作
     */
    public void handleUpgrade(@NonNull RequestUpgrade requestUpgrade){
        if(RequestUpgrade.UPGRADE_TYPE_APK == requestUpgrade.getUpgradeType()){
            new Thread(()->{
                //如果是升级apk，则直接安装即可
                mUpgradeStatus.setStatus(ResponseUpgradeStatus.STATUS_UNZIPPING_FILE);
                ExeCommand.executeSuCmd("pm install -t -r " + requestUpgrade.getFilePath());
                mUpgradeStatus.setStatus(ResponseUpgradeStatus.STATUS_FINISHED);
                //删除升级包
                ExeCommand.executeSuCmd("rm " + requestUpgrade.getFilePath());
            }).start();
        }else {
            new Thread(()->{
                //将升级包移动到待升级的地方
                //后缀名检查
                String extension = FileUtil.getExtensionFromFileName(requestUpgrade.getFilePath());
                if(StringUtil.isEmpty(extension)
                ||!( extension.contains("zip") ||  extension.contains("img"))){
                    mUpgradeStatus.setStatus(ResponseUpgradeStatus.STATUS_FAILED);
                    mUpgradeStatus.setReason(mContext.getString(R.string.maintain_error_file_type));
                    return;
                }
                mUpgradeStatus.setStatus(ResponseUpgradeStatus.STATUS_CHECKING);
                //文件校验
                try{
                    RecoverySystem.verifyPackage(new File(requestUpgrade.getFilePath()), null, null);
                }catch (Exception e){
                    mUpgradeStatus.setStatus(ResponseUpgradeStatus.STATUS_FAILED);
                    mUpgradeStatus.setReason(mContext.getString(R.string.maintain_error_file_check));
                    return;
                }
                //文件拷贝
                //todo:升级
            }).start();
        }
    }

    /**
     * 获取更新状态
     */
    public ResponseUpgradeStatus getUpgradeStatus(){
        return mUpgradeStatus;
    }
}
