package com.hcs.android.business.util;

/**
 * 用于应用音量与安卓音量单位的换算
 * 应用音量为百分百
 * 安卓音量为0-15
 */
public class VolumeUtil {

    /**
     * 系统最大音量
     */
    private static final int MAX_SYSTEM_VOLUME = 15;

    /**
     * 100%
     */
    private static final int WHOLE_PERCENTAGE = 100;

    /**
     * 将app的百分比转换为系统的音量
     * @param appPercentage 百分比
     * @return 系统音量
     */
    public static int appToSys(int appPercentage){
        return MAX_SYSTEM_VOLUME  * appPercentage / WHOLE_PERCENTAGE;
    }

    /**
     * 将系统音量转换为app的百分比
     * @param sysVolume 系统音量
     * @return 百分比
     */
    public static int sysToApp(int sysVolume){
        return sysVolume * WHOLE_PERCENTAGE / MAX_SYSTEM_VOLUME;
    }
}
