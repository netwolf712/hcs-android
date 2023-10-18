package com.hcs.android.business.constant;

/**
 * 布尔的各种表示方法
 */
public class BooleanConstant {
    /**
     * 布尔类型
     */
    public final static boolean BOOLEAN_TRUE = true;
    public final static boolean BOOLEAN_FALSE = false;
    public static int booleanToInt(boolean b){
        if(b){
            return INTEGER_TRUE;
        }else{
            return INTEGER_FALSE;
        }
    }

    /**
     * 数值类型
     */
    public final static int INTEGER_TRUE = 1;
    public final static int INTEGER_FALSE = 0;
    public static boolean intToBoolean(int b){
        if(b == INTEGER_TRUE){
            return BOOLEAN_TRUE;
        }else{
            return BOOLEAN_FALSE;
        }
    }
    /**
     * 字符串类型
     */
    public final static String STRING_TRUE = "1";
    public final static String STRING_FALSE = "0";
}
