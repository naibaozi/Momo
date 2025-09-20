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
     * 微信开放ID
     */
    private String openId;

    /**
     * 登录用户名
     */
    private String userName;

    /**
     * 登录密码（序列化时排除）
     */
    @JSONField(serialize = false)
    private String passWord;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色（1-普通用户，2-管理员等，可关联sys_dict）
     */
    private Integer role;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态（0-禁用，1-正常，对应sys_dict的user_status类型）
     */
    private Integer status;

    /**
     * 注册时间
     */
    private Date createdAt;

    /**
     * 信息更新时间
     */
    private Date updatedAt;

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