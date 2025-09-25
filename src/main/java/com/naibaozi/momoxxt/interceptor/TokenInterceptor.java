/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 20:36:00
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 09:53:48
 * @FilePath: src/main/java/com/naibaozi/momoxxt/interceptor/TokenInterceptor.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.interceptor;

import com.alibaba.fastjson.JSON;
import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
import com.naibaozi.momoxxt.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Token 拦截器：验证请求的 Token 有效性
 * 适配新的接口路径：拦截/user/*、/pet/* 和 /admin/* 等所有业务模块请求
 */
@WebFilter(urlPatterns = {"/user/*", "/pet/*", "/admin/*"}) // 确保包含了 /admin/*
public class TokenInterceptor implements Filter {

    @Resource
    private UserDao userDao;

    @Resource
    private JwtUtil jwtUtil;

    // 无需 Token 验证的接口（白名单）
    private static final String[] WHITE_LIST = {
            "user:login", "user:add", "user:sendEmailCode", "user:wechatLogin",
            "pet:getAllPetTypes", "pet:getPetTypesByParentId",
            "consult/user:createOrder", "consult/user:mockPay",
            "admin:login"  // 管理员登录接口
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String requestURI = request.getRequestURI();
        String module = getModuleFromUri(requestURI);
        String op = request.getParameter("op");

        System.out.println("\n--- 开始拦截请求: " + requestURI + " ---");

        // 1. 白名单判断：如果是白名单接口（如登录、注册），直接放行
        if (isInWhiteList(module, op)) {
            System.out.println("请求在白名单内，直接放行: " + module + ":" + op);
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 特殊接口放行（如聊天接口）
        if (requestURI.startsWith("/consult/chat/")) {
            System.out.println("聊天接口，直接放行: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 非白名单接口：执行Token验证
        String token = request.getHeader("Authorization");
        System.out.println("获取到的Authorization头: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("Token不存在或格式错误，拒绝访问。");
            sendErrorResponse(out, 401, "未登录，请先登录");
            return;
        }

        String pureToken = token.substring(7).trim();
        if (!jwtUtil.validateToken(pureToken)) {
            System.out.println("Token无效或已过期，拒绝访问。");
            sendErrorResponse(out, 401, "登录已过期，请重新登录");
            return;
        }

        // 4. Token验证通过：解析用户ID并放入请求属性
        Long userId = jwtUtil.getUserIdFromToken(pureToken);
        request.setAttribute("currentUserId", userId);
        System.out.println("Token验证成功！解析到userId: " + userId + "，已放入request属性。");

        // 5. 管理员接口权限校验（这一步必须在Token验证之后！）
        if (requestURI.startsWith("/admin/")) {
            System.out.println("检测到是管理员接口，开始校验管理员权限...");
            User user = userDao.getUserById(userId);

            if (user == null) {
                System.out.println("管理员校验失败：用户不存在，userId: " + userId);
                sendErrorResponse(out, 403, "无管理员权限");
                return;
            }

            if (user.getRole() != 1) {
                System.out.println("管理员校验失败：非管理员角色，userId: " + userId + ", role: " + user.getRole());
                sendErrorResponse(out, 403, "无管理员权限");
                return;
            }

            System.out.println("管理员校验通过！userId: " + userId + ", role: " + user.getRole());
            request.setAttribute("isAdmin", true);
        }

        // 6. 所有校验通过，放行请求
        System.out.println("所有校验通过，放行请求: " + requestURI);
        filterChain.doFilter(request, response);
    }

    // --- 以下是辅助方法，无需修改 ---
    private String getModuleFromUri(String uri) {
        if (uri == null || uri.isEmpty()) { return ""; }
        String[] parts = uri.split("/");
        return parts.length >= 2 ? parts[1] : "";
    }

    private boolean isInWhiteList(String module, String op) {
        if (module == null || op == null) { return false; }
        String key = module + ":" + op;
        for (String whiteKey : WHITE_LIST) {
            if (whiteKey.equals(key)) { return true; }
        }
        return false;
    }

    private void sendErrorResponse(PrintWriter out, int code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        out.write(JSON.toJSONString(error));
        out.flush();
        out.close();
    }

    @Override public void init(FilterConfig filterConfig) throws ServletException {}
    @Override public void destroy() {}
}