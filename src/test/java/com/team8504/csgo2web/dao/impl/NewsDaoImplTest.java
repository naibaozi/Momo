package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.NewsDao;
import com.team8504.csgo2web.entity.News;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NewsDaoImplTest {
    @Resource
    NewsDao newsDao;

    @Test
    void addNews() {
        News news = new News();
        news.setCId(1);
        news.setNTitle("测试标题");
        news.setNAuthor("测试作者");
        news.setNContent("测试内容");
        news.setNCtime(new java.sql.Date(System.currentTimeMillis()));
        news.setNDesc("测试简介");
        news.setNThumb("测试缩略图片地址");
        news.setNImg("测试图片地址");
        news.setNAddress("测试发布地址");
        newsDao.addNews(news);
        System.out.println("添加新闻成功");
    }

    @Test
    void deleteNews() {
        newsDao.deleteNews(1);
        System.out.println("删除新闻成功");
    }

    @Test
    void updateNews() {
        News news = new News();
        news.setNId(1);
        news.setNTitle("测试标题3");
        news.setNAuthor("测试作者2");
        news.setNContent("测试内容2");
        news.setNCtime(new java.sql.Date(System.currentTimeMillis()));
        news.setNDesc("测试简介2");
        news.setNThumb("测试缩略图片地址2");
        news.setNImg("测试图片地址2");
        news.setNAddress("测试发布地址2");
        news.setCId(1);
        newsDao.updateNews(news);
        System.out.println("修改新闻成功");
    }

    @Test
    void getNewsList() {
        List<News> newsList = newsDao.getNewsList(1);
       for (News news:newsList){
           System.out.println("新闻信息"+news.toString());
       }
    }
}