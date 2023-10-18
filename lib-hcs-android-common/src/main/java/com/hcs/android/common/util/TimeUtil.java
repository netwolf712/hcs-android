package com.hcs.android.common.util;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

/**
 * 将毫秒转换成时:分:秒
 */
public class TimeUtil {
    /**
     * 秒
     */
    public static final long SECOND = 1;

    /**
     * 1分钟=60秒
     */
    public static final long MINUTE = 60;

    /**
     * 1小时=3600秒
     */
    public static final long HOUR = 3600;

    /**
     * 1天=86400秒
     */
    public static final long DAY = 86400;

    /**
     * 一秒的毫秒数
     */
    public static final long SECOND_MS = 1000;
    /**
     * 将毫秒转换成时:分:秒
     * @param millisecond 毫秒时间
     * @return 转换结果
     */
    @NonNull
    @SuppressLint("DefaultLocale")
    public static String convertMillisecondToTime(long millisecond){
        //先将毫秒转成秒
        long leastTime = millisecond / 1000;
        long hours = leastTime / HOUR;
        leastTime = leastTime % HOUR;
        Long minutes = leastTime / MINUTE;
        Long seconds = leastTime % MINUTE;
        if(hours == 0){
            return String.format("%02d:%02d", minutes, seconds);
        }else {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
    }


    /**
     * 将播放时间转换为字符串格式
     * @param currentPosition 当前播放的时间
     * @param duration 总时间
     * @return hh:mm:ss/hh:mm:ss
     */
    @NonNull
    public static String getDurationText(Long currentPosition, Long duration){
        if(currentPosition == null || duration == null){
            return "--:--/--:--";
        }

        return String.format("%s/%s", convertMillisecondToTime(currentPosition),TimeUtil.convertMillisecondToTime(duration));
    }
}
