package com.hcs.android.business.request.model;

import android.content.Context;
import androidx.annotation.NonNull;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.RequestPrivacy;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.settings.SettingsHelper;

/**
 * 高级设置中 隐私设置内容
 */
public class PrivacyRequestModel {

    /**
     * 获取隐私设置内容
     * @return
     */
    @NonNull
    public RequestPrivacy getPrivacySet(){
        return WorkManager.getInstance().getPrivacySet();
    }

    /**
     * 更新隐私设置内容
     */
    public void updateStepMasters(RequestPrivacy privacySet, Context context){
        if (null != privacySet) {
            SettingsHelper.getInstance(context).putData(PreferenceConstant.PRIVACY_ENABLE,privacySet.isEnable());
            SettingsHelper.getInstance(context).putData(PreferenceConstant.PRIVACY_DEVICE_TYPE,privacySet.getDeviceTypes());
            SettingsHelper.getInstance(context).putData(PreferenceConstant.PRIVACY_RULE_HIDE_START,privacySet.getHideStart());
            SettingsHelper.getInstance(context).putData(PreferenceConstant.PRIVACY_RULE_HIDE_LENGTH,privacySet.getHideLength());
            SettingsHelper.getInstance(context).putData(PreferenceConstant.PRIVACY_RULE_REPLACE_CHARACTER,privacySet.getReplaceCharacter());
        }
    }
}
