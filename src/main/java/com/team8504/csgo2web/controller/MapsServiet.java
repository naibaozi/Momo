package com.team8504.csgo2web.controller;

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
 * @Author: J.C.ZONG
 * @Package: com.team8504.csgo2web.controller
 * @Project: CSGO2WEB
 * @Date: 2024/09/26/上午9:08
 * @Version 0.0
 */
@WebServlet("/maps")
public class MapsServiet extends HttpServlet {
    @Resource
    MapsDao mapsDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        System.out.println("NewsServiet = "+request.getRequestURI());
        //查询新闻列表
        List<Maps> MapsList =  mapsDao.getMapsList();
        response.setContentType("text/html;charset=UTF-8");//设置编码格式
        PrintWriter pw = response.getWriter();//创建输出流
        for (Maps maps : MapsList) {
            pw.println(maps.toString());
            pw.println("<br>");//换行
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MapsServiet = "+request.getRequestURI());
    }

}
