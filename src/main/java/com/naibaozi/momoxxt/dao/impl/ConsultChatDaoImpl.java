/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:17:21
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:17:21
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/ConsultChatDaoImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.ConsultChatDao;
import com.naibaozi.momoxxt.entity.ConsultChat;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.util.List;

@Repository
public class ConsultChatDaoImpl implements ConsultChatDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer addChat(ConsultChat chat) {
        String sql = "INSERT INTO consult_chat (order_id, sender_id, sender_type, content_type, content, send_time) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql,
                chat.getOrderId(), chat.getSenderId(), chat.getSenderType(),
                chat.getContentType(), chat.getContent());
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    @Override
    public List<ConsultChat> getChatsByOrderId(Integer orderId) {
        String sql = "SELECT * FROM consult_chat WHERE order_id = ? ORDER BY send_time ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultChat.class), orderId);
    }
}