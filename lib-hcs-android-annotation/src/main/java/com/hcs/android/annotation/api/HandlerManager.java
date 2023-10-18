package com.hcs.android.annotation.api;

/**
 * 事件管理器
 */
public class HandlerManager {
    private ICommandManager mCommandManager = null;

//    private static class HandlerManagerHolder{
//        private static final HandlerManager INSTANCE = new HandlerManager();
//    }
//    public static HandlerManager getInstance(){
//        return HandlerManagerHolder.INSTANCE;
//    }

    public HandlerManager(String basePackageName){
        try {
            Class<?> clazz = Class.forName(basePackageName + ".CommandManagerImpl");
            mCommandManager = (ICommandManager)clazz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 处理命令
     *
     * @param commandId 命令id
     * @param commandData 命令数据
     */
    public Object handleCommand(String commandId,Object commandData){
        if(mCommandManager == null){
            System.out.println("load CommandManager implement failed");
            return null;
        }
        return mCommandManager.handleCommand(commandId,commandData);
    }

    public Object handleCommand(String basePackageName,String commandId,Object commandData){
        try {
            Class<?> clazz = Class.forName(basePackageName + ".annotation.api.CommandManagerImpl");
            ICommandManager commandManager = (ICommandManager)clazz.newInstance();
            return commandManager.handleCommand(commandId,commandData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
