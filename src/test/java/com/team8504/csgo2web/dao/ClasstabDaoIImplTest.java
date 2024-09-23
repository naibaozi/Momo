package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Classtab;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ClasstabDaoIImplTest {
    @Resource
    ClasstabDaoI classtabDaoI;

    @Test
    void insertClasstab() {
    }

    @Test
    void deleteClasstab() {
    }

    @Test
    void updateClasstab() {
    }

    @Test
    void getClasstabList() {
        List<Classtab> ClasstabList = classtabDaoI.getClasstabList();
        for (Classtab classtab : ClasstabList){
            System.out.println("classtab.getClasstabName()="+classtab.getClasstabName());
        }
    }
}