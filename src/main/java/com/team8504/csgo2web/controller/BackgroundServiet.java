package com.team8504.csgo2web.controller;

import com.team8504.csgo2web.dao.BackgroundDao;
import com.team8504.csgo2web.dao.MapsDao;
import com.team8504.csgo2web.entity.Background;
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
 * @Author: KYX
 * @Package: com.team8504.csgo2web.controller
 * @Project: FKJY-Text2
 * @Date: 2024/09/26/上午11:15
 * @Version: 0.0
 */
@WebServlet("/background")
public class BackgroundServiet extends HttpServlet {
    @Resource
    BackgroundDao backgroundDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        System.out.println("BackgroundServiet = "+request.getRequestURI());
        //查询新闻列表
        List<Background> BackgroundList =  backgroundDao.getBackgroundList();
        response.setContentType("text/html;charset=UTF-8");//设置编码格式
        PrintWriter pw = response.getWriter();//创建输出流
        for (Background background:BackgroundList) {
            pw.println(background.toString());
            pw.println("<br>");//换行
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("BackgroundServiet = "+request.getRequestURI());
    }

}
