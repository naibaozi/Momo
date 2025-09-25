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
 * 基于JdbcTemplate实现CRUD操作（适配新 User 实体类与 user_base 表字段）
 * @author naibaozi
 */
@Repository
public class UserDaoImpi implements UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // ====================== 原有方法：适配新增字段 ======================
    @Override
    public List<User> getUserinfoList() {
        // 适配：补充 real_name、gender、audit_status 等新增字段，排除 password 敏感字段
        String sql = "SELECT " +
                "id, open_id AS openId, username AS userName, " +  // 移除 password 字段，避免泄露
                "avatar, phone, email, status, nickname AS nickName, " +
                "role, real_name AS realName, gender, age, " +  // 新增医生审核相关字段
                "id_card_front_url AS idCardFrontUrl, id_card_back_url AS idCardBackUrl, " +
                "audit_status AS auditStatus, audit_remark AS auditRemark, " +
                "create_time AS createTime, update_time AS updateTime, " +
                "(SELECT COUNT(*) FROM mall_order WHERE user_id = u.id) AS orderCount, " +  // 修正表名：order→mall_order（避免关键字冲突）
                "(SELECT COUNT(*) FROM community_note WHERE author_id = u.id) AS noteCount " +  // 修正表名：note→community_note
                "FROM `user_base` u";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User getUserById(Long id) {
        // 适配：补充 realName、gender、auditStatus 等字段，移除 password 敏感字段
        String sql = "SELECT " +
                "id, " +
                "open_id AS openId, " +
                "username AS userName, " +  // 移除 password 字段，避免查询时泄露
                "avatar, " +
                "role, " +
                "phone, " +
                "email, " +
                "status, " +
                "nickname AS nickName, " +
                "real_name AS realName, " +  // 新增：真实姓名
                "gender, " +  // 新增：性别
                "age, " +  // 新增：年龄
                "id_card_front_url AS idCardFrontUrl, " +  // 新增：身份证正面URL
                "id_card_back_url AS idCardBackUrl, " +  // 新增：身份证背面URL
                "audit_status AS auditStatus, " +  // 新增：审核状态
                "audit_remark AS auditRemark, " +  // 新增：审核备注
                "create_time AS createTime, " +
                "update_time AS updateTime " +
                "FROM `user_base` " +
                "WHERE id = ?";

        try {
            System.out.println("执行用户查询SQL: " + sql);
            System.out.println("查询用户ID: " + id);

            User user = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    id
            );

            if (user != null) {
                System.out.println("查询成功 - 用户ID: " + user.getId() + ", 姓名: " + user.getRealName() + ", 审核状态: " + user.getAuditStatus());
            } else {
                System.out.println("查询结果为空 - 用户ID: " + id);
            }
            return user;

        } catch (EmptyResultDataAccessException e) {
            System.out.println("数据库中不存在ID为 " + id + " 的用户");
            return null;
        } catch (Exception e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        // 适配：补充 realName、auditStatus 等字段，移除 password 敏感字段
        String sql = "SELECT " +
                "id, open_id AS openId, username AS userName, " +
                "avatar, phone, email, status, nickname AS nickName, " +
                "role, real_name AS realName, audit_status AS auditStatus, " +  // 新增核心字段
                "create_time AS createTime, update_time AS updateTime " +
                "FROM `user_base` WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        } catch (Exception e) {
            System.err.println("根据用户名查询失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer addUser(User user) {
        // 适配：补充 real_name、gender、audit_status 等新增字段（医生注册时需提交）
        String sql = "INSERT INTO user_base (" +
                "username, password, open_id, avatar, nickname, " +
                "phone, email, status, role, " +  // 原有字段
                "real_name, gender, age, id_card_front_url, id_card_back_url, " +  // 新增医生身份字段
                "audit_status, audit_remark, " +  // 新增审核字段（默认待审核）
                "create_time, update_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // 原有参数（1-9）
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassWord());
            ps.setString(3, user.getOpenId());
            ps.setString(4, user.getAvatar());
            ps.setString(5, user.getNickName());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getEmail());
            ps.setInt(8, user.getStatus() == null ? 1 : user.getStatus());  // 默认正常状态
            ps.setInt(9, user.getRole() == null ? 0 : user.getRole());      // 默认普通用户
            // 新增参数（10-16）
            ps.setString(10, user.getRealName() == null ? "" : user.getRealName());  // 真实姓名（医生必填）
            ps.setInt(11, user.getGender() == null ? 0 : user.getGender());          // 性别（默认未知）
            ps.setString(12, user.getAge() == null ? "" : user.getAge());            // 年龄（默认空）
            ps.setString(13, user.getIdCardFrontUrl() == null ? "" : user.getIdCardFrontUrl()); // 身份证正面
            ps.setString(14, user.getIdCardBackUrl() == null ? "" : user.getIdCardBackUrl());   // 身份证背面
            ps.setInt(15, user.getAuditStatus() == null ? 0 : user.getAuditStatus());          // 默认待审核
            ps.setString(16, user.getAuditRemark() == null ? "" : user.getAuditRemark());      // 审核备注（默认空）
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public Integer updateUser(User user) {
        // 适配：新增 realName、gender、auditStatus 等字段的动态更新逻辑
        StringBuilder sql = new StringBuilder("UPDATE `user_base` SET update_time = NOW()");
        List<Object> params = new ArrayList<>();

        // 原有字段更新逻辑（保持不变）
        if (user.getOpenId() != null && !user.getOpenId().trim().isEmpty()) {
            sql.append(", open_id = ?");
            params.add(user.getOpenId());
        }
        if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
            sql.append(", avatar = ?");
            params.add(user.getAvatar());
        }
        if (user.getNickName() != null && !user.getNickName().trim().isEmpty()) {
            sql.append(", nickname = ?");
            params.add(user.getNickName());
        }
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            sql.append(", phone = ?");
            params.add(user.getPhone());
        }
        if (user.getStatus() != null) {
            sql.append(", status = ?");
            params.add(user.getStatus());
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            sql.append(", email = ?");
            params.add(user.getEmail());
        }

        // 新增：医生身份相关字段更新（如修改身份证照片、真实姓名）
        if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            sql.append(", real_name = ?");
            params.add(user.getRealName());
        }
        if (user.getGender() != null) {
            sql.append(", gender = ?");
            params.add(user.getGender());
        }
        if (user.getAge() != null && !user.getAge().trim().isEmpty()) {
            sql.append(", age = ?");
            params.add(user.getAge());
        }
        if (user.getIdCardFrontUrl() != null && !user.getIdCardFrontUrl().trim().isEmpty()) {
            sql.append(", id_card_front_url = ?");
            params.add(user.getIdCardFrontUrl());
        }
        if (user.getIdCardBackUrl() != null && !user.getIdCardBackUrl().trim().isEmpty()) {
            sql.append(", id_card_back_url = ?");
            params.add(user.getIdCardBackUrl());
        }

        // 新增：审核状态与备注更新（管理员操作）
        if (user.getAuditStatus() != null) {
            sql.append(", audit_status = ?");
            params.add(user.getAuditStatus());
        }
        if (user.getAuditRemark() != null && !user.getAuditRemark().trim().isEmpty()) {
            sql.append(", audit_remark = ?");
            params.add(user.getAuditRemark());
        }

        // 角色更新（原有逻辑补充，可选）
        if (user.getRole() != null) {
            sql.append(", role = ?");
            params.add(user.getRole());
        }

        // 必须包含用户ID条件
        sql.append(" WHERE id = ?");
        params.add(user.getId());

        System.out.println("执行动态更新SQL: " + sql.toString());
        System.out.println("更新参数: " + params);
        return jdbcTemplate.update(sql.toString(), params.toArray());
    }

    @Override
    public User getUserByEmail(String email) {
        // 适配：补充 realName、auditStatus 字段，移除 password 敏感字段
        String sql = "SELECT " +
                "id, open_id AS openId, username AS userName, " +
                "avatar, phone, email, status, nickname AS nickName, " +
                "role, real_name AS realName, audit_status AS auditStatus, " +  // 新增核心字段
                "create_time AS createTime, update_time AS updateTime " +
                "FROM `user_base` WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
        } catch (Exception e) {
            System.err.println("根据邮箱查询失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User getUserByOpenId(String openid) {
        // 适配：补充 realName、auditStatus 字段，移除 password 敏感字段
        String sql = "SELECT " +
                "id, " +
                "open_id AS openId, " +
                "username AS userName, " +
                "avatar, " +
                "phone, " +
                "email, " +
                "status, " +
                "nickname AS nickName, " +
                "role, real_name AS realName, audit_status AS auditStatus, " +  // 新增核心字段
                "create_time AS createTime, " +
                "update_time AS updateTime " +
                "FROM `user_base` " +
                "WHERE open_id = ?";

        try {
            System.out.println("执行openid查询SQL: " + sql);
            System.out.println("查询openid: " + openid);

            User user = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    openid
            );

            if (user != null) {
                System.out.println("查询成功 - 用户ID: " + user.getId() + ", openid: " + user.getOpenId() + ", 审核状态: " + user.getAuditStatus());
            } else {
                System.out.println("查询结果为空 - openid: " + openid);
            }
            return user;

        } catch (EmptyResultDataAccessException e) {
            System.out.println("数据库中不存在openid为 " + openid + " 的用户");
            return null;
        } catch (Exception e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> searchUsers(Integer pageNum, Integer pageSize, String keyword) {
        // 适配：补充 realName、auditStatus 字段，修正表名（order→mall_order，note→community_note）
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;

        StringBuilder sql = new StringBuilder("SELECT " +
                "id, open_id AS openId, username AS userName, " +
                "avatar, phone, email, status, nickname AS nickName, " +
                "role, real_name AS realName, audit_status AS auditStatus, " +  // 新增字段
                "create_time AS createTime, update_time AS updateTime, " +
                "(SELECT COUNT(*) FROM mall_order WHERE user_id = u.id) AS orderCount, " +
                "(SELECT COUNT(*) FROM community_note WHERE author_id = u.id) AS noteCount " +
                "FROM `user_base` u WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // 新增：支持按真实姓名搜索（医生审核场景常用）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeKeyword = "%" + keyword.trim() + "%";
            sql.append(" AND (username LIKE ? OR nickname LIKE ? OR phone LIKE ? OR email LIKE ? OR real_name LIKE ?)");
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);  // 新增：real_name 字段匹配
        }

        sql.append(" LIMIT ?, ?");
        params.add(start);
        params.add(pageSize);

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(User.class), params.toArray());
    }

    // ====================== 原有方法：无需修改（字段未涉及） ======================
    @Override
    public Integer updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE `user_base` SET password = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newPassword, userId);
    }

    @Override
    public Integer deleteUser(Long userId) {
        String sql = "DELETE FROM `user_base` WHERE id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    @Override
    public List<User> getUserByPage(Integer pageNum, Integer pageSize, String username, Integer status) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;

        // 适配：补充 realName、auditStatus 字段，修正表名
        StringBuilder sql = new StringBuilder("SELECT " +
                "id, open_id AS openId, username AS userName, " +
                "avatar, phone, email, status, nickname AS nickName, " +
                "role, real_name AS realName, audit_status AS auditStatus, " +  // 新增字段
                "create_time AS createTime, update_time AS updateTime, " +
                "(SELECT COUNT(*) FROM mall_order WHERE user_id = u.id) AS orderCount, " +
                "(SELECT COUNT(*) FROM community_note WHERE author_id = u.id) AS noteCount " +
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
        String sql = "SELECT " +
                "u.*, " +
                "COUNT(o.id) AS order_count, " +
                "SUM(CASE WHEN o.order_status = 0 THEN 1 ELSE 0 END) AS pending_pay_count, " +
                "SUM(CASE WHEN o.order_status = 1 THEN 1 ELSE 0 END) AS pending_ship_count " +
                "FROM user_base u " +
                "LEFT JOIN mall_order o ON u.id = o.user_id " +  // 修正表名：order→mall_order
                "WHERE u.id = ? " +
                "GROUP BY u.id";
        return jdbcTemplate.queryForMap(sql, userId);
    }

    @Override
    public Integer countUserNotes(Long userId) {
        String sql = "SELECT COUNT(id) FROM community_note WHERE user_id = ? AND status = 1";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public Integer countUnreadNotices(Long userId) {
        String sql = "SELECT COUNT(id) FROM notice WHERE user_id = ? AND is_read = 0";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public Integer countSearchUsers(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `user_base` WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        // 新增：支持按真实姓名统计
        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeKeyword = "%" + keyword.trim() + "%";
            sql.append(" AND (username LIKE :keyword OR nickname LIKE :keyword OR phone LIKE :keyword OR email LIKE :keyword OR real_name LIKE :keyword)");
            params.addValue("keyword", likeKeyword);
        }

        return namedParameterJdbcTemplate.queryForObject(
                sql.toString(),
                params,
                Integer.class
        );
    }

    // ====================== 新增方法1：根据角色查询用户ID列表（保持不变，已适配） ======================
    @Override
    public List<Long> getUserIdListByRole(int role) {
        String sql = "SELECT id " +
                "FROM `user_base` " +
                "WHERE role = ? " +
                "AND status = 1 " +
                "ORDER BY create_time DESC";

        try {
            System.out.println("执行角色查询用户ID列表SQL: " + sql);
            System.out.println("查询角色值: " + role);

            List<Long> userIdList = jdbcTemplate.queryForList(
                    sql,
                    Long.class,
                    role
            );

            System.out.println("角色" + role + "的用户ID列表查询完成，共" + (userIdList == null ? 0 : userIdList.size()) + "条记录");
            return userIdList == null ? new ArrayList<>() : userIdList;

        } catch (Exception e) {
            System.err.println("根据角色查询用户ID列表失败，角色值: " + role);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ====================== 新增方法2：分页查询待审核医生列表（适配新增字段） ======================
    @Override
    public List<User> getPendingDoctorListByPage(List<Long> pendingUserIds, int offset, int pageSize) {
        if (pendingUserIds == null || pendingUserIds.isEmpty()) {
            System.out.println("待审核用户ID列表为空，无需查询分页数据");
            return new ArrayList<>();
        }

        // 适配：补充 realName、gender、idCardFrontUrl 等医生审核核心字段
        String sql = "SELECT " +
                "id, " +
                "username AS userName, " +
                "real_name AS realName, " +  // 新增：医生真实姓名
                "gender, " +  // 新增：性别
                "age, " +  // 新增：年龄
                "phone, " +  // 手机号（管理员联系用）
                "avatar, " +  // 头像
                "id_card_front_url AS idCardFrontUrl, " +  // 身份证正面（审核用）
                "id_card_back_url AS idCardBackUrl, " +  // 身份证背面（审核用）
                "audit_status AS auditStatus, " +  // 审核状态（确认待审核）
                "create_time AS createTime " +  // 注册时间（排序用）
                "FROM `user_base` " +
                "WHERE id IN (" + getPlaceholders(pendingUserIds.size()) + ") " +
                "AND status = 1 " +  // 仅查询启用状态的用户
                "ORDER BY create_time DESC " +
                "LIMIT ?, ?";

        List<Object> params = new ArrayList<>(pendingUserIds);
        params.add(offset);
        params.add(pageSize);

        try {
            System.out.println("执行待审核医生分页查询SQL: " + sql);
            System.out.println("待审核用户ID列表: " + pendingUserIds);
            System.out.println("分页参数：offset=" + offset + ", pageSize=" + pageSize);

            List<User> pendingDoctorList = jdbcTemplate.query(
                    sql,
                    params.toArray(),
                    new BeanPropertyRowMapper<>(User.class)
            );

            System.out.println("待审核医生分页查询完成，共" + (pendingDoctorList == null ? 0 : pendingDoctorList.size()) + "条记录");
            return pendingDoctorList == null ? new ArrayList<>() : pendingDoctorList;

        } catch (Exception e) {
            System.err.println("待审核医生分页查询失败");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ====================== 新增方法3：查询医生审核详情（UserDao接口补充的方法） ======================
    @Override
    public User getDoctorAuditDetail(Long auditId) {
        // 核心：查询医生审核所需的全部非敏感信息（与前端auditDetail页面字段对齐）
        String sql = "SELECT " +
                "id, " +
                "real_name AS realName, " +  // 医生姓名
                "gender, " +  // 性别
                "age, " +  // 年龄
                "phone, " +  // 联系电话
                "id_card_front_url AS idCardFrontUrl, " +  // 身份证正面
                "id_card_back_url AS idCardBackUrl, " +  // 身份证背面
                "audit_status AS auditStatus, " +  // 审核状态
                "audit_remark AS auditRemark " +  // 审核备注（拒绝原因）
                "FROM `user_base` " +
                "WHERE id = ? " +
                "AND role = 2 " +  // 仅查询医生角色
                "AND status = 1";  // 仅查询启用状态

        try {
            System.out.println("执行医生审核详情查询SQL: " + sql);
            System.out.println("审核ID（用户ID）: " + auditId);

            User doctorDetail = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    auditId
            );

            if (doctorDetail != null) {
                System.out.println("审核详情查询成功 - 医生姓名: " + doctorDetail.getRealName() + ", 审核状态: " + doctorDetail.getAuditStatus());
            } else {
                System.out.println("无此审核ID对应的医生信息 - 审核ID: " + auditId);
            }
            return doctorDetail;

        } catch (EmptyResultDataAccessException e) {
            System.out.println("数据库中不存在审核ID为 " + auditId + " 的医生");
            return null;
        } catch (Exception e) {
            System.err.println("查询医生审核详情失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ====================== 新增方法4：更新医生审核状态（UserDao接口补充的方法） ======================
    @Override
    public Integer updateDoctorAuditStatus(Long userId, Integer auditStatus) {
        // 核心：仅更新审核状态和备注，避免修改其他字段
        String sql = "UPDATE `user_base` " +
                "SET audit_status = ?, update_time = NOW() " +
                "WHERE id = ? " +
                "AND role = 2 " +  // 仅允许更新医生角色用户
                "AND status = 1";  // 仅更新启用状态的用户

        try {
            System.out.println("执行医生审核状态更新SQL: " + sql);
            System.out.println("用户ID: " + userId + ", 目标审核状态: " + auditStatus);

            int affectedRows = jdbcTemplate.update(sql, auditStatus, userId);
            System.out.println("审核状态更新完成，受影响行数: " + affectedRows);
            return affectedRows;

        } catch (Exception e) {
            System.err.println("更新医生审核状态失败，用户ID: " + userId);
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法5：统计待审核医生总数（UserDao接口补充的方法） ======================
    @Override
    public Integer getPendingDoctorTotalCount(List<Long> pendingUserIds) {
        if (pendingUserIds == null || pendingUserIds.isEmpty()) {
            System.out.println("待审核用户ID列表为空，总数为0");
            return 0;
        }

        String sql = "SELECT COUNT(*) " +
                "FROM `user_base` " +
                "WHERE id IN (" + getPlaceholders(pendingUserIds.size()) + ") " +
                "AND role = 2 " +  // 仅统计医生角色
                "AND status = 1";  // 仅统计启用状态

        List<Object> params = new ArrayList<>(pendingUserIds);

        try {
            System.out.println("执行待审核医生总数统计SQL: " + sql);
            System.out.println("待审核用户ID列表: " + pendingUserIds);

            Integer totalCount = jdbcTemplate.queryForObject(
                    sql,
                    params.toArray(),
                    Integer.class
            );

            System.out.println("待审核医生总数统计完成，共" + (totalCount == null ? 0 : totalCount) + "条");
            return totalCount == null ? 0 : totalCount;

        } catch (Exception e) {
            System.err.println("统计待审核医生总数失败");
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 工具方法：生成IN语句的占位符（保持不变） ======================
    private String getPlaceholders(int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            placeholders.append("?");
            if (i != count - 1) {
                placeholders.append(", ");
            }
        }
        return placeholders.toString();
    }
}