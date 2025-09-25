/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:16:05
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 08:15:40
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/ConsultOrderDaoImpl.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:16:05
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 08:15:40
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/ConsultOrderDaoImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.ConsultOrderDao;
import com.naibaozi.momoxxt.entity.ConsultOrder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.util.List;

@Repository
public class ConsultOrderDaoImpl implements ConsultOrderDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer addOrder(ConsultOrder order) {
        String sql = "INSERT INTO consult_order (order_no, user_id, pet_id, doctor_id, consult_type, symptom, media_url, amount, coupon_id, pay_status, consult_status, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql,
                order.getOrderNo(), order.getUserId(), order.getPetId(), order.getDoctorId(),
                order.getConsultType(), order.getSymptom(), order.getMediaUrl(), order.getAmount(),
                order.getCouponId(), order.getPayStatus(), order.getConsultStatus());
        // 查询自增ID
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    @Override
    public ConsultOrder getOrderByNo(String orderNo) {
        String sql = "SELECT * FROM consult_order WHERE order_no = ?";
        List<ConsultOrder> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultOrder.class), orderNo);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<ConsultOrder> getOrdersByUserId(Integer userId, Integer consultStatus) {
        String sql = "SELECT * FROM consult_order WHERE user_id = ?";
        if (consultStatus != null) {
            sql += " AND consult_status = ?";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultOrder.class), userId, consultStatus);
        }
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultOrder.class), userId);
    }

    @Override
    public List<ConsultOrder> getPendingAcceptOrders() {
        // 待接诊：已支付（pay_status=1）、待接诊（consult_status=0）、医生可接诊（receive_status=1）
        String sql = "SELECT co.* FROM consult_order co " +
                "LEFT JOIN consult_doctor cd ON co.doctor_id = cd.id " +
                "WHERE co.pay_status = 1 AND co.consult_status = 0 AND cd.receive_status = 1 " +
                "ORDER BY co.create_time ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultOrder.class));
    }

    @Override
    public Integer updateOrderStatus(ConsultOrder order) {
        // 动态拼接更新字段（避免空值覆盖）
        StringBuilder sql = new StringBuilder("UPDATE consult_order SET update_time = NOW()");
        if (order.getPayStatus() != null) sql.append(", pay_status = ?");
        if (order.getPayTime() != null) sql.append(", pay_time = ?");
        if (order.getConsultStatus() != null) sql.append(", consult_status = ?");
        if (order.getDoctorId() != null) sql.append(", doctor_id = ?");
        if (order.getAcceptTime() != null) sql.append(", accept_time = ?");
        sql.append(" WHERE id = ?");

        // 组装参数
        List<Object> params = new java.util.ArrayList<>();
        if (order.getPayStatus() != null) params.add(order.getPayStatus());
        if (order.getPayTime() != null) params.add(order.getPayTime());
        if (order.getConsultStatus() != null) params.add(order.getConsultStatus());
        if (order.getDoctorId() != null) params.add(order.getDoctorId());
        if (order.getAcceptTime() != null) params.add(order.getAcceptTime());
        params.add(order.getId());

        return jdbcTemplate.update(sql.toString(), params.toArray());
    }

    @Override
    public ConsultOrder getOrderById(Integer id) {
        String sql = "SELECT * FROM consult_order WHERE id = ?";
        List<ConsultOrder> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultOrder.class), id);
        return list.isEmpty() ? null : list.get(0);
    }
}