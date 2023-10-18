package com.hcs.android.business.entity;

import com.hcs.android.common.util.StringUtil;

import java.util.List;

public class ListCompare <T>{
    public boolean compareLists(List<T> srcList, List<T> dstList, ICompare<T> iCompare){
        if(!compareListBase(srcList,dstList)){
            return false;
        }
        if(StringUtil.isEmpty(srcList) && StringUtil.isEmpty(dstList)){
            return true;
        }
        for(T srcObj : srcList){
            boolean find = false;
            for(T dstObj : dstList){
                if(iCompare.handleCompare(srcObj,dstObj)){
                    find = true;
                    break;
                }
            }
            if(!find){
                return false;
            }
        }
        return true;
    }

    /**
     * 根据数据长度简单比较两个列表是否一致
     * @return true 一致，false 不一致
     */
    private boolean compareListBase(List<T> srcList,List<T> dstList){
        if(StringUtil.isEmpty(srcList) && StringUtil.isEmpty(dstList)){
            return true;
        }
        if(StringUtil.isEmpty(srcList) && !StringUtil.isEmpty(dstList)){
            return false;
        }
        if(!StringUtil.isEmpty(srcList) && StringUtil.isEmpty(dstList)){
            return false;
        }
        return srcList.size() == dstList.size();
    }
}
