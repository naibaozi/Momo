package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.NewsDao;
import com.team8504.csgo2web.entity.News;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class NewsDaoImpl implements NewsDao {
    @Resource
    JdbcTemplate jdbcTemplate;
      /*
      * 添加新闻
      * @param news
      * */
    @Override
    public boolean addNews(News news) {
        String sql = "insert into news(n_title,n_author,n_ctime,n_content,n_desc,n_thumb,n_img,n_address,c_id) values(?,?,?,?,?,?,?,?,?)";
        int update = jdbcTemplate.update(sql,news.getNTitle(),news.getNAuthor(),news.getNCtime(),news.getNContent(),news.getNDesc(),news.getNThumb(),news.getNImg(),news.getNAddress(),news.getCId());
        System.out.println("添加新闻成功，受影响行数："+update);

        return false;
    }

    @Override
    public boolean deleteNews(long id) {
        String sql = "delete from news where n_id = ?";
        int row = jdbcTemplate.update(sql, id);
        System.out.println("删除新闻成功，受影响行数："+row);
        return false;
    }

    @Override
    public boolean updateNews(News news) {
        String sql = "update news set n_title = ?,n_author = ?,n_ctime = ?,n_content = ?,n_desc = ?,n_thumb = ?,n_img = ?,n_address = ?,c_id = ? where n_id = ?";
        int update = jdbcTemplate.update(sql,news.getNTitle(),news.getNAuthor(),news.getNCtime(),news.getNContent(),news.getNDesc(),news.getNThumb(),news.getNImg(),news.getNAddress(),news.getNId(),news.getCId());
        System.out.println("更新新闻成功，受影响行数："+update);
        return false;
    }

    @Override
    public List<News> getNewsList() {
        String sql = "SELECT * FROM news";
        BeanPropertyRowMapper<News> rowMapper = new BeanPropertyRowMapper<>(News.class);
        return jdbcTemplate.query(sql,rowMapper);

    }
}
