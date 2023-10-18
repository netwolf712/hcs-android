package com.hcs.android.business.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.ResourceUtil;

/**
 * 更新方式枚举
 */
public enum UpdateTypeEnum {
    /**
     * 增量更新
     */
    UPDATE_NORMAL(0),
    /**
     * 暴力更新
     */
    UPDATE_FORCE(1),
    /**
     * 删除
     */
    UPDATE_DELETE(3);


    /**
     * 枚举值
     */
    private int value;

    UpdateTypeEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static UpdateTypeEnum findById(int id){
        for(UpdateTypeEnum filterEnum : UpdateTypeEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return UPDATE_NORMAL;
    }

    public int getValue(){
        return value;
    }
}
