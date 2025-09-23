/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:18:44
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:18:44
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/ConsultDoctorDaoImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.ConsultDoctorDao;
import com.naibaozi.momoxxt.entity.ConsultDoctor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.util.List;

@Repository
public class ConsultDoctorDaoImpl implements ConsultDoctorDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public ConsultDoctor getDoctorByUserId(Integer userId) {
        String sql = "SELECT * FROM consult_doctor WHERE user_id = ?";
        List<ConsultDoctor> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultDoctor.class), userId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ConsultDoctor getDoctorById(Integer id) {
        String sql = "SELECT * FROM consult_doctor WHERE id = ?";
        List<ConsultDoctor> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultDoctor.class), id);
        return list.isEmpty() ? null : list.get(0);
    }
}