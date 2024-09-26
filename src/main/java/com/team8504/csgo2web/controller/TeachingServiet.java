package com.team8504.csgo2web.controller;

import com.team8504.csgo2web.dao.TeachingDao;
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
 * @Author: J.C.ZONG
 * @Package: com.team8504.csgo2web.controller
 * @Project: CSGO2WEB
 * @Date: 2024/09/26/上午9:08
 * @Version 0.0
 */
@WebServlet("/teaching")
public class TeachingServiet extends HttpServlet {
    @Resource
    TeachingDao teachingDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("TeachingServiet = "+request.getRequestURI());

        //查询用户列表
        List<Teaching> teachingList =  teachingDao.getTeachingList();
        response.setContentType("text/html;charset=UTF-8");//设置编码格式
        PrintWriter pw = response.getWriter();//创建输出流
        for (Teaching teaching : teachingList) {
            pw.println(teaching.toString());
            pw.println("<br>");//换行
        }
        //注册用户

        //修改用户信息

        //登录验证
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("TeachingServiet = "+request.getRequestURI());
    }


}
