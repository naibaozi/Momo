/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-19 15:57:18
 * @FilePath: src/main/java/com/naibaozi/momoxxt/entity/User.java
 * @Description: User实体类
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.entity;

import lombok.Data;
import lombok.ToString;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@ToString
public class User {
    /**
     * 用户 ID（对应数据库 id 字段，自增主键）
     */
    private Long id; // 统一使用 Long 类型，避免大数值溢出

    /**
     * 微信开放 ID（对应数据库 openid 字段）
     */
    private String openId;

    /**
     * 登录用户名（对应数据库 username 字段）
     */
    private String userName;

    /**
     * 登录密码（对应数据库 password 字段）
     */
    private String passWord;

    /**
     * 真实姓名（对应数据库 real_name 字段）
     */
    private String realName;

    /**
     * 头像 URL（对应数据库 avatar 字段）
     */
    private String avatar;

    /**
     * 角色（1 - 学生，2 - 教师，对应数据库 role 字段）
     */
    private Integer role;

    /**
     * 手机号（对应数据库 phone 字段）
     */
    private String phone;

    /**
     * 邮箱（对应数据库 email 字段）
     */
    private String email;

    /**
     * 状态（0 - 禁用，1 - 正常，对应数据库 status 字段）
     */
    private Integer status;

    /**
     * 创建时间（对应数据库 created_at 字段）
     */
    private Date createdAt;

    /**
     * 更新时间（对应数据库 updated_at 字段）
     */
    private Date updatedAt;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}