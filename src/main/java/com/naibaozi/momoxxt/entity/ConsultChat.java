/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 08:25:48
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:55:21
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/ConsultChat.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.entity;

import lombok.Data;
import java.util.Date;

/**
 * 问诊聊天记录表（对应consult_chat）
 */
@Data
public class ConsultChat {
    /** 聊天ID（主键） */
    private Integer id;
    /** 关联问诊订单ID（consult_order.id） */
    private Integer orderId;
    /** 发送者ID（用户/医生，关联user_base.id） */
    private Integer senderId;
    /** 发送者角色（1=用户，2=医生） */
    private Integer senderType;
    /** 内容类型（1=文字，2=图片，3=语音） */
    private Integer contentType;
    /** 内容（文字/媒体URL） */
    private String content;
    /** 发送时间 */
    private Date sendTime;
}