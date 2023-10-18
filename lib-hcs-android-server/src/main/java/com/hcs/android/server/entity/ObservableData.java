package com.hcs.android.server.entity;

import java.util.Observable;

/**
 * 可订阅的数据
 */
public class ObservableData<T> extends Observable {
    /**
     * 数据内容
     */
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
        this.setChanged();
        this.notifyObservers(t);
    }
}
