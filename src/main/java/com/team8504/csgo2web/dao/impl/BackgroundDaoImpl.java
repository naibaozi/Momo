package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.BackgroundDao;
import com.team8504.csgo2web.entity.Background;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class BackgroundDaoImpl implements BackgroundDao {
    @Resource
    JdbcTemplate jdbcTemplate;
    @Override
    public List<Background> getBackgroundList() {
        String sql = "select * from background";
        List<Background> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Background>(Background.class));
        return list;
    }
}
