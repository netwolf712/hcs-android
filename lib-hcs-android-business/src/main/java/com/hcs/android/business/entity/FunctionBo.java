package com.hcs.android.business.entity;

/**
 * 功能对象
 */
public class FunctionBo {
    /**
     * 功能id
     */
    private Integer functionId;
    public Integer getFunctionId(){
        return functionId;
    }
    public void setFunctionId(Integer functionId){
        this.functionId = functionId;
    }

    /**
     * 功能名称
     */
    private String functionName;
    public String getFunctionName(){
        return functionName;
    }
    public void setFunctionName(String functionName){
        this.functionName = functionName;
    }
}
