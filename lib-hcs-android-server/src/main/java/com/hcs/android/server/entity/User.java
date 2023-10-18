package com.hcs.android.server.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    @ColumnInfo(name = "username")
    private String username;
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    @ColumnInfo(name = "password")
    private String password;
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    @ColumnInfo(name = "token")
    private String token;
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    /**
     * 登录时间
     */
    @ColumnInfo(name = "loginTime")
    private Long loginTime;
    public Long getLoginTime(){
        return loginTime;
    }
    public void setLoginTime(Long loginTime){
        this.loginTime = loginTime;
    }

}
