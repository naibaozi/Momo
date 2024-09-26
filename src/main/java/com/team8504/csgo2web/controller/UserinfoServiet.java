package com.team8504.csgo2web.controller;
/**
 * @Author: Tao
 * @Project: csgo2web
 * @Date: 2024/09/26/10:25
 * @Version: 0.0
 */
import com.team8504.csgo2web.dao.UserinfoDao;
import com.team8504.csgo2web.entity.Userinfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/userinfo")
public class UserinfoServiet extends HttpServlet {
    //注入userinfo对象
    @Resource
    UserinfoDao userinfoDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("UserinfoServiet = "+request.getRequestURI());

        //查询用户列表
        List<Userinfo> userinfoList =  userinfoDao.getUserinfoList();
        response.setContentType("text/html;charset=UTF-8");//设置编码格式
        PrintWriter pw = response.getWriter();//创建输出流
        for (Userinfo userinfo : userinfoList) {
            pw.println(userinfo.toString());
            pw.println("<br>");//换行
        }
        //注册用户
        PrintWriter out = response.getWriter();
        //修改用户信息
        out.println("<html>");
        //登录验证
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("UserinfoServiet = "+request.getRequestURI());
    }
}
