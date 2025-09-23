/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:26:42
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:56:43
 * @FilePath: src/main/java/com/naibaozi/momoxxt/websocket/ConsultWebSocket.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:26:42
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:56:43
 * @FilePath: src/main/java/com/naibaozi/momoxxt/websocket/ConsultWebSocket.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naibaozi.momoxxt.constant.ConsultConstant;
import com.naibaozi.momoxxt.entity.ConsultChat;
import com.naibaozi.momoxxt.service.ConsultChatService;
import com.naibaozi.momoxxt.util.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 问诊实时聊天WebSocket
 * 连接路径：ws://域名/consult/chat/{orderId}/{userId}/{senderType}
 * senderType：1=用户，2=医生
 */
@ServerEndpoint("/consult/chat/{orderId}/{userId}/{senderType}")
@Component
public class ConsultWebSocket {

    // 存储连接：key=orderId_userId_senderType，value=连接对象
    private static final Map<String, ConsultWebSocket> CONNECTIONS = new ConcurrentHashMap<>();

    // 静态注入Service（WebSocket特殊注入方式）
    private static ConsultChatService consultChatService;

    @Resource
    public void setConsultChatService(ConsultChatService service) {
        ConsultWebSocket.consultChatService = service;
    }

    // 会话对象
    private Session session;
    // 关联订单ID
    private Integer orderId;
    // 发送者ID
    private Integer userId;
    // 发送者类型
    private Integer senderType;

    /**
     * 连接建立时触发
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("orderId") Integer orderId,
                       @PathParam("userId") Integer userId,
                       @PathParam("senderType") Integer senderType) {
        this.session = session;
        this.orderId = orderId;
        this.userId = userId;
        this.senderType = senderType;

        // 存储连接
        String key = getConnectionKey(orderId, userId, senderType);
        CONNECTIONS.put(key, this);

        // 发送连接成功消息
        sendMessage(JSON.toJSONString(ResultUtil.success("聊天连接成功")));
    }

    /**
     * 收到客户端消息时触发
     */
    @OnMessage
    public void onMessage(String message) {
        try {
            // 解析消息参数
            JSONObject param = JSON.parseObject(message);
            Integer contentType = param.getInteger("contentType");
            String content = param.getString("content");

            // 1. 保存消息到数据库
            ConsultChat chat = consultChatService.sendMessage(
                    this.orderId,
                    this.userId,
                    this.senderType,
                    contentType,
                    content
            );

            // 2. 广播消息给同订单的所有连接
            broadcastMessage(JSON.toJSONString(ResultUtil.success(chat)));
        } catch (Exception e) {
            sendMessage(JSON.toJSONString(ResultUtil.error(500, "消息发送失败：" + e.getMessage())));
        }
    }

    /**
     * 连接关闭时触发
     */
    @OnClose
    public void onClose() {
        String key = getConnectionKey(orderId, userId, senderType);
        CONNECTIONS.remove(key);
        sendMessage(JSON.toJSONString(ResultUtil.success("聊天连接已关闭")));
    }

    /**
     * 连接异常时触发
     */
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
        sendMessage(JSON.toJSONString(ResultUtil.error(500, "聊天连接异常")));
    }

    /**
     * 发送消息给当前客户端
     */
    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播消息给同订单的所有连接
     */
    private void broadcastMessage(String message) {
        for (ConsultWebSocket connection : CONNECTIONS.values()) {
            if (connection.orderId.equals(this.orderId)) {
                connection.sendMessage(message);
            }
        }
    }

    /**
     * 生成连接唯一标识
     */
    private String getConnectionKey(Integer orderId, Integer userId, Integer senderType) {
        return orderId + "_" + userId + "_" + senderType;
    }
}