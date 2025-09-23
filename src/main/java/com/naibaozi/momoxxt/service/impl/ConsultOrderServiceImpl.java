/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:22:10
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:22:10
 * @FilePath: src/main/java/com/naibaozi/momoxxt/service/impl/ConsultOrderServiceImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.service.impl;

import com.naibaozi.momoxxt.constant.ConsultConstant;
import com.naibaozi.momoxxt.dao.ConsultDoctorDao;
import com.naibaozi.momoxxt.dao.ConsultOrderDao;
import com.naibaozi.momoxxt.dao.UserPetDao;
import com.naibaozi.momoxxt.entity.ConsultDoctor;
import com.naibaozi.momoxxt.entity.ConsultOrder;
import com.naibaozi.momoxxt.entity.UserPet;
import com.naibaozi.momoxxt.service.ConsultOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultOrderServiceImpl implements ConsultOrderService {

    @Resource
    private ConsultOrderDao consultOrderDao;

    @Resource
    private UserPetDao userPetDao;

    @Resource
    private ConsultDoctorDao consultDoctorDao;

    /**
     * 生成唯一订单编号（CON+年月日+3位随机数）
     */
    private String generateOrderNo() {
        return "CON" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date()) + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultOrder createOrder(Integer userId, Integer petId, Integer consultType, String symptom, List<String> mediaUrls, Integer couponId) {
        // 1. 校验宠物归属
        UserPet pet = userPetDao.getUserPetById(Long.valueOf(petId));
        if (pet == null || !pet.getUserId().equals(userId)) {
            throw new RuntimeException("宠物不存在或不属于当前用户");
        }

        // 2. 计算问诊费用（普通59.9，紧急99.9）
        BigDecimal amount = new BigDecimal("59.90");
        if (ConsultConstant.CONSULT_TYPE_EMERGENCY.equals(consultType)) {
            amount = new BigDecimal("99.90");
        }

        // 3. 处理媒体URL（逗号分隔）
        String mediaUrlStr = mediaUrls != null ? String.join(",", mediaUrls) : "";

        // 4. 构建订单实体
        ConsultOrder order = new ConsultOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPetId(petId);
        order.setConsultType(consultType);
        order.setSymptom(symptom);
        order.setMediaUrl(mediaUrlStr);
        order.setAmount(amount);
        order.setCouponId(couponId == null ? 0 : couponId);
        order.setPayStatus(ConsultConstant.PAY_STATUS_UNPAID); // 初始未支付
        order.setConsultStatus(ConsultConstant.CONSULT_STATUS_PENDING); // 初始待接诊

        // 5. 保存订单
        Integer orderId = consultOrderDao.addOrder(order);
        order.setId(orderId);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean mockPay(String orderNo, Integer userId) {
        // 1. 校验订单
        ConsultOrder order = consultOrderDao.getOrderByNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权支付他人订单");
        }
        if (ConsultConstant.PAY_STATUS_PAID.equals(order.getPayStatus())) {
            throw new RuntimeException("订单已支付");
        }
        if (ConsultConstant.CONSULT_STATUS_CANCELED.equals(order.getConsultStatus())) {
            throw new RuntimeException("订单已取消，无法支付");
        }

        // 2. 更新支付状态
        ConsultOrder updateOrder = new ConsultOrder();
        updateOrder.setId(order.getId());
        updateOrder.setPayStatus(ConsultConstant.PAY_STATUS_PAID);
        updateOrder.setPayTime(new Date());
        Integer rows = consultOrderDao.updateOrderStatus(updateOrder);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleAccept(Integer doctorId, Integer orderId, Boolean accept) {
        // 1. 校验医生状态
        ConsultDoctor doctor = consultDoctorDao.getDoctorById(doctorId);
        if (doctor == null || doctor.getReceiveStatus() == 0) {
            throw new RuntimeException("医生不存在或未处于接诊状态");
        }

        // 2. 校验订单
        ConsultOrder order = consultOrderDao.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!ConsultConstant.PAY_STATUS_PAID.equals(order.getPayStatus())) {
            throw new RuntimeException("订单未支付，无法接诊");
        }
        if (!ConsultConstant.CONSULT_STATUS_PENDING.equals(order.getConsultStatus())) {
            throw new RuntimeException("订单状态异常，非待接诊状态");
        }

        // 3. 处理接诊/拒单
        ConsultOrder updateOrder = new ConsultOrder();
        updateOrder.setId(order.getId());
        if (accept) {
            // 接诊：绑定医生、更新接诊时间、状态改为接诊中
            updateOrder.setDoctorId(doctorId);
            updateOrder.setAcceptTime(new Date());
            updateOrder.setConsultStatus(ConsultConstant.CONSULT_STATUS_IN_PROGRESS);
        } else {
            // 拒单：状态改为已取消
            updateOrder.setConsultStatus(ConsultConstant.CONSULT_STATUS_CANCELED);
        }

        Integer rows = consultOrderDao.updateOrderStatus(updateOrder);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean finishConsult(Integer doctorId, Integer orderId) {
        // 1. 校验订单与医生权限
        ConsultOrder order = consultOrderDao.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作，非接诊医生");
        }
        if (!ConsultConstant.CONSULT_STATUS_IN_PROGRESS.equals(order.getConsultStatus())) {
            throw new RuntimeException("订单状态异常，非接诊中");
        }

        // 2. 更新状态为已完成
        ConsultOrder updateOrder = new ConsultOrder();
        updateOrder.setId(order.getId());
        updateOrder.setConsultStatus(ConsultConstant.CONSULT_STATUS_FINISHED);
        Integer rows = consultOrderDao.updateOrderStatus(updateOrder);
        return rows > 0;
    }

    @Override
    public List<ConsultOrder> getUserOrders(Integer userId, Integer consultStatus) {
        return consultOrderDao.getOrdersByUserId(userId, consultStatus);
    }

    @Override
    public List<ConsultOrder> getPendingOrders() {
        return consultOrderDao.getPendingAcceptOrders();
    }
}