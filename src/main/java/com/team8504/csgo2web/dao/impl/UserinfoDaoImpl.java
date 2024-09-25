package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.UserinfoDao;
import com.team8504.csgo2web.entity.Userinfo;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserinfoDaoImpl implements UserinfoDao {
    @Resource
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Userinfo> getUserinfoList(){
        String sql = "select * from userinfo";
        List<Userinfo> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Userinfo.class));
        return list;
    }
}
