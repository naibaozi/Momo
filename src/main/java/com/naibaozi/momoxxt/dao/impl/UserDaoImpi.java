package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问层实现类
 * 基于JdbcTemplate实现CRUD操作（适配新 User 实体类）
 * @author naibaozi
 */
@Repository
public class UserDaoImpi implements UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<User> getUserinfoList() {
        // 1. 移除 nickname，新增 real_name、role、phone、avatar 等字段；别名适配实体类驼峰
        String sql = "SELECT " +
                "id, openid, username AS userName, password AS passWord, " +
                "real_name AS realName, avatar, role, phone, email, status, " +
                "created_at AS createdAt, updated_at AS updatedAt " +
                "FROM `user`";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User getUserById(Long id) {
        // 2. 同 list 逻辑，补充新字段和别名
        String sql = "SELECT " +
                "id, openid, username AS userName, password AS passWord, " +
                "real_name AS realName, avatar, role, phone, email, status, " +
                "created_at AS createdAt, updated_at AS updatedAt " +
                "FROM `user` WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        // 3. 适配 userName → username 字段查询
        String sql = "SELECT " +
                "id, openid, username AS userName, password AS passWord, " +
                "real_name AS realName, avatar, role, phone, email, status, " +
                "created_at AS createdAt, updated_at AS updatedAt " +
                "FROM `user` WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        // 4. 新增 SQL：移除 nickname，新增 real_name、role、phone、avatar 字段
        String sql = "INSERT INTO `user` (" +
                "`username`, `password`, `real_name`, `avatar`, " +
                "`role`, `phone`, `email`, `status`, `created_at`" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // 绑定参数：适配实体类的 userName、passWord、realName 等
                ps.setString(1, user.getUserName());    // username（实体类 userName → 数据库 username）
                ps.setString(2, user.getPassWord());    // password（实体类 passWord → 数据库 password）
                ps.setString(3, user.getRealName());    // real_name（实体类 realName 直接对应）
                ps.setString(4, user.getAvatar());      // avatar
                ps.setInt(5, user.getRole() != null ? user.getRole() : 1); // role（默认1-学生）
                ps.setString(6, user.getPhone());       // phone
                ps.setString(7, user.getEmail());       // email
                ps.setInt(8, user.getStatus() != null ? user.getStatus() : 1); // status（默认1-正常）

                return ps;
            }
        }, keyHolder);

        // 返回自增 ID（避免空指针）
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : -1;
    }

    @Override
    public Integer updateUser(User user) {
        // 5. 更新 SQL：移除 nickname，新增 real_name、role、phone、avatar 字段
        String sql = "UPDATE `user` SET " +
                "real_name = ?, avatar = ?, role = ?, " +
                "phone = ?, email = ?, status = ?, updated_at = NOW() " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql,
                user.getRealName(),   // real_name
                user.getAvatar(),     // avatar
                user.getRole(),       // role
                user.getPhone(),      // phone
                user.getEmail(),      // email
                user.getStatus(),     // status
                user.getId()          // 条件：id
        );
    }

    @Override
    public Integer updatePassword(Integer id, String newPassword) {
        // 6. 密码更新：适配实体类 passWord 字段（逻辑不变）
        String sql = "UPDATE `user` SET password = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newPassword, id);
    }

    @Override
    public Integer deleteUser(Integer id) {
        // 7. 删除逻辑不变
        String sql = "DELETE FROM `user` WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> getUserByPage(Integer pageNum, Integer pageSize, String username, Integer status) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;

        // 8. 分页 SQL：补充新字段和别名
        StringBuilder sql = new StringBuilder("SELECT " +
                "id, openid, username AS userName, password AS passWord, " +
                "real_name AS realName, avatar, role, phone, email, status, " +
                "created_at AS createdAt, updated_at AS updatedAt " +
                "FROM `user` WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // 动态条件（用户名模糊查询）
        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        // 分页
        sql.append(" LIMIT ?, ?");
        params.add(start);
        params.add(pageSize);

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(User.class), params.toArray());
    }

    @Override
    public Integer countUser(String username, Integer status) {
        // 9. 统计逻辑不变（仅查询 count，与字段新增无关）
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `user` WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND username LIKE :username");
            params.addValue("username", "%" + username.trim() + "%");
        }
        if (status != null) {
            sql.append(" AND status = :status");
            params.addValue("status", status);
        }

        return namedParameterJdbcTemplate.queryForObject(
                sql.toString(),
                params,
                Integer.class
        );
    }
}