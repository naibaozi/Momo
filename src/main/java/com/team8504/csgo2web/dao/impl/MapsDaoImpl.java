package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.MapsDao;
import com.team8504.csgo2web.entity.Maps;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public  class MapsDaoImpl implements MapsDao {
    @Resource
    JdbcTemplate jdbcTemplate;
    @Override
    public List<Maps> getMapsList(Integer cId) {
        String sql = "SELECT * FROM maps WHERE c_id = ?";
        List<Maps> mapslist = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Maps.class),cId);
        return mapslist;
    }
}
