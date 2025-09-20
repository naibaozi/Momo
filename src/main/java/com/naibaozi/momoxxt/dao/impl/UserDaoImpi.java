package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.Map;

/**
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
        // 修正：表名改为user_base，openid改为open_id，补充nickname字段
        String sql = "SELECT " +
                "id, open_id AS openId, username AS userName, password AS passWord, " +
                "avatar, phone, email, status, nickname AS nickname, " +
                "create_time AS createTime, update_time AS updateTime, " +
                "(SELECT COUNT(*) FROM `order` WHERE user_id = u.id) AS orderCount, " +
                "(SELECT COUNT(*) FROM `note` WHERE author_id = u.id) AS noteCount " +
                "FROM `user_base` u";  // 表名修正为user_base
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }


    @Override
    public User getUserById(Long id) {
        // 只查询User实体中存在的字段，移除orderCount和noteCount子查询
        String sql = "SELECT " +
                "id, " +
                "open_id AS openId, " +
                "username AS userName, " +
                "password AS passWord, " +
                "avatar, " +
                "phone, " +
                "email, " +
                "status, " +
                "nickname, " +  // 数据库字段与实体属性名一致，无需别名
                "create_time AS createTime, " +
                "update_time AS updateTime " +
                "FROM `user_base` " +
                "WHERE id = ?";

        try {
            // 打印查询信息，便于调试
            System.out.println("执行用户查询SQL: " + sql);
            System.out.println("查询用户ID: " + id);

            // 执行查询
            User user = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    id
            );

            // 打印查询结果
            if (user != null) {
                System.out.println("查询成功 - 用户ID: " + user.getId() + ", 昵称: " + user.getNickName());
            } else {
                System.out.println("查询结果为空 - 用户ID: " + id);
            }
            return user;

        } catch (EmptyResultDataAccessException e) {
            // 明确捕获"无结果"异常，区别于其他错误
            System.out.println("数据库中不存在ID为 " + id + " 的用户");
            return null;
        } catch (Exception e) {
            // 捕获其他异常（如字段映射错误、SQL语法错误等）
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace(); // 打印堆栈信息，便于排查问题
            return null;
        }
    }


    @Override
    public User getUserByUsername(String username) {
        // 修正：openid改为open_id，补充nickname字段
        String sql = "SELECT " +
                "id, open_id AS openId, username AS userName, password AS passWord, " +
                "avatar, phone, email, status, nickname AS nickname, " +
                "create_time AS createTime, update_time AS updateTime " +
                "FROM `user_base` WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer addUser(User user) {
        // 修正：openid改为open_id，补充nickname字段（如果需要）
        String sql = "INSERT INTO user_base (" +
                "username, password, open_id, avatar, nickname, " +  // 字段名修正为open_id
                "phone, email, status, create_time, update_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";  // 调整参数数量

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassWord());
            ps.setString(3, user.getOpenId());
            ps.setString(4, user.getAvatar());
            ps.setString(5, user.getNickName());  // 新增nickname参数
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getEmail());
            ps.setInt(8, user.getStatus());  // 参数索引调整
            return ps;
        }, keyHolder);

        // 返回自增ID
        return keyHolder.getKey().intValue();
    }

    /**
     * 核心修改：动态更新用户信息（仅更新非空字段）
     * 解决：绑定微信时重置phone、status等字段的问题
     */
    @Override
    public Integer updateUser(User user) {
        // 1. 动态拼接SQL语句（仅包含非空字段）
        StringBuilder sql = new StringBuilder("UPDATE `user_base` SET update_time = NOW()");
        List<Object> params = new ArrayList<>();

        // 2. 逐个判断字段：仅当字段不为null时，才加入更新逻辑
        if (user.getOpenId() != null && !user.getOpenId().trim().isEmpty()) {
            sql.append(", open_id = ?");
            params.add(user.getOpenId());
        }
        if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
            sql.append(", avatar = ?");
            params.add(user.getAvatar());
        }
        // 昵称：仅当前端传递了新昵称时才更新（避免null覆盖原有值）
        if (user.getNickName() != null && !user.getNickName().trim().isEmpty()) {
            sql.append(", nickname = ?");
            params.add(user.getNickName());
        }
        // 手机号：仅当前端明确传递时才更新（绑定微信时不传递，故不更新）
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            sql.append(", phone = ?");
            params.add(user.getPhone());
        }
        // 状态：仅当前端传递了有效状态值时才更新（默认不更新）
        if (user.getStatus() != null) {
            sql.append(", status = ?");
            params.add(user.getStatus());
        }
        // 其他字段（如email）同理：仅非空才更新
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            sql.append(", email = ?");
            params.add(user.getEmail());
        }

        // 3. 拼接更新条件（必须包含用户ID，否则会更新所有用户）
        sql.append(" WHERE id = ?");
        params.add(user.getId()); // 最后添加用户ID参数

        // 4. 执行动态SQL并返回影响行数
        System.out.println("执行动态更新SQL: " + sql.toString());
        System.out.println("更新参数: " + params); // 调试日志：查看实际更新的字段和值
        return jdbcTemplate.update(sql.toString(), params.toArray());
    }


    @Override
    public Integer updatePassword(Long userId, String newPassword) {
        // 该SQL语句正确，无需修改
        String sql = "UPDATE `user_base` SET password = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newPassword, userId);
    }

    @Override
    public Integer deleteUser(Long userId) {
        // 该SQL语句正确，无需修改
        String sql = "DELETE FROM `user_base` WHERE id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    @Override
    public List<User> getUserByPage(Integer pageNum, Integer pageSize, String username, Integer status) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;

        // 修正：openid改为open_id，补充nickname字段
        StringBuilder sql = new StringBuilder("SELECT " +
                "id, open_id AS openId, username AS userName, password AS passWord, " +  // 修正为open_id
                "avatar, phone, email, status, nickname AS nickname, " +  // 补充nickname
                "create_time AS createTime, update_time AS updateTime, " +
                "(SELECT COUNT(*) FROM `order` WHERE user_id = u.id) AS orderCount, " +
                "(SELECT COUNT(*) FROM `note` WHERE author_id = u.id) AS noteCount " +
                "FROM `user_base` u WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        sql.append(" LIMIT ?, ?");
        params.add(start);
        params.add(pageSize);

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(User.class), params.toArray());
    }

    @Override
    public Integer countUser(String username, Integer status) {
        // 该SQL语句正确，无需修改
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `user_base` WHERE 1=1");
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


    @Override
    public Map<String, Object> getUserWithOrderStats(Long userId) {
        // 修正：表名和字段名适配
        String sql = "SELECT " +
                "u.*, " +
                "COUNT(o.id) AS order_count, " +
                "SUM(CASE WHEN o.order_status = 0 THEN 1 ELSE 0 END) AS pending_pay_count, " +
                "SUM(CASE WHEN o.order_status = 1 THEN 1 ELSE 0 END) AS pending_ship_count " +
                "FROM user_base u " +  // 确认表名正确
                "LEFT JOIN mall_order o ON u.id = o.user_id " +
                "WHERE u.id = ? " +
                "GROUP BY u.id";
        return jdbcTemplate.queryForMap(sql, userId);
    }

    @Override
    public Integer countUserNotes(Long userId) {
        // 该SQL语句正确，无需修改
        String sql = "SELECT COUNT(id) FROM community_note WHERE user_id = ? AND status = 1";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public Integer countUnreadNotices(Long userId) {
        // 该SQL语句正确，无需修改
        String sql = "SELECT COUNT(id) FROM notice WHERE user_id = ? AND is_read = 0";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public User getUserByEmail(String email) {
        // 修正：open_id字段名和补充nickname
        String sql = "SELECT " +
                "id, open_id AS openId, username AS userName, password AS passWord, " +  // 修正为open_id
                "avatar, phone, email, status, nickname AS nickname, " +  // 补充nickname
                "create_time AS createTime, update_time AS updateTime " +
                "FROM `user_base` WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
        } catch (Exception e) {
            // 当查询不到结果时会抛出异常，这里返回null表示用户不存在
            return null;
        }
    }

    @Override
    public User getUserByOpenId(String openid) {
        // SQL查询语句：根据open_id查询用户信息
        String sql = "SELECT " +
                "id, " +
                "open_id AS openId, " +  // 映射到实体类的openId字段
                "username AS userName, " +
                "password AS passWord, " +
                "avatar, " +
                "phone, " +
                "email, " +
                "status, " +
                "nickname AS nickName, " +  // 映射到实体类的nickName字段
                "create_time AS createTime, " +
                "update_time AS updateTime " +
                "FROM `user_base` " +
                "WHERE open_id = ?";  // 通过open_id字段查询

        try {
            // 打印调试信息
            System.out.println("执行openid查询SQL: " + sql);
            System.out.println("查询openid: " + openid);

            // 执行查询并返回结果
            User user = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    openid
            );

            // 打印查询结果
            if (user != null) {
                System.out.println("查询成功 - 用户ID: " + user.getId() + ", openid: " + user.getOpenId());
            } else {
                System.out.println("查询结果为空 - openid: " + openid);
            }
            return user;

        } catch (EmptyResultDataAccessException e) {
            // 捕获"无结果"异常，明确表示未查询到用户
            System.out.println("数据库中不存在openid为 " + openid + " 的用户");
            return null;
        } catch (Exception e) {
            // 捕获其他异常（如SQL错误、字段映射错误等）
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



}
