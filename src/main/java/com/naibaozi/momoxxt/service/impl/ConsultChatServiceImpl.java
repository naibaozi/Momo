/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:23:26
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:23:26
 * @FilePath: src/main/java/com/naibaozi/momoxxt/service/impl/ConsultChatServiceImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.service.impl;

import com.naibaozi.momoxxt.constant.ConsultConstant;
import com.naibaozi.momoxxt.dao.ConsultChatDao;
import com.naibaozi.momoxxt.dao.ConsultOrderDao;
import com.naibaozi.momoxxt.dao.ConsultDoctorDao;
import com.naibaozi.momoxxt.entity.ConsultChat;
import com.naibaozi.momoxxt.entity.ConsultOrder;
import com.naibaozi.momoxxt.service.ConsultChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class ConsultChatServiceImpl implements ConsultChatService {

    @Resource
    private ConsultChatDao consultChatDao;

    @Resource
    private ConsultOrderDao consultOrderDao;

    @Resource
    private ConsultDoctorDao consultDoctorDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultChat sendMessage(Integer orderId, Integer senderId, Integer senderType, Integer contentType, String content) {
        // 1. 校验订单存在性与状态
        ConsultOrder order = consultOrderDao.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (ConsultConstant.CONSULT_STATUS_CANCELED.equals(order.getConsultStatus()) ||
                ConsultConstant.CONSULT_STATUS_FINISHED.equals(order.getConsultStatus())) {
            throw new RuntimeException("订单已结束，无法发送消息");
        }

        // 2. 校验发送者权限
        boolean hasPermission = false;
        if (ConsultConstant.SENDER_TYPE_USER.equals(senderType)) {
            // 用户：必须是订单所属用户
            hasPermission = order.getUserId().equals(senderId);
        } else if (ConsultConstant.SENDER_TYPE_DOCTOR.equals(senderType)) {
            // 医生：必须是接诊医生且订单处于接诊中
            hasPermission = order.getDoctorId() != null && order.getDoctorId().equals(senderId)
                    && ConsultConstant.CONSULT_STATUS_IN_PROGRESS.equals(order.getConsultStatus());
        }
        if (!hasPermission) {
            throw new RuntimeException("无权发送消息");
        }

        // 3. 保存消息
        ConsultChat chat = new ConsultChat();
        chat.setOrderId(orderId);
        chat.setSenderId(senderId);
        chat.setSenderType(senderType);
        chat.setContentType(contentType);
        chat.setContent(content);
        Integer chatId = consultChatDao.addChat(chat);
        chat.setId(chatId);
        return chat;
    }

    @Override
    public List<ConsultChat> getChatRecords(Integer orderId, Integer userId) {
        // 1. 校验订单
        ConsultOrder order = consultOrderDao.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 校验权限（用户/医生均可查看）
        boolean isUser = order.getUserId().equals(userId);
        boolean isDoctor = order.getDoctorId() != null && order.getDoctorId().equals(userId);
        if (!isUser && !isDoctor) {
            throw new RuntimeException("无权查看此聊天记录");
        }

        // 3. 查询记录
        return consultChatDao.getChatsByOrderId(orderId);
    }
}