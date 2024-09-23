package com.team8504.csgo2web;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
public  class TestConn {
    @Resource
    JdbcTemplate jdbcTemplate;
    @Test
         public void testConn() throws SQLException {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        if (conn != null){
            System.out.println("Connection successful"+"    "+conn);
        }else{
            System.out.println("Connection failed"+"    "+conn);
        }
        conn.close();
    }   
   
   
}
