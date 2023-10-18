package com.hcs.android.business.entity;

import com.hcs.android.business.constant.UpdateTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class ResponseList<T> extends ResponseListBase{
    private List<T> list;
    public List<T> getList(){
        return list;
    }
    public void setList(List<T> list){
        this.list = list;
    }

    public void setDefaultUpdateType(){
        setUpdateType(UpdateTypeEnum.UPDATE_NORMAL.getValue());
    }
    public ResponseList(){
        setDefaultUpdateType();
    }
    public ResponseList(List<T> list){
        setList(list);
        setDefaultUpdateType();
    }

    public ResponseList(T item){
        list = new ArrayList<>();
        list.add(item);
        setDefaultUpdateType();
    }
}
