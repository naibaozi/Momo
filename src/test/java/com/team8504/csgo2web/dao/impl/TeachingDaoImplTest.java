package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.TeachingDao;
import com.team8504.csgo2web.entity.News;
import com.team8504.csgo2web.entity.Teaching;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TeachingDaoImplTest {
    @Resource
   TeachingDao teachingDao;

    @Test
    void addTeaching() {
        Teaching teaching=new Teaching();
        teaching.setCId(1);
        teaching.setTTitle("测试");
        teaching.setTDesc("测试");
        teaching.setTAuthor("测试");
        teaching.setTContent("测试");
        teaching.setTThumb("");
        teaching.setTDate(new java.sql.Date(System.currentTimeMillis()));
        teaching.setTImg("");
        teaching.setTTag("测试");
        teachingDao.addTeaching(teaching);
        System.out.println("添加教学成功");

    }

    @Test
    void deleteTeaching() {
    }

    @Test
    void updateTeaching() {
    }

    @Test
    void getTeachingList() {
    }
}