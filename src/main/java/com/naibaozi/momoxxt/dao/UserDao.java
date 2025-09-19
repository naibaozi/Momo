/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-19 17:18:43
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/UserDao.java
 * @Description: 用户数据访问层（UserDAO）接口
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.User;
import java.util.List;

public interface UserDao {

    /**
     * 查询所有用户信息（包含 realName、role、phone 等新字段）
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
     * 新增用户（包含 realName、role、phone 等新字段）
     * @param user 用户对象（需设置 userName、passWord、realName 等）
     * @return 新增成功返回自增ID，失败返回0
     */
    Integer addUser(User user);

    /**
     * 更新用户信息（包含 realName、role、phone 等，不包含密码）
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
    Integer updatePassword(Integer id, String newPassword);

    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 受影响的行数
     */
    Integer deleteUser(Integer id);

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
    
    
    
    
}