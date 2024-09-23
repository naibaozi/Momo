package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.Classtab;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ClasstabDaoIImpl implements ClasstabDaoI {
    @Resource
    JdbcTemplate jdbcTemplate;
    @Override
    public boolean insertClasstab(Classtab classtab) {
        return false;
    }

    @Override
    public boolean deleteClasstab(long id) {
        return false;
    }

    @Override
    public boolean updateClasstab(Classtab classtab) {
        return false;
    }

    @Override
    public List<Classtab> getClasstabList() {
        String sql = "select * from classtab";
        List<Classtab> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Classtab.class));
        return list;
    }
}
