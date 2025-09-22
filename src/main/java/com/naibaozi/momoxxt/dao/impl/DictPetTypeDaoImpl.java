/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:51:58
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:33
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/DictPetTypeDaoImpl.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:51:58
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:33
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/impl/DictPetTypeDaoImpl.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.DictPetTypeDao;
import com.naibaozi.momoxxt.entity.DictPetType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class DictPetTypeDaoImpl implements DictPetTypeDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<DictPetType> getAllPetTypes() {
        String sql = "SELECT id, type_name AS typeName, parent_id AS parentId, sort FROM dict_pet_type ORDER BY sort ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DictPetType.class));
    }

    @Override
    public DictPetType getPetTypeById(Integer id) {
        String sql = "SELECT id, type_name AS typeName, parent_id AS parentId, sort FROM dict_pet_type WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(DictPetType.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<DictPetType> getPetTypesByParentId(Integer parentId) {
        String sql = "SELECT id, type_name AS typeName, parent_id AS parentId, sort FROM dict_pet_type WHERE parent_id = ? ORDER BY sort ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DictPetType.class), parentId);
    }

    @Override
    public Integer addPetType(DictPetType dictPetType) {
        String sql = "INSERT INTO dict_pet_type (type_name, parent_id, sort) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dictPetType.getTypeName());
            ps.setInt(2, dictPetType.getParentId());
            ps.setInt(3, dictPetType.getSort());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public Integer updatePetType(DictPetType dictPetType) {
        String sql = "UPDATE dict_pet_type SET type_name = ?, parent_id = ?, sort = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                dictPetType.getTypeName(),
                dictPetType.getParentId(),
                dictPetType.getSort(),
                dictPetType.getId());
    }

    @Override
    public Integer deletePetType(Integer id) {
        String sql = "DELETE FROM dict_pet_type WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}