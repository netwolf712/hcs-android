package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 床头屏功能列表
 */
public enum BedScreenFunctionEnum {
    /**
     * 医院简介
     */
    HOSPITAL_DESCRIPTION(0,"bed_screen_function_hospital_description"),
    /**
     * 科室简介
     */
    DEPARTMENT_DESCRIPTION(1,"bed_screen_function_department_description"),
    /**
     * 费用查询
     */
    EXPENSE_QUERY(2,"bed_screen_function_expense_query"),
    /**
     * 营养点餐
     */
    NUTRITIONAL_ORDERING(3,"bed_screen_function_nutritional_ordering"),
    /**
     * 服务评价
     */
    SERVICE_EVALUATION(4,"bed_screen_function_service_evaluation"),
    /**
     * 呼叫增援
     */
    CALL_REINFORCE(5,"bed_screen_function_call_reinforce"),
    /**
     * 呼叫护士站
     */
    CALL_NURSE_STATION(6,"bed_screen_function_call_nurse_station"),
    /**
     * 计时
     */
    TIMING(7,"bed_screen_function_timing"),
    /**
     * 自定义1
     */
    CUSTOM_1(21,"bed_screen_function_custom_1"),
    /**
     * 自定义2
     */
    CUSTOM_2(22,"bed_screen_function_custom_2"),
    /**
     * 自定义3
     */
    CUSTOM_3(23,"bed_screen_function_custom_3"),
    /**
     * 无效功能
     */
    FUNCTION_NONE(999,"bed_screen_function_none");

    private final String name;

    /**
     * 枚举值
     */
    private final int value;

    BedScreenFunctionEnum(int value,String name){
        this.value = value;
        this.name = name;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static BedScreenFunctionEnum findById(int id){
        for(BedScreenFunctionEnum filterEnum : BedScreenFunctionEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return HOSPITAL_DESCRIPTION;
    }

    /**
     * 返回该设备类型的文字说明
     */
    public String getName(){
        return name;
    }

    public String getDisplayName(@NonNull Context context){
        return context.getString(ResourceUtil.getStringId(context,name));
    }

    public int getValue(){
        return value;
    }
}
