/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:19:36
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:34
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/UserPetDao.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:19:36
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:34
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/UserPetDao.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.UserPet;
import java.util.List;

public interface UserPetDao {

    /**
     * 新增宠物信息
     * @param userPet 宠物实体
     * @return 新增成功返回自增ID，失败返回0
     */
    Integer addUserPet(UserPet userPet);

    /**
     * 根据ID删除宠物
     * @param id 宠物ID
     * @return 受影响的行数
     */
    Integer deleteUserPet(Long id);

    /**
     * 更新宠物信息
     * @param userPet 宠物实体（需包含ID）
     * @return 受影响的行数
     */
    Integer updateUserPet(UserPet userPet);

    /**
     * 根据ID查询宠物
     * @param id 宠物ID
     * @return 宠物实体，查询不到返回null
     */
    UserPet getUserPetById(Long id);

    /**
     * 根据用户ID查询宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    List<UserPet> getUserPetsByUserId(Long userId);

    /**
     * 设置用户默认宠物（将其他宠物设为非默认）
     * @param userId 用户ID
     * @param petId 要设为默认的宠物ID
     * @return 受影响的行数
     */
    Integer setDefaultPet(Long userId, Long petId);
}