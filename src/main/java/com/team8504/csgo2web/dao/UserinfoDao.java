package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Userinfo;

import java.util.List;

public interface UserinfoDao {


    // 获取所有用户信息
   public List<Userinfo> getUserinfoList();
}
