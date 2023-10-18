package com.hcs.android.annotation.api;

/**
 * 命令管理器
 */
public interface ICommandManager {
    /**
     * 处理命令
     */
    Object handleCommand(String commandId,Object data);
}
