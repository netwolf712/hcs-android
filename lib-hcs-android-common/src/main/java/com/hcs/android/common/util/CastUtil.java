package com.hcs.android.common.util;

/**
 * 强制转换
 * 只为了强制转换不显示警告
 */
public interface CastUtil {
    @SuppressWarnings("unchecked")
    static <T> T cast(Object object) {
        return (T) object;
    }
}
