/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 21:08:50
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/User.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;
import java.util.Date;

@Data
@ToString
public class User {
    /**
     * 用户ID（对应user_base.id，自增主键）
     */
    private Long id;

    /**
     * 微信开放ID(对应open_id)
     */
    private String openId;

    /**
     * 登录用户名username
     */
    private String userName;

    /**
     * 昵称nickname
     */
    private String nickName;

    /**
     * 登录密码（序列化时排除）password
     */
    @JSONField(serialize = false)
    private String passWord;


    /**
     * 头像URL avatar
     */
    private String avatar;


    /**
     * 手机号 phone
     */
    private String phone;

    /**
     * 邮箱 email
     */
    private String email;

    /**
     * 状态（0-禁用，1-正常） status
     */
    private Integer status;

    /**
     * 注册时间 create_time
     */
    private Date createTime;

    /**
     * 信息更新时间 update_time
     */
    private Date updateTime;

    // 以下为关联扩展字段（非数据库字段，用于查询关联数据）
    /**
     * 关联的订单数量（用于个人中心展示）
     */
    private Integer orderCount;

    /**
     * 发布的笔记数量（社区相关）
     */
    private Integer noteCount;
}