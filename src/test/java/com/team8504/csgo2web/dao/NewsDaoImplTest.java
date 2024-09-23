package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.News;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class NewsDaoImplTest {
    @Resource
    NewsDao newsDao;

    @Test
    void addNews() {
        News news = new News();
        news.setNId(1);
        news.setNTitle("测试标题");
        news.setNAuthor("测试作者");
        news.setNContent("测试内容");
        news.setNCtime(new java.sql.Date(System.currentTimeMillis()));
        news.setNDesc("测试简介");
        news.setNThumb("测试缩略图片地址");
        news.setNImg("测试图片地址");
        news.setNAddress("测试发布地址");
        news.setClasstabId("测试所属一级分类");
        newsDao.addNews(news);
        System.out.println("添加新闻成功");
    }

    @Test
    void deleteNews() {
    }

    @Test
    void updateNews() {
    }

    @Test
    void getNewsList() {
        List<News> newsList = newsDao.getNewsList();
       for (News news:newsList){
           System.out.println("新闻信息"+news.toString());
       }
    }
}