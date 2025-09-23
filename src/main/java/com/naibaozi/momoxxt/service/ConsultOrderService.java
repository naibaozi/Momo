/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:21:29
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:21:29
 * @FilePath: src/main/java/com/naibaozi/momoxxt/service/ConsultOrderService.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.service;

import com.naibaozi.momoxxt.entity.ConsultOrder;
import java.util.List;

/**
 * 问诊订单Service接口
 */
public interface ConsultOrderService {
    /**
     * 创建问诊订单
     * @param userId 用户ID
     * @param petId 宠物ID
     * @param consultType 问诊类型（1=普通，2=紧急）
     * @param symptom 症状描述
     * @param mediaUrls 媒体URL列表（可空）
     * @param couponId 优惠券ID（0=未使用）
     * @return 订单实体（含订单编号、金额）
     */
    ConsultOrder createOrder(Integer userId, Integer petId, Integer consultType, String symptom, List<String> mediaUrls, Integer couponId);

    /**
     * 模拟支付（测试用）
     * @param orderNo 订单编号
     * @param userId 用户ID
     * @return 支付结果（true=成功）
     */
    Boolean mockPay(String orderNo, Integer userId);

    /**
     * 医生接诊/拒单
     * @param doctorId 医生ID
     * @param orderId 订单ID
     * @param accept 是否接诊（true=接诊，false=拒单）
     * @return 操作结果
     */
    Boolean handleAccept(Integer doctorId, Integer orderId, Boolean accept);

    /**
     * 结束问诊
     * @param doctorId 医生ID
     * @param orderId 订单ID
     * @return 操作结果
     */
    Boolean finishConsult(Integer doctorId, Integer orderId);

    /**
     * 查询用户的问诊订单
     * @param userId 用户ID
     * @param consultStatus 问诊状态（null=全部）
     * @return 订单列表
     */
    List<ConsultOrder> getUserOrders(Integer userId, Integer consultStatus);

    /**
     * 查询待接诊订单（医生端）
     * @return 待接诊订单列表
     */
    List<ConsultOrder> getPendingOrders();
}