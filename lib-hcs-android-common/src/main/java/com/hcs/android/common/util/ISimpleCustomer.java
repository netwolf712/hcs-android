package com.hcs.android.common.util;

/**
 * 仿Customer的简易customer
 * 主要是android对sdk版本有要求，版本24以上才能用
 */
public interface ISimpleCustomer<T> {
    void accept(T var1);
}
