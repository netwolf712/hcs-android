package com.hcs.android.server.entity;


import com.hcs.android.common.util.log.KLog;

/**
 * 客户端发过来的命令
 */
public class CommandBo {
    public CommandBo(String token,String username,Integer commandId,String commandName)
    {
        this.token = token;
        this.username = username;
        this.commandId = commandId;
        String[] strArr = commandName.split("\\@");
        if (strArr.length == 1) {
            this.commandName = commandName;
            this.commandPara = null;
        }
        else if (strArr.length == 2)
        {
            this.commandName = strArr[0];
            this.commandPara = strArr[1];
        }
        else
        {
            KLog.e("commandName:" + commandName +" error!");
        }
    }
    /**
     * 登录时的token
     */
    private String token;
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    private String username;
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * 命令id
     */
    private Integer commandId;
    public Integer getCommandId(){
        return commandId;
    }
    public void setCommandId(Integer commandId){
        this.commandId = commandId;
    }

    /**
     * 命令名称
     */
    private String commandName;
    public String getCommandName(){
        return commandName;
    }
    public void setCommandName(String commandName){
        this.commandName = commandName;
    }

    /**
     * 命令参数
     */
    private String commandPara;
    public String getCommandPara(){
        return commandPara;
    }
    public void setCommandPara(String commandPara){
        this.commandPara = commandPara;
    }

}
