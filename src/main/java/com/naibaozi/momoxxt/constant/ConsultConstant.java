/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 09:21:01
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:21:01
 * @FilePath: src/main/java/com/naibaozi/momoxxt/constant/ConsultConstant.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.constant;

/**
 * 问诊相关常量
 */
public class ConsultConstant {
    // 问诊类型
    public static final Integer CONSULT_TYPE_NORMAL = 1; // 普通
    public static final Integer CONSULT_TYPE_EMERGENCY = 2; // 紧急

    // 支付状态
    public static final Integer PAY_STATUS_UNPAID = 0; // 未支付
    public static final Integer PAY_STATUS_PAID = 1; // 已支付

    // 问诊状态
    public static final Integer CONSULT_STATUS_PENDING = 0; // 待接诊
    public static final Integer CONSULT_STATUS_IN_PROGRESS = 1; // 接诊中
    public static final Integer CONSULT_STATUS_FINISHED = 2; // 已完成
    public static final Integer CONSULT_STATUS_CANCELED = 3; // 已取消

    // 发送者角色
    public static final Integer SENDER_TYPE_USER = 1; // 用户
    public static final Integer SENDER_TYPE_DOCTOR = 2; // 医生

    // 消息类型
    public static final Integer CONTENT_TYPE_TEXT = 1; // 文字
    public static final Integer CONTENT_TYPE_IMAGE = 2; // 图片
    public static final Integer CONTENT_TYPE_VOICE = 3; // 语音
}