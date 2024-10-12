package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.DyDao;
import com.team8504.csgo2web.entity.Dy;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.dao
 * Project: CSGO2WEB
 * Date: 2024/10/12/下午4:11
 * Version 0.0
 */
@Repository
public class DyDaoImpl implements DyDao {
    @Resource
    JdbcTemplate jdbcTemplate;
    /**
     * @param dId
     * @return
     */
    @Override
    public List<Dy> getDyList(Integer zId) {
        String sql = "select * from dy where z_id = ?";
        List<Dy> Dylist = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Dy>(Dy.class),zId);
        return Dylist;
    }
    @Override
    public List<Dy> getDy(Integer dId) {
        String sql = "select * from dy where d_id = ?";
        List<Dy> Dylist = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Dy>(Dy.class),dId);
        return Dylist;
    }
}
