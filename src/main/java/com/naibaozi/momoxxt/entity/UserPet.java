/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:11:24
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:33
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/UserPet.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:11:24
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:33
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/UserPet.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.entity;

import lombok.Data;
import lombok.ToString;
import java.util.Date;

@Data
@ToString
public class UserPet {
    /**
     * 宠物ID（主键）
     */
    private Long id;

    /**
     * 所属用户ID（关联user_base.id）
     */
    private Long userId;

    /**
     * 宠物昵称
     */
    private String petName;

    /**
     * 宠物品类ID（关联dict_pet_type.id）
     */
    private Integer petTypeId;

    /**
     * 宠物年龄（如“8个月”）
     */
    private String petAge;

    /**
     * 宠物性别（0=未知，1=公，2=母）
     */
    private Integer petGender;

    /**
     * 过往病史
     */
    private String medicalHistory;

    /**
     * 是否默认宠物（1=是，0=否）
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}