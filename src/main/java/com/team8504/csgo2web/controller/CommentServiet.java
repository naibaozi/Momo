package com.team8504.csgo2web.controller;


import com.team8504.csgo2web.dao.CommentDao;
import com.team8504.csgo2web.entity.Comment;
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
@WebServlet("/comment")
public class CommentServiet extends HttpServlet {
    @Resource
    CommentDao commentDao;
    //查询新闻列表
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        System.out.println("CommentServiet ="+request.getRequestURI());
        List<Comment> commentList = commentDao.getCommentList();
        response.setContentType("text/html;charset=UTF-8");//设置编码格式
        PrintWriter pw = response.getWriter();//创建输出流
        for (Comment comment : commentList) {
            pw.println(comment.toString());
            pw.println("<br>");//换行
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        System.out.println("CommentServiet ="+request.getRequestURI());
    }
}
