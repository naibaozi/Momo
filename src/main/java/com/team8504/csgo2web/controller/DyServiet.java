package com.team8504.csgo2web.controller;

import com.alibaba.fastjson.JSON;
import com.team8504.csgo2web.dao.DyDao;
import com.team8504.csgo2web.dao.NewsDao;
import com.team8504.csgo2web.entity.Dy;
import com.team8504.csgo2web.entity.News;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.List;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/10/12/下午4:18
 * Version 0.0
 */
@CrossOrigin
@WebServlet(name = "DyServiet", urlPatterns = {"/dy"})
public class DyServiet extends HttpServlet {
    @Resource
    DyDao dyDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (op != null){
            if (op.equals("querylist")){
                String zidStr = request.getParameter("zId");
                Integer zId = 1;//默认为1
                if (zidStr != null && !zidStr.equals("")){
                    zId = Integer.parseInt(zidStr);
                }
                List<Dy> dyList = dyDao.getDyList(zId);
                String jsonString = JSON.toJSONString(dyList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
            if(op.equals("querydy")){
                String dIdStr = request.getParameter("dId");
                Integer dId = 1;
                if (dIdStr != null && !dIdStr.equals("")){
                    dId = Integer.parseInt(dIdStr);
                }
                List<Dy> dy = dyDao.getDy(dId);
                String jsonString = JSON.toJSONString(dy);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);
            }
        }
    }

}