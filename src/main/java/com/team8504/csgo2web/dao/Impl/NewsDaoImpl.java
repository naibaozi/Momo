package com.team8504.csgo2web.dao;

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
        String sql = "INSERT INTO news(n_id,n_title,n_author,n_ctime,n_content,n_desc,n_thumb,n_img,n_address) VALUES(?,?,?,?,?,?,?,?,?)";
        int updateCount = jdbcTemplate.update(sql,
                news.getNId(),
                news.getNTitle(),
                news.getNAuthor(),
                news.getNCtime(),
                news.getNContent(),
                news.getNDesc(),
                news.getNThumb(),
                news.getNImg(),
                news.getNAddress()
        );
        System.out.println("添加产品信息成功，受影响行数："+updateCount);
        return false;
    }

    @Override
    public boolean deleteNews(long id) {
        return false;
    }

    @Override
    public boolean updateNews(News news) {
        return false;
    }

    @Override
    public List<News> getNewsList() {
        String sql = "SELECT * FROM news";
        BeanPropertyRowMapper<News> rowMapper = new BeanPropertyRowMapper<>(News.class);
        return jdbcTemplate.query(sql,rowMapper);

    }
}
