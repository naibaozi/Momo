/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 20:36:00
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 10:05:23
 * @FilePath: src/main/java/com/naibaozi/momoxxt/interceptor/TokenInterceptor.java
 * @Description: 适配新接口路径的Token拦截器，支持/user/*和/pet/*等模块
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.interceptor;

import com.alibaba.fastjson.JSON;
import com.naibaozi.momoxxt.util.JwtUtil;
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
 * 适配新的接口路径：拦截/user/*和/pet/*等所有业务模块请求
 */
@WebFilter(urlPatterns = {"/user/*", "/pet/*"}) // 拦截用户模块和宠物模块的所有请求
public class TokenInterceptor implements Filter {

    // 无需 Token 验证的接口（白名单），格式："模块名:操作名"
    private static final String[] WHITE_LIST = {
            "user:login",         // 用户登录
            "user:add",           // 用户注册
            "user:sendEmailCode", // 发送邮箱验证码
            "user:wechatLogin",   // 微信登录
            "pet:getAllPetTypes", // 获取宠物品类（公开接口）
            "pet:getPetTypesByParentId", // 获取子品类（公开接口）
            "consult/user:createOrder", // 创建问诊订单（未登录拦截器已处理）
            "consult/user:mockPay"      // 模拟支付
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 设置响应格式（避免乱码）
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 2. 解析请求的模块名和操作类型
        String requestURI = request.getRequestURI(); // 例如：/user 或 /pet
        String module = getModuleFromUri(requestURI); // 提取模块名：user 或 pet
        String op = request.getParameter("op");      // 提取操作类型：login、add等

         
        if (requestURI.startsWith("/consult/chat/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 白名单判断：如果是白名单接口，直接放行
        if (isInWhiteList(module, op)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 非白名单接口：获取请求头中的 Token
        String token = request.getHeader("Authorization");

        // 5. Token 基础校验
        if (token == null || token.trim().isEmpty()) {
            sendErrorResponse(out, 401, "未登录，请先登录");
            return;
        }

        // 6. 移除 Token 前缀（前端传递的格式是 "Bearer xxx"）
        if (!token.startsWith("Bearer ")) {
            sendErrorResponse(out, 401, "Token 格式错误");
            return;
        }
        String pureToken = token.substring(7).trim(); // 截取 "Bearer " 后的纯 Token

        // 7. 验证 Token 有效性（过期、篡改均会返回 false）
        if (!JwtUtil.validateToken(pureToken)) {
            sendErrorResponse(out, 401, "登录已过期，请重新登录");
            return;
        }

        // 8. Token 有效：解析用户ID并放入请求属性（后续接口可直接获取）
        Long userId = JwtUtil.getUserIdFromToken(pureToken);
        request.setAttribute("currentUserId", userId); // 存储当前登录用户ID

        // 9. 放行请求（进入 Servlet 处理）
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求URI中提取模块名
     * 例如：/user → user，/pet → pet
     */
    private String getModuleFromUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            return "";
        }
        String[] parts = uri.split("/");
        return parts.length >= 2 ? parts[1] : "";
    }

    /**
     * 判断操作是否在白名单中
     * 白名单格式："模块名:操作名"，例如"user:login"
     */
    private boolean isInWhiteList(String module, String op) {
        if (module == null || op == null) {
            return false;
        }
        String key = module + ":" + op;
        for (String whiteKey : WHITE_LIST) {
            if (whiteKey.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送错误响应（与 Servlet 保持一致）
     */
    private void sendErrorResponse(PrintWriter out, int code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        out.write(JSON.toJSONString(error));
        out.flush();
        out.close();
    }

    // 初始化和销毁方法（默认实现即可）
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    @Override
    public void destroy() {}
}
    