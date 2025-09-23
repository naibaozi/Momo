/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 08:25:09
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:55:21
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/ConsultDoctor.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 问诊医生信息表（对应consult_doctor）
 */
@Data
public class ConsultDoctor {
    /** 医生ID（主键） */
    private Integer id;
    /** 关联user_base.id（医生的用户账号） */
    private Integer userId;
    /** 真实姓名（认证用） */
    private String realName;
    /** 职称（如“异宠执业医师”） */
    private String title;
    /** 擅长品类（dict_pet_type.id逗号分隔） */
    private String goodAtType;
    /** 资质证书URL（审核用） */
    private String qualification;
    /** 单次问诊费（如59.90） */
    private BigDecimal consultationFee;
    /** 接诊状态（1=可接诊，0=休息中） */
    private Integer receiveStatus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}