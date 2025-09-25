/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 10:35:34
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/UserDao.java
 * @Description: 用户数据访问层（UserDAO）接口
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.ConsultOrder;
import com.naibaozi.momoxxt.entity.User;
import java.util.List;
import java.util.Map;

public interface UserDao {

    /**
     * 查询所有用户信息（包含订单数、笔记数等扩展字段）
     * @return 用户列表
     */
    List<User> getUserinfoList();

    /**
     * 根据ID查询用户（返回包含 realName、role 等字段的 User 对象）
     * @param id 用户ID
     * @return 单个用户对象，查询不到则返回null
     */
     User getUserById(Long id);

    /**
     * 根据用户名查询用户（适配 userName → 数据库 username 字段）
     * @param username 用户名（对应数据库 username 字段）
     * @return 单个用户对象，查询不到则返回null
     */
    User getUserByUsername(String username);

    /**
     * 新增用户（包含openId、avatar等字段）
     * @param user 用户对象（需设置userName、passWord等）
     * @return 新增成功返回自增ID，失败返回0
     */
    Integer addUser(User user);

    /**
     * 更新用户信息（包含openId、phone等，不包含密码）
     * @param user 用户对象
     * @return 受影响的行数
     */
    Integer updateUser(User user);
    /**
     * 更新用户密码（适配 User 实体类的 passWord 字段）
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 受影响的行数
     */
    Integer updatePassword(Long id, String newPassword);

    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 受影响的行数
     */
    Integer deleteUser(Long id);

    /**
     * 分页查询用户（带条件，包含新字段）
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页条数
     * @param username 用户名（可选，模糊查询）
     * @param status 状态（可选）
     * @return 分页用户列表（包含 realName、role 等）
     */
    List<User> getUserByPage(Integer pageNum, Integer pageSize, String username, Integer status);

    /**
     * 统计用户总数（带条件）
     * @param username 用户名（可选）
     * @param status 状态（可选）
     * @return 用户总数量
     */
    Integer countUser(String username, Integer status);

    /**
     * 查询用户及关联的订单统计
     * @param userId 用户ID
     * @return 包含用户信息和订单数量的Map
     */
    Map<String, Object> getUserWithOrderStats(Long userId);

    /**
     * 查询用户发布的笔记数量
     * @param userId 用户ID
     * @return 笔记数量
     */
    Integer countUserNotes(Long userId);

    /**
     * 查询用户的未读消息数量
     * @param userId 用户ID
     * @return 未读消息数
     */
    Integer countUnreadNotices(Long userId);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱地址
     * @return 用户对象，不存在则返回null
     */
    User getUserByEmail(String email);


    User getUserByOpenId(String openid);



    List<User> searchUsers(Integer pageNum, Integer pageSize, String keyword);
    
    
    
    Integer countSearchUsers(String keyword);

    /**
     * 根据角色查询用户ID列表（用于获取所有医生角色用户）
     * @param role 角色值（如2=医生）
     * @return 用户ID列表
     */
    List<Long> getUserIdListByRole(int role);

    /**
     * 分页查询待审核医生列表
     * @param pendingUserIds 待审核用户ID列表
     * @param offset 分页偏移量
     * @param pageSize 每页条数
     * @return 待审核医生列表（User实体，不含敏感字段）
     */
    List<User> getPendingDoctorListByPage(List<Long> pendingUserIds, int offset, int pageSize);



    // ------------------------------ 补充医生审核相关方法 ------------------------------
    /**
     * 根据审核ID（即用户ID）查询医生审核详情
     * 用于前端审核详情页，返回医生基础信息+身份证照片（不含敏感字段如密码）
     * @param auditId 审核ID（对应医生的user_id）
     * @return 医生审核详情（User实体，含realName、gender、idCardFrontUrl等）
     */
    User getDoctorAuditDetail(Long auditId);

    /**
     * 更新医生的审核状态
     * 用于审核通过/拒绝操作，更新user_base表的audit_status字段
     * @param userId 医生用户ID
     * @param auditStatus 审核状态（0=待审核，1=已通过，2=已拒绝）
     * @return 受影响的行数（1=成功，0=失败）
     */
    Integer updateDoctorAuditStatus(Long userId, Integer auditStatus);

    /**
     * 统计待审核医生总数
     * 用于分页查询时计算总页数（totalCount）
     * @param pendingUserIds 待审核医生的用户ID列表
     * @return 待审核医生总数
     */
    Integer getPendingDoctorTotalCount(List<Long> pendingUserIds);

    
}