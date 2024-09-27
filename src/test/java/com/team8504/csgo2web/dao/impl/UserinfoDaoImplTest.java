package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.UserinfoDao;
import com.team8504.csgo2web.entity.Userinfo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserinfoDaoImplTest {
    @Resource
    UserinfoDao userinfoDao;

    @Test
    void saveUserinfo() {
        Userinfo userinfo = new Userinfo();
        userinfo.setUNickname("kyx");
        userinfo.setUEmail("123456789@qq.com");
        userinfo.setUPsd("123456");
        userinfo.setUCreateTime(new java.util.Date());
        userinfo.setUAvatar(" ");
        userinfo.setUSex("男");
        userinfo.setUStatus("1");
        userinfoDao.saveUserinfo(userinfo);
        System.out.println("注册成功");
    }
    @Test
    void getUserinfoList() {
        List<Userinfo> userinfoList = userinfoDao.getUserinfoList();
        for (Userinfo userinfo : userinfoList) {
            System.out.println(userinfo);
        }
    }
    @Test
    void login() {
        Userinfo userinfo = userinfoDao.login("123456789@qq.com","123456");
        System.out.println(userinfo);
    }

}