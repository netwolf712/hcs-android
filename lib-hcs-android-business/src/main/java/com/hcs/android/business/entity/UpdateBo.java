package com.hcs.android.business.entity;

import com.hcs.android.business.constant.UpdateTypeEnum;

public class UpdateBo {
    /**
     * 更新模式，对应UpdateTypeEnum
     */
    private Integer updateType = UpdateTypeEnum.UPDATE_NORMAL.getValue();
    public Integer getUpdateType(){
        return updateType;
    }
    public void setUpdateType(Integer updateType){
        this.updateType = updateType;
    }
}
