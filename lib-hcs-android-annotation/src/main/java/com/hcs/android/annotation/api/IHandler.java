package com.hcs.android.annotation.api;

/**
 * 处理器
 */
public interface IHandler {
    /**
     * 处理动作
     * @param data 代处理的数据
     * @return 自定义返回内容
     */
    Object handle(Object data);
}
