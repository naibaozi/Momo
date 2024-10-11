package com.team8504.csgo2web.controller;

import com.alibaba.fastjson.JSON;
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
 * Author: J.C.ZONG
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/09/26/上午9:08
 * Version 0.0
 */
@WebServlet("/classtab")
public class ClasstabServiet extends HttpServlet {
    @Resource
    ClasstabDao classtabDao;
    //查询新闻列表
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        String op = request.getParameter("op");
        if (op != null){
            if(op.equals("query")){
                List<Classtab> classtabList = classtabDao.getClasstabList();
                String jsonString = JSON.toJSONString(classtabList);
                writer.write(jsonString);
                writer.flush();
                writer.close();
                System.out.println("查看栏目类型");
            }
        }else {
            System.out.println("无效的业务操作");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        System.out.println("ClasstabServiet ="+request.getRequestURI());
    }
}
