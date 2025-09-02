package com.team8504.csgo2web.controller;

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

import static org.apache.coyote.http11.Constants.a;

/**
 * Author: J.C.ZONG
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/09/26/上午9:08
 * Version 0.0
 */
@WebServlet("/userinfo")
public class UserinfoServiet extends HttpServlet {
    //注入userinfo对象
    @Resource
    UserinfoDao userinfoDao;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("UserinfoServiet = " + request.getRequestURI());
        String op = request.getParameter("op");//获取操作类型
        if (op != null) {
            //查询用户列表
            if (op.equals("list_userinfo")) {
                list_userinfo(request, response);
            }
            //添加用户信息
            if (op.equals("add_userinfo")) {
                add_userinfo(request, response);
            }
            //修改用户信息
            if (op.equals("update_userinfo")) {
                //update_userinfo(request, response);
            }
            //删除用户信息
            if (op.equals("delete_userinfo")) {
                //delete_userinfo(request, response);
            }
            //登录验证
            if (op.equals("login")) {
                login_userinfo(request, response);
            }

        }else if (op == null){
            System.out.println("非法请求");
            response.sendRedirect(request.getContextPath()+"/404.html");
        }
    }

        //查询用户列表
        private void list_userinfo (HttpServletRequest request,HttpServletResponse response) throws IOException {
            List<Userinfo> userinfoList = userinfoDao.getUserinfoList();
            response.setContentType("text/html;charset=UTF-8");//设置编码格式
            PrintWriter pw = response.getWriter();//创建输出流
            for (Userinfo userinfo : userinfoList) {
                pw.println(userinfo.toString());
                pw.println("<br>");//换行
            }
        }
        //登录
        private void login_userinfo (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String uEmail = request.getParameter("uEmail");
            String uPsd = request.getParameter("uPsd");
            Userinfo userinfo = userinfoDao.login(uEmail,uPsd);
            System.out.println("userinfo = " + userinfo);
            if (userinfo != null){
                System.out.println("登录成功");
                response.sendRedirect(request.getContextPath()+"/index.html");//重定向主页
            }else{
                request.getRequestDispatcher("/login.html").forward(request, response);
            }

        }
        private void add_userinfo (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//            private long uId;
//            private String uNickname;
//            private String uPsd;
//            private String uEmail;
//            private java.util.Date uCreateTime;
//            private String uAvatar;
//            private String uSex;
            String uNickname = request.getParameter("uNickname");
            String uPsd = request.getParameter("uPsd");
            String uEmail = request.getParameter("uEmail");
            String uAvatar = request.getParameter("uAvatar");
            String uSex = request.getParameter("uSex");
            String userStatus = "1";
            Userinfo userinfo = new Userinfo();
            userinfo.setUNickname(uNickname);
            userinfo.setUPsd(uPsd);
            userinfo.setUEmail(uEmail);
            userinfo.setUAvatar(uAvatar);
            userinfo.setUSex(uSex);
            userinfo.setUStatus(userStatus);
            userinfo.setUCreateTime(new java.util.Date());
            userinfoDao.saveUserinfo(userinfo);

            if (userinfo!=null){
                System.out.println("添加用户成功");
                response.sendRedirect(request.getContextPath()+"/login.html");
            }else {
                System.out.println("添加用户失败");
                request.getRequestDispatcher("/404.html").forward(request, response);
            }

        }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("UserinfoServiet = "+request.getRequestURI());
        this.doGet(request,response);//重定向
    }
}
