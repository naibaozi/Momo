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
            if (op.equals("query")){
                String cidStr = request.getParameter("cId");
                Integer cId = 1;//默认为1
                if (cidStr != null && !cidStr.equals("")){
                    cId = Integer.parseInt(cidStr);
                }
                List<News> newsList = newsDao.getNewsList(cId);
                String jsonString = JSON.toJSONString(newsList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
        }
    }

}
