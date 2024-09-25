package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.CommentDao;
import com.team8504.csgo2web.entity.Background;
import com.team8504.csgo2web.entity.Comment;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CommentDaoImpl implements CommentDao {
    @Resource
    JdbcTemplate jdbcTemplate;
    @Override
    public List<Comment> getCommentList() {
        String sql = "select * from comment";
        List<Comment> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Comment>(Comment.class));
        return list;
    }
}
