/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:51:25
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:32
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/DictPetTypeDao.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:51:25
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:32
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/DictPetTypeDao.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.DictPetType;
import java.util.List;

public interface DictPetTypeDao {

    /**
     * 查询所有宠物品类
     * @return 品类列表
     */
    List<DictPetType> getAllPetTypes();

    /**
     * 根据ID查询品类
     * @param id 品类ID
     * @return 品类对象
     */
    DictPetType getPetTypeById(Integer id);

    /**
     * 根据父ID查询子品类
     * @param parentId 父级ID
     * @return 子品类列表
     */
    List<DictPetType> getPetTypesByParentId(Integer parentId);

    /**
     * 新增品类
     * @param dictPetType 品类对象
     * @return 新增成功数量
     */
    Integer addPetType(DictPetType dictPetType);

    /**
     * 更新品类
     * @param dictPetType 品类对象
     * @return 更新成功数量
     */
    Integer updatePetType(DictPetType dictPetType);

    /**
     * 删除品类
     * @param id 品类ID
     * @return 删除成功数量
     */
    Integer deletePetType(Integer id);
}