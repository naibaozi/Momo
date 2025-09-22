/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:48:50
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:33
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/DictPetType.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-22 08:48:50
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-22 11:57:33
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/DictPetType.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.entity;

import lombok.Data;
import lombok.ToString;
import java.util.Date;
import java.util.List;

/**
 * 宠物品类数据字典实体类（对应dict_pet_type表）
 */
@Data
@ToString
public class DictPetType {

    /**
     * 字典ID（主键）
     */
    private Integer id;

    /**
     * 品类名称（如“爬宠-鬃狮蜥”“啮齿-金丝熊”）
     */
    private String typeName;

    /**
     * 父级ID（0=一级品类，>0=二级品类）
     */
    private Integer parentId;

    /**
     * 排序（热门品类靠前）
     */
    private Integer sort;

    private List<DictPetType> children;
}
