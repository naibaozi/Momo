package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.UserinfoDao;
import com.team8504.csgo2web.entity.Userinfo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserinfoDaoImplTest {
    @Resource
    UserinfoDao userinfoDao;

    @Test
    void getUserinfoList() {
        List<Userinfo> userinfoList = userinfoDao.getUserinfoList();
        for (Userinfo userinfo : userinfoList) {
            System.out.println(userinfo);
        }
    }
}