/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:15:36
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:15:36
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/ConsultOrderDao.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.ConsultOrder;
import java.util.List;

/**
 * 问诊订单DAO接口
 */
public interface ConsultOrderDao {
    /**
     * 创建问诊订单
     * @param order 订单实体
     * @return 新增订单ID
     */
    Integer addOrder(ConsultOrder order);

    /**
     * 根据订单编号查询
     * @param orderNo 订单编号
     * @return 订单实体
     */
    ConsultOrder getOrderByNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @param consultStatus 问诊状态（null=全部）
     * @return 订单列表
     */
    List<ConsultOrder> getOrdersByUserId(Integer userId, Integer consultStatus);

    /**
     * 查询待接诊订单（已支付、未取消）
     * @return 待接诊订单列表
     */
    List<ConsultOrder> getPendingAcceptOrders();

    /**
     * 更新订单状态（支付/接诊/完成）
     * @param order 订单实体（含更新字段）
     * @return 影响行数
     */
    Integer updateOrderStatus(ConsultOrder order);

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单实体
     */
    ConsultOrder getOrderById(Integer id);
}