package com.team8504.csgo2web;

import com.team8504.csgo2web.entity.Userinfo;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
@SpringBootTest
class Csgo2webApplicationTests {



    @Resource
    JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
       List list = jdbcTemplate.query("select * from Userinfo", new BeanPropertyRowMapper(Userinfo.class));
       for(Object o:list){
           Userinfo products = (Userinfo) o;
           System.out.println(products.getUsername()+" "+products.getPassword());
       }

    }

}
