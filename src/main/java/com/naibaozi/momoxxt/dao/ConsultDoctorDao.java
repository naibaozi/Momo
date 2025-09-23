/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:17:51
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:17:51
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/ConsultDoctorDao.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.ConsultDoctor;

/**
 * 医生DAO接口
 */
public interface ConsultDoctorDao {
    /**
     * 根据用户ID查询医生信息（医生账号关联user_base）
     * @param userId 用户ID
     * @return 医生实体
     */
    ConsultDoctor getDoctorByUserId(Integer userId);

    /**
     * 根据医生ID查询
     * @param id 医生ID
     * @return 医生实体
     */
    ConsultDoctor getDoctorById(Integer id);
}