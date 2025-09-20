/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 20:36:00
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 21:08:49
 * @FilePath: src/main/java/com/naibaozi/momoxxt/interceptor/TokenInterceptor.java
 * @Description: 
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
 */
@WebFilter(urlPatterns = "/userinfo/*") // 拦截所有 /userinfo 开头的请求
public class TokenInterceptor implements Filter {

    // 无需 Token 验证的接口（白名单）
    // 新增 sendEmailCode 到白名单
    private static final String[] WHITE_LIST = {
            "login",         // 登录接口
            "add",           // 注册接口
            "sendEmailCode",  // 发送验证码接口
            "wechatLogin" //微信登录接口
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 设置响应格式（避免乱码）
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 2. 获取请求的 op 参数（操作类型）
        String op = request.getParameter("op");

        // 3. 白名单判断：如果是白名单接口，直接放行
        if (isInWhiteList(op)) {
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
     * 判断操作是否在白名单中
     */
    private boolean isInWhiteList(String op) {
        if (op == null) return false;
        for (String whiteOp : WHITE_LIST) {
            if (whiteOp.equals(op)) {
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
