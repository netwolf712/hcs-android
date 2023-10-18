package com.hcs.android.business.service;

import java.util.List;

public interface IDbService<T> {
    /**
     * 清除全部
     */
    void deleteAll();

    /**
     * 插入全部
     */
    void insertAll(List<T> list);
}
