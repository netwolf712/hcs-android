package com.hcs.android.server.entity;

import java.util.Set;

/**
 * 用于记录登录用户的信息
 */
public class LoginUser {
    /**
     * 对应的用户名
     */
    private String username;
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    /**
     * 访问令牌
     */
    private String token;
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    /**
     * 最后一次访问时间
     */
    private Long lastVisitTime;
    public Long getLastVisitTime(){
        return lastVisitTime;
    }
    public void setLastVisitTime(Long lastVisitTime){
        this.lastVisitTime = lastVisitTime;
    }

    /**
     * 权限列表
     * 这里当前没有权限管理
     * 默认给所有权限
     */
    private Set<String> permissions;
    public Set<String> getPermissions(){
        return permissions;
    }
    public void setPermissions(Set<String> permissions){
        this.permissions = permissions;
    }

    /**
     * 角色列表
     * 这里当前没有角色管理
     * 默认给admin权限
     */
    private Set<String> roles;
    public Set<String> getRoles(){
        return roles;
    }
    public void setRoles(Set<String> roles){
        this.roles = roles;
    }
}
