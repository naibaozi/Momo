/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:23:06
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:23:06
 * @FilePath: src/main/java/com/naibaozi/momoxxt/service/ConsultChatService.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.service;

import com.naibaozi.momoxxt.entity.ConsultChat;
import java.util.List;

/**
 * 问诊聊天Service接口
 */
public interface ConsultChatService {
    /**
     * 发送聊天消息
     * @param orderId 订单ID
     * @param senderId 发送者ID
     * @param senderType 发送者角色（1=用户，2=医生）
     * @param contentType 内容类型（1=文字，2=图片，3=语音）
     * @param content 内容（文字/媒体URL）
     * @return 保存后的消息实体
     */
    ConsultChat sendMessage(Integer orderId, Integer senderId, Integer senderType, Integer contentType, String content);

    /**
     * 查询订单的聊天记录
     * @param orderId 订单ID
     * @param userId 操作人ID（校验权限）
     * @return 聊天记录列表
     */
    List<ConsultChat> getChatRecords(Integer orderId, Integer userId);
}