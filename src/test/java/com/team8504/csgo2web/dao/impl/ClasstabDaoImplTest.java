package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.ClasstabDao;
import com.team8504.csgo2web.entity.Classtab;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ClasstabDaoImplTest {
    @Resource
    ClasstabDao classtabDaoI;

    @Test
    void insertClasstab() {
        Classtab classtab = new Classtab();
        classtab.setCName("test2");
        classtab.setCId(2);
        classtabDaoI.insertClasstab(classtab);
        System.out.println("insert success");
    }

    @Test
    void deleteClasstab() {
        classtabDaoI.deleteClasstab(1);
        System.out.println("delete success");
    }

    @Test
    void updateClasstab() {
        Classtab classtab = new Classtab();
        classtab.setCName("test2");
        classtab.setCId(1);
        classtabDaoI.updateClasstab(classtab);
        System.out.println("update success");

    }

}