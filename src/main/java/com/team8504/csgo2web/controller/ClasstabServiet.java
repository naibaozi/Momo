package com.team8504.csgo2web.controller;

import com.team8504.csgo2web.dao.ClasstabDao;
import com.team8504.csgo2web.entity.Classtab;
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
* @Author: Tao
* @Package: com.team8504.csgo2web.controller
* @Project: csgo2web
* @Date: 2024/09/26/13:38
* @Version: 0.0
*/
@WebServlet("/classtab")
public class ClasstabServiet extends HttpServlet {
    @Resource
    ClasstabDao classtabDao;
    //查询新闻列表
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        System.out.println("ClasstabServiet ="+request.getRequestURI());
        List<Classtab> classtabList = classtabDao.getClasstabList();
        response.setContentType("text/html;charset=UTF-8");//设置编码格式
        PrintWriter pw = response.getWriter();//创建输出流
        for (Classtab classtab : classtabList) {
            pw.println(classtab.toString());
            pw.println("<br>");//换行
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        System.out.println("ClasstabServiet ="+request.getRequestURI());
    }
}
