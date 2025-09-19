package com.naibaozi.momoxxt;

import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class MomoXxtApplicationTests {



    @Resource
    JdbcTemplate jdbcTemplate;



}
