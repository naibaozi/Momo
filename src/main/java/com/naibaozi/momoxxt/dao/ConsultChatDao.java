/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:17:02
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:17:02
 * @FilePath: src/main/java/com/naibaozi/momoxxt/dao/ConsultChatDao.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.ConsultChat;
import java.util.List;

/**
 * 问诊聊天DAO接口
 */
public interface ConsultChatDao {
    /**
     * 保存聊天消息
     * @param chat 聊天实体
     * @return 新增消息ID
     */
    Integer addChat(ConsultChat chat);

    /**
     * 根据订单ID查询聊天记录
     * @param orderId 订单ID
     * @return 聊天记录列表（按时间排序）
     */
    List<ConsultChat> getChatsByOrderId(Integer orderId);
}