package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.ClasstabDaoI;
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
        String sql = "insert into classtab(c_name) values(?)";
        int update = jdbcTemplate.update(sql,classtab.getCName());
        return false;
    }

    @Override
    public boolean deleteClasstab(long id) {
        String sql = "delete from classtab where c_id = ?";
        int row = jdbcTemplate.update(sql, id);
        return false;
    }

    @Override
    public boolean updateClasstab(Classtab classtab) {
        String sql = "update classtab set classtab_name = ? where c_id = ?";
        int update = jdbcTemplate.update(sql,classtab.getCName(),classtab.getCId());
        return false;
    }


    @Override
    public List<Classtab> getClasstabList() {
        String sql = "select * from classtab";
        List<Classtab> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Classtab.class));
        return list;
    }
}
