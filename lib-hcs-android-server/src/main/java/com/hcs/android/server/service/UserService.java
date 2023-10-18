package com.hcs.android.server.service;

import com.hcs.android.common.util.Md5Util;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.server.config.Config;
import com.hcs.android.server.dao.DataBaseHelper;
import com.hcs.android.server.dao.UserDao;
import com.hcs.android.server.entity.User;

public class UserService {
    private UserDao userDao = DataBaseHelper.getInstance().userDao();
    /**
     * 根据用户名、密码返回访问令牌
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌
     */
    private String getToken(String username,String password){
        String orgData = username + System.currentTimeMillis() + password;
        return Md5Util.encrypt(orgData);
    }
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 带token的user
     */
    public User login(String username,String password){
        User user = userDao.findOne(username,password);
        if(user != null){
            user.setToken(getToken(username,password));
            user.setLoginTime(System.currentTimeMillis());
            userDao.update(user);
            TokenService.getInstance().setLoginUser(user);
        }
        return user;
    }

    public User getUserByToken(String token){
        if(StringUtil.isEmpty(token)){
            return null;
        }
        return userDao.getUserByToken(token);
    }

    public void updateUser(User user){
        userDao.update(user);
        TokenService.getInstance().setLoginUser(user);
    }

    /**
     * 检测密码，主要用于一些高危操作
     * @param token 登录的token
     * @param password 密码
     * @return true表示验证正确
     */
    public boolean checkPassword(String token,String password){
        User user = getUserByToken(token);
        if(user == null){
            return false;
        }
        return user.getPassword().equals(password);
    }

    /**
     * 服务器登录
     */
    public User serviceLogin(){
        return login(Config.DEFAULT_SERVICE_WEB_USERNAME,Config.DEFAULT_SERVICE_WEB_PASSWORD);
    }
}
