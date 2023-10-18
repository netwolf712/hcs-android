package com.hcs.android.business.entity;

import com.hcs.android.business.constant.UpdateTypeEnum;

/**
 * 列表请求回复的基础对象
 */
public class ResponseListBase extends RequestListBase{
    /**
     * 总记录条数
     */
    private Integer totalLength;
    public Integer getTotalLength(){
        return totalLength;
    }
    public void setTotalLength(Integer totalLength){
        this.totalLength = totalLength;
    }

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
