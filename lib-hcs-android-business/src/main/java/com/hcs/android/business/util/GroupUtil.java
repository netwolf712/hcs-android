package com.hcs.android.business.util;

import android.content.Context;

import com.hcs.android.business.R;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.service.TimeSlotService;
import com.hcs.android.common.settings.SettingsHelper;

/**
 * 分区助手
 * 主要用于计算分区的语音广播地址和端口
 */
public class GroupUtil {
    /**
     * 主机号的端口偏移权重
     */
    private final static int MASTER_PORT_OFFSET = 100;
    /**
     * 分机机号的端口偏移权重
     */
    private final static int SLAVE_PORT_OFFSET = 2;
    /**
     * 通过主机号与分机号获得语音广播端口
     * @param parentNo 主机号
     * @param groupSn 分组序号
     * @return 语音广播端口
     */
    public static int getAudioMulticastPort(int parentNo,int groupSn){
        return parentNo * MASTER_PORT_OFFSET + groupSn * SLAVE_PORT_OFFSET;
    }

    /**
     * 对象转换
     */
    public static MulticastGroupModel convertToModel(MulticastGroup multicastGroup, Context context,String masterNo){
        MulticastGroupModel groupModel = new MulticastGroupModel();
        groupModel.setMulticastGroup(multicastGroup);
        groupModel.setMulticastAddress(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_ADDRESS,context.getString(R.string.default_audio_multicast_address)));
        groupModel.setMulticastPort(getAudioMulticastPort(Integer.parseInt(masterNo),multicastGroup.getGroupSn()));
        return groupModel;
    }
}
