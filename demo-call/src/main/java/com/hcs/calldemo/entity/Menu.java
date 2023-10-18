package com.hcs.calldemo.entity;

/**
 * 菜单实体
 */
public class Menu {
    /**
     * 菜单名称
     */
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    /**
     * 菜单路由
     */
    private String router;
    public String getRouter(){
        return router;
    }
    public void setRouter(String router){
        this.router = router;
    }
}
