package com.team8504.csgo2web.controller;

import com.alibaba.fastjson.JSON;
import com.team8504.csgo2web.dao.MapsDao;
import com.team8504.csgo2web.dao.ZdtabDao;
import com.team8504.csgo2web.entity.Maps;
import com.team8504.csgo2web.entity.Zdtab;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/10/12/下午4:34
 * Version 0.0
 */
@WebServlet(name = "ZdtabServiet", urlPatterns = {"/zdtab"})
public class ZdtabServiet extends HttpServlet {
    @Resource
    ZdtabDao zdtabDao ;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        this.doPost(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (op !=null) {
            if (op.equals("querylist")) {
                List<Zdtab> zdtabList =zdtabDao.getZdtabList();
                String jsonString = JSON.toJSONString(zdtabList);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);

            }
            if (op.equals("queryzd")){
                String zIdStr = request.getParameter("zId");
                Integer zId = 1;
                if (zIdStr != null && !zIdStr.equals("")){
                    zId = Integer.parseInt(zIdStr);
                }
                List<Zdtab> zdtab = zdtabDao.getZd(zId);
                String jsonString = JSON.toJSONString(zdtab);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(jsonString);

            }
        }
    }

}
