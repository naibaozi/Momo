package com.team8504.csgo2web.controller;

import com.alibaba.fastjson.JSON;
import com.team8504.csgo2web.dao.NewsDao;
import com.team8504.csgo2web.entity.News;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Author: J.C.ZONG
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/09/26/上午9:08
 * Version 0.0
 */
@CrossOrigin
@WebServlet(name = "NewsServiet", urlPatterns = {"/news"})
public class NewsServiet extends HttpServlet {
    @Resource
    NewsDao newsDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (op != null){
            if (op.equals("querylist")){
                String lidStr = request.getParameter("lId");
                Integer lId = 1;//默认为1
                if (lidStr != null && !lidStr.equals("")){
                    lId = Integer.parseInt(lidStr);
                }
                List<News> newsList = newsDao.getNewsList(lId);
                String jsonString = JSON.toJSONString(newsList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
            if (op.equals("query")){
                List<News> newsList = newsDao.getNewsAll();
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                String jsonString = JSON.toJSONString(newsList);
                writer.write(jsonString);
                writer.flush();
                writer.close();
                System.out.println("查看栏目类型");
            }
            if(op.equals("querynews")){
                String nIdStr = request.getParameter("nId");
                Integer nId = 1;
                if (nIdStr != null && !nIdStr.equals("")){
                    nId = Integer.parseInt(nIdStr);
                }
                List<News> newsList = newsDao.getNews(nId);
                String jsonString = JSON.toJSONString(newsList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
        }
    }

}
