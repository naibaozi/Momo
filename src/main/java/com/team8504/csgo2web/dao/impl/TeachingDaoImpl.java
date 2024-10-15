package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.TeachingDao;
import com.team8504.csgo2web.entity.News;
import com.team8504.csgo2web.entity.Teaching;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeachingDaoImpl implements TeachingDao {
    @Resource
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean addTeaching(Teaching teaching) {
        String sql = "insert into teaching(t_title,t_desc,t_thumb,t_date,t_author,t_content,t_img,t_tag,c_id) values(?,?,?,?,?,?,?,?,?)";
        int update = jdbcTemplate.update(sql,teaching.getTTitle(),teaching.getTDesc(),teaching.getTThumb(),teaching.getTDate(),teaching.getTAuthor(),teaching.getTContent(),teaching.getTImg(),teaching.getTTag(),teaching.getCId());
        System.out.println("添加教学成功，受影响行数："+update);
        return false;
    }

    @Override
    public boolean deleteTeaching(Integer tId) {
        String sql = "delete from teaching where t_id = ?";
        int row = jdbcTemplate.update(sql, tId);
        System.out.println("删除教学成功，受影响行数："+row);
        return false;
    }

    @Override
    public boolean updateTeaching(Teaching teaching) {
        String sql = "update teaching set t_title = ?,t_desc = ?,t_thumb = ?,t_date = ?,t_author = ?,t_content = ?,t_img = ?,t_tag = ?,c_id = ? where t_id = ?";
        int update = jdbcTemplate.update(sql,teaching.getTTitle(),teaching.getTDesc(),teaching.getTThumb(),teaching.getTDate(),teaching.getTAuthor(),teaching.getTContent(),teaching.getTImg(),teaching.getTTag(),teaching.getCId(),teaching.getTId());
        System.out.println("修改教学成功，受影响行数："+update);
        return false;
    }

    @Override
    public List<Teaching> getTeachingList(Integer cId) {
        String sql = "SELECT * FROM teaching WHERE c_id = ?";
        List<Teaching> teachingList= jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Teaching.class),cId);
        return teachingList;
    }

    @Override
    public List<Teaching> getCourseList(Integer tId) {
        String sql= "SELECT * FROM teaching WHERE t_id =?";
        List<Teaching> contextList=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Teaching.class),tId);
        return contextList;
    }
}
