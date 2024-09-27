package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Userinfo;

import java.util.List;

public interface UserinfoDao {


    //用户注册
    void saveUserinfo(Userinfo userinfo);
    // 获取所有用户信息
   public List<Userinfo> getUserinfoList();
   //用户登录
   Userinfo login(String uEmail, String uPsd);
}
