/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 08:19:27
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:55:20
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/PayConstant.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 08:19:27
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:55:20
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/PayConstant.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.entity;


/**
 * 支付相关常量
 */
public class PayConstant {
    // 支付状态（与consult_order表pay_status字段对齐）
    public static final Integer PAY_STATUS_UNPAID = 0; // 未支付
    public static final Integer PAY_STATUS_PAID = 1;   // 已支付

    // 支付类型（预留，后续对接真实支付用）
    public static final Integer PAY_TYPE_WECHAT = 1; // 微信支付
    public static final Integer PAY_TYPE_ALIPAY = 2;  // 支付宝

    // 假支付测试标记
    public static final boolean TEST_PAY_MODE = true; // true=假支付模式，false=真实支付模式
}