package com.team8504.csgo2web.controller;

import com.alibaba.fastjson.JSON;
import com.team8504.csgo2web.dao.TeachingDao;
import com.team8504.csgo2web.entity.Maps;
import com.team8504.csgo2web.entity.Teaching;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Author: yin
 * Package: com.team8504.csgo2web.controller
 * Project: FKJY-Text
 * Date: 2024/09/27/上午9:25
 * Version: 0.0
 */
@WebServlet(name = "TeachingServiet", urlPatterns = {"/teaching"})
public class TeachingServiet extends HttpServlet {
    @Resource
    TeachingDao teachingDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        this.doPost(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (op !=null) {
            if (op.equals("query")) {
                String cidStr = request.getParameter("cId");
                Integer cId=1;
                if (cidStr != null && !cidStr.equals("")){
                    cId = Integer.parseInt(cidStr);
                }
                List<Teaching>teachingList =teachingDao.getTeachingList(cId);
                String jsonString = JSON.toJSONString(teachingList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
            if(op.equals("queryCourse")){
                String tidStr =request.getParameter("tId");
                Integer tId = 1 ;
                if (tidStr !=null && !tidStr.equals("")){
                    tId = Integer.parseInt(tidStr);
                }
                List<Teaching> courseList = teachingDao.getCourseList(tId);
                String jsonString = JSON.toJSONString(courseList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
        }
    }


}
