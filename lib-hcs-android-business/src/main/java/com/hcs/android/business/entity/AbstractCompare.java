package com.hcs.android.business.entity;

/**
 * 编辑器
 */
public abstract class AbstractCompare {
    /**
     * 比较两者是否一致
     * 是则返回true，否则返回false
     */
    public abstract boolean compare(Object dst);
}
