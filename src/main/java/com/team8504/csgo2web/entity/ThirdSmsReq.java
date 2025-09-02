package com.team8504.csgo2web.entity;

/**
 * Author: zby
 * Package: com.team8504.csgo2web.entity
 * Project: CSGO2WEB
 * Date: 2025/09/02/上午8:42
 * Version 0.0
 */
public class ThirdSmsReq {
    private String verifyCode;   // 验证码
    private String phoneNumber;  // 手机号。格式为”+86-xxxxxxxxxxx”
    private String action;       // 验证码行为。1001：注册登录； 1002：重置密码； 1003：修改手机号（此种情况下，verifyCode为空，仅发送一个短信通知）
    private String productId;    // 项目ID
    private String taskId;       // 任务ID。无实际语义，问题定位使用
}
