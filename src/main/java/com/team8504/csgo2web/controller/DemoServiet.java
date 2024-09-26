package com.team8504.csgo2web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @Author: J.C.ZONG
 * @Package: com.team8504.csgo2web.controller
 * @Project: CSGO2WEB
 * @Date: 2024/09/26/上午9:08
 * @Version 0.0
 */
@WebServlet(name = "DemoServiet", urlPatterns = {"/demo"})
public class DemoServiet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("这是doGet请求"+request.getRequestURI());
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("这是doPost请求"+request.getRequestURI());
    }
}
