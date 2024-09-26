package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.BackgroundDao;
import com.team8504.csgo2web.entity.Background;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BackgroundDaoImplTest {
    @Resource
    private BackgroundDao backgroundDao;
    @Test
    void getBackgroundList() {
        List<Background> list = backgroundDao.getBackgroundList();
        for (Background background : list) {
            System.out.println(background);
        }
    }
}