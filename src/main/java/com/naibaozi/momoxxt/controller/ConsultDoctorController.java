/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:25:39
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:56:00
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/ConsultDoctorController.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:25:39
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:56:00
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/ConsultDoctorController.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naibaozi.momoxxt.dao.ConsultDoctorDao;
import com.naibaozi.momoxxt.entity.ConsultChat;
import com.naibaozi.momoxxt.entity.ConsultDoctor;
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

@WebServlet("/consult/doctor/*")
public class ConsultDoctorController extends HttpServlet {

    @Resource
    private ConsultOrderService consultOrderService;

    @Resource
    private ConsultChatService consultChatService;

    @Resource
    private ConsultDoctorDao consultDoctorDao;

    /**
     * 获取当前登录医生ID（从用户ID转换）
     */
    private Integer getCurrentDoctorId(Integer userId) {
        ConsultDoctor doctor = consultDoctorDao.getDoctorByUserId(userId);
        if (doctor == null) {
            throw new RuntimeException("非医生账号，无权操作");
        }
        return doctor.getId();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String op = request.getParameter("op");
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        Integer doctorId = getCurrentDoctorId(currentUserId);

        try {
            switch (op) {
                case "getPendingOrders":
                    // 查询待接诊订单
                    List<ConsultOrder> pendingOrders = consultOrderService.getPendingOrders();
                    out.write(ResultUtil.success(pendingOrders));
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
        Integer doctorId = getCurrentDoctorId(currentUserId);
        JSONObject param = JSON.parseObject(request.getReader().readLine());

        try {
            switch (op) {
                case "handleAccept":
                    // 接诊/拒单
                    Boolean accept = param.getBoolean("accept");
                    Boolean result = consultOrderService.handleAccept(doctorId, param.getInteger("orderId"), accept);
                    out.write(ResultUtil.success(result));
                    break;
                case "finishConsult":
                    // 结束问诊
                    Boolean finishResult = consultOrderService.finishConsult(doctorId, param.getInteger("orderId"));
                    out.write(ResultUtil.success(finishResult));
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