package com.team8504.csgo2web.controller;

import com.alibaba.fastjson.JSON;
import com.team8504.csgo2web.dao.MapsDao;
import com.team8504.csgo2web.entity.Maps;
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
 * Author: Tao
 * Package: com.team8504.csgo2web.controller
 * Project: csgo2web
 * Date: 2024/09/26/13:38
 * Version: 0.0
 */
@WebServlet(name = "MapsServiet", urlPatterns = {"/maps"})
public class MapsServiet extends HttpServlet {
    @Resource
    MapsDao mapsDao;

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
                List<Maps>mapsList =mapsDao.getMapsList(cId);
                String jsonString = JSON.toJSONString(mapsList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);

            }
        }
    }

}
