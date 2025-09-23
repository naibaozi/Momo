/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:24:37
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:56:00
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/ConsultUserController.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:24:37
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:56:00
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/ConsultUserController.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naibaozi.momoxxt.entity.ConsultChat;
import com.naibaozi.momoxxt.entity.ConsultOrder;
import com.naibaozi.momoxxt.service.ConsultChatService;
import com.naibaozi.momoxxt.service.ConsultOrderService;
import com.naibaozi.momoxxt.util.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/consult/user/*")
public class ConsultUserController extends HttpServlet {

    @Resource
    private ConsultOrderService consultOrderService;

    @Resource
    private ConsultChatService consultChatService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String op = request.getParameter("op");
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");

        try {
            switch (op) {
                case "getOrders":
                    // 查询用户订单：支持按状态筛选
                    Integer status = request.getParameter("status") != null ? Integer.parseInt(request.getParameter("status")) : null;
                    List<ConsultOrder> orders = consultOrderService.getUserOrders(currentUserId, status);
                    out.write(ResultUtil.success(orders));
                    break;
                case "getChatRecords":
                    // 查询聊天记录
                    Integer orderId = Integer.parseInt(request.getParameter("orderId"));
                    List<ConsultChat> chats = consultChatService.getChatRecords(orderId, currentUserId);
                    out.write(ResultUtil.success(chats));
                    break;
                default:
                    out.write(ResultUtil.error(400, "无效的操作类型"));
            }
        } catch (Exception e) {
            out.write(ResultUtil.error(500, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String op = request.getParameter("op");
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        JSONObject param = JSON.parseObject(request.getReader().readLine());

        try {
            switch (op) {
                case "createOrder":
                    // 创建问诊订单
                    ConsultOrder order = consultOrderService.createOrder(
                            currentUserId,
                            param.getInteger("petId"),
                            param.getInteger("consultType"),
                            param.getString("symptom"),
                            param.getJSONArray("mediaUrls") != null ? param.getJSONArray("mediaUrls").toJavaList(String.class) : null,
                            param.getInteger("couponId")
                    );
                    out.write(ResultUtil.success(order));
                    break;
                case "mockPay":
                    // 模拟支付
                    Boolean payResult = consultOrderService.mockPay(param.getString("orderNo"), currentUserId);
                    out.write(ResultUtil.success(payResult));
                    break;
                case "sendMessage":
                    // 发送聊天消息
                    ConsultChat chat = consultChatService.sendMessage(
                            param.getInteger("orderId"),
                            currentUserId,
                            param.getInteger("senderType"),
                            param.getInteger("contentType"),
                            param.getString("content")
                    );
                    out.write(ResultUtil.success(chat));
                    break;
                default:
                    out.write(ResultUtil.error(400, "无效的操作类型"));
            }
        } catch (Exception e) {
            out.write(ResultUtil.error(500, e.getMessage()));
        }
    }
}