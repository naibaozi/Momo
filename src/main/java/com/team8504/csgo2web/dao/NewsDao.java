package com.team8504.csgo2web.dao;

import com.team8504.csgo2web.entity.News;

import java.util.List;

/*
* 添加新闻信息
* @param news
* */
public interface NewsDao {
    //增
    public  boolean addNews(News news);
    //删
    public boolean deleteNews(long id);
    //改
    public boolean updateNews(News news);
    //查
    public List<News> getNewsList(Integer lId);
}
