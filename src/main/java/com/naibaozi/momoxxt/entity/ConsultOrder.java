/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 08:24:44
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:55:21
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/ConsultOrder.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 问诊订单表（对应consult_order）
 */
@Data
public class ConsultOrder {
    /** 订单ID（主键） */
    private Integer id;
    /** 订单编号（唯一，如CON20241001001） */
    private String orderNo;
    /** 用户ID（关联user_base.id） */
    private Integer userId;
    /** 问诊宠物ID（关联user_pet.id） */
    private Integer petId;
    /** 接诊医生ID（关联consult_doctor.id） */
    private Integer doctorId;
    /** 问诊类型（1=普通，2=紧急） */
    private Integer consultType;
    /** 症状描述（用户填写） */
    private String symptom;
    /** 症状媒体URL（照片/视频逗号分隔） */
    private String mediaUrl;
    /** 应付金额（问诊费-优惠券） */
    private BigDecimal amount;
    /** 优惠券ID（0=未使用） */
    private Integer couponId;
    /** 支付状态（0=未支付，1=已支付） */
    private Integer payStatus;
    /** 支付时间 */
    private Date payTime;
    /** 问诊状态（0=待接诊，1=接诊中，2=已完成，3=已取消） */
    private Integer consultStatus;
    /** 接诊时间 */
    private Date acceptTime;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}