/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:21:14
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:34
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/UserPetDaoImpl.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:21:14
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:34
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/UserPetDaoImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.UserPetDao;
import com.naibaozi.momoxxt.entity.UserPet;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import jakarta.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserPetDaoImpl implements UserPetDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer addUserPet(UserPet userPet) {
        String sql = "INSERT INTO user_pet (" +
                "user_id, pet_name, pet_type_id, pet_age, pet_gender, " +
                "medical_history, is_default, create_time, update_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userPet.getUserId());
            ps.setString(2, userPet.getPetName());
            ps.setInt(3, userPet.getPetTypeId());
            ps.setString(4, userPet.getPetAge());
            ps.setInt(5, userPet.getPetGender());
            ps.setString(6, userPet.getMedicalHistory());
            ps.setInt(7, userPet.getIsDefault() != null ? userPet.getIsDefault() : 0);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public Integer deleteUserPet(Long id) {
        String sql = "DELETE FROM user_pet WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Integer updateUserPet(UserPet userPet) {
        StringBuilder sql = new StringBuilder("UPDATE user_pet SET update_time = NOW()");
        List<Object> params = new ArrayList<>();

        if (userPet.getPetName() != null && !userPet.getPetName().trim().isEmpty()) {
            sql.append(", pet_name = ?");
            params.add(userPet.getPetName());
        }
        if (userPet.getPetTypeId() != null) {
            sql.append(", pet_type_id = ?");
            params.add(userPet.getPetTypeId());
        }
        if (userPet.getPetAge() != null && !userPet.getPetAge().trim().isEmpty()) {
            sql.append(", pet_age = ?");
            params.add(userPet.getPetAge());
        }
        if (userPet.getPetGender() != null) {
            sql.append(", pet_gender = ?");
            params.add(userPet.getPetGender());
        }
        if (userPet.getMedicalHistory() != null) {
            sql.append(", medical_history = ?");
            params.add(userPet.getMedicalHistory());
        }
        if (userPet.getIsDefault() != null) {
            sql.append(", is_default = ?");
            params.add(userPet.getIsDefault());
        }

        sql.append(" WHERE id = ?");
        params.add(userPet.getId());

        return jdbcTemplate.update(sql.toString(), params.toArray());
    }

    @Override
    public UserPet getUserPetById(Long id) {
        String sql = "SELECT " +
                "id, user_id AS userId, pet_name AS petName, pet_type_id AS petTypeId, " +
                "pet_age AS petAge, pet_gender AS petGender, medical_history AS medicalHistory, " +
                "is_default AS isDefault, create_time AS createTime, update_time AS updateTime " +
                "FROM user_pet WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserPet.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UserPet> getUserPetsByUserId(Long userId) {
        String sql = "SELECT " +
                "id, user_id AS userId, pet_name AS petName, pet_type_id AS petTypeId, " +
                "pet_age AS petAge, pet_gender AS petGender, medical_history AS medicalHistory, " +
                "is_default AS isDefault, create_time AS createTime, update_time AS updateTime " +
                "FROM user_pet WHERE user_id = ? ORDER BY is_default DESC, create_time DESC";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserPet.class), userId);
    }

    @Override
    public Integer setDefaultPet(Long userId, Long petId) {
        // 先将用户所有宠物设为非默认
        jdbcTemplate.update("UPDATE user_pet SET is_default = 0, update_time = NOW() WHERE user_id = ?", userId);
        // 再将目标宠物设为默认
        return jdbcTemplate.update("UPDATE user_pet SET is_default = 1, update_time = NOW() WHERE id = ?", petId);
    }
}