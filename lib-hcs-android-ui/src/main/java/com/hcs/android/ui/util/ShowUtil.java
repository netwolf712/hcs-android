package com.hcs.android.ui.util;

import android.view.View;

import com.hcs.android.common.util.StringUtil;

/**
 * 显示控制助手
 */
public class ShowUtil {
    /**
     * 将布尔型的是否显示转换为View关于是否显示的定义
     */
    public static int convertBooleanToVisibility(boolean bShow){
        return bShow ? View.VISIBLE : View.GONE;
    }

    /**
     * 根据字符串状态判断是否要显示
     * @param str 为空则不显示，否则显示
     */
    public static int visibilityWithStringState(String str){
        return StringUtil.isEmpty(str) ? View.GONE : View.VISIBLE;
    }
}
