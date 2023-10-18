package com.hcs.android.server.service;

import com.hcs.android.server.config.Config;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.server.entity.LoginUser;
import com.hcs.android.server.entity.User;
import com.yanzhenjie.andserver.http.HttpRequest;

import java.util.HashSet;
import java.util.Set;

/**
 * 访问令牌控制服务
 */
public class TokenService {
    /**
     * 用于缓存登录的用户
     */
    private static HashSet<LoginUser> mLoginUserCache = new HashSet<>();
    private static TokenService mInstance = null;

    public static TokenService getInstance(){
        if(mInstance == null){
            synchronized (TokenService.class){
                if(mInstance == null) {
                    mInstance = new TokenService();
                }
            }
        }
        return mInstance;
    }
    /**
     * 从请求中获取token
     * @param request 请求
     * @return 脱去prefix的token
     */
    public String getToken(HttpRequest request){
        String token = request.getHeader(Config.TOKEN_HEADER);
        if(!StringUtil.isEmpty(token)){
            return token.replace(Config.TOKEN_PREFIX,"");
        }
        return null;
    }

    /**
     * 通过访问令牌找到用户
     * @param token 访问令牌
     * @return 用户
     */
    public LoginUser findUserByToken(String token){
        for(LoginUser loginUser : mLoginUserCache){
            if(token.equals(loginUser.getToken())){
                //如果有找到，还要查看是否超时了
                if(loginUser.getLastVisitTime() + Config.EXPIRE_TIME < System.currentTimeMillis()){
                    mLoginUserCache.remove(loginUser);
                    return null;
                }else{
                    return loginUser;
                }
            }
        }
        return null;
    }

    /**
     * 通过用户名找到用户
     * @param username 用户名
     * @return 用户
     */
    public LoginUser findUserByUsername(String username){
        for(LoginUser loginUser : mLoginUserCache){
            if(loginUser.getUsername().equals(username)){
                return loginUser;
            }
        }
        return null;
    }
    /**
     * 将登录用户加入缓存
     * @param user 登录用户
     */
    public void setLoginUser(User user){
        //先查找此用户是否已存在
        LoginUser loginUser = findUserByToken(user.getToken());
        if(loginUser == null){
            loginUser = findUserByUsername(user.getUsername());
        }
        if(loginUser != null){
            //如果已经存在，则更新token
            loginUser.setToken(user.getToken());
            //访问时间也更新过
            loginUser.setLastVisitTime(System.currentTimeMillis());
        }else{
            //不存在则重新创建
            loginUser = new LoginUser();
            loginUser.setUsername(user.getUsername());
            loginUser.setToken(user.getToken());
            loginUser.setLastVisitTime(System.currentTimeMillis());
            Set<String> permissions = new HashSet<>();
            permissions.add("*:*:*");
            loginUser.setPermissions(permissions);
            Set<String> roles = new HashSet<>();
            roles.add("admin");
            loginUser.setRoles(roles);
            mLoginUserCache.add(loginUser);
        }
    }

    /**
     * 移除登录用户缓存
     * @param username 用户名
     */
    public void removeUser(String username){
        LoginUser loginUser = findUserByUsername(username);
        if(loginUser != null){
            mLoginUserCache.remove(loginUser);
        }
    }
}
