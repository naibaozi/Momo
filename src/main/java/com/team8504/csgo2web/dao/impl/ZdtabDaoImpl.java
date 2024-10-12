package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.ZdtabDao;
import com.team8504.csgo2web.entity.Dy;
import com.team8504.csgo2web.entity.Zdtab;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* Author: zby
* Package: com.team8504.csgo2web.dao
* Project: CSGO2WEB
* Date: 2024/10/12/下午4:31
* Version 0.0
 *
*/
@Repository
public class ZdtabDaoImpl implements ZdtabDao {
    @Resource
    JdbcTemplate jdbcTemplate;
    /**
     * @param zId
     * @return
     */
    @Override
    public List<Zdtab> getZdtabList() {
        String sql = "select * from zdtab ";
        List<Zdtab> Zdtablist = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Zdtab>(Zdtab.class));
        return Zdtablist;

    }
    @Override
    public List<Zdtab> getZd(Integer zId){
        String sql = "select * from zdtab where z_id = ?";
        List<Zdtab> Zdtablist = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Zdtab>(Zdtab.class),zId);
        return Zdtablist;
    }

}
