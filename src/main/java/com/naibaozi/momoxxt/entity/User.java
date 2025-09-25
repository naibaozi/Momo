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
     * 微信开放ID(对应user_base.open_id)
     */
    private String openId;

    /**
     * 登录用户名（对应user_base.username）
     */
    private String userName;

    /**
     * 昵称（对应user_base.nickname，社区展示用）
     */
    private String nickName;

    /**
     * 登录密码（对应user_base.password，序列化时排除，避免泄露）
     */
    @JSONField(serialize = false)
    private String passWord;

    /**
     * 头像URL（对应user_base.avatar）
     */
    private String avatar;

    /**
     * 手机号（对应user_base.phone，登录/联系用）
     */
    private String phone;

    /**
     * 邮箱（对应user_base.email，找回密码用）
     */
    private String email;

    /**
     * 账号状态（对应user_base.status：0-禁用，1-正常）
     */
    private Integer status;

    /**
     * 角色（对应user_base.role：0-普通用户，1-管理员，2-医生，3-商户，4-待审核）
     * 注意：当前业务逻辑中，医生角色（2）的审核状态由 auditStatus 控制，role 仅表示最终角色
     */
    private Integer role;

    /**
     * 真实姓名（对应user_base.real_name，医生资质审核核心字段，与 consult_doctor.real_name 关联）
     */
    private String realName;

    /**
     * 性别（对应user_base.gender：0-未知，1-男，2-女，前端审核详情页展示）
     */
    private Integer gender;

    /**
     * 年龄（对应user_base.age，字符串类型支持灵活表述，如“35岁”“5年经验”）
     */
    private String age;

    /**
     * 身份证正面照URL（对应user_base.id_card_front_url，医生审核材料）
     */
    private String idCardFrontUrl;

    /**
     * 身份证背面照URL（对应user_base.id_card_back_url，医生审核材料）
     */
    private String idCardBackUrl;

    /**
     * 审核状态（对应user_base.audit_status：0-待审核，1-已通过，2-已拒绝）
     * 核心字段：区分医生审核流程阶段，与 role=2（医生角色）配合使用
     */
    private Integer auditStatus;

    /**
     * 审核备注（对应user_base.audit_remark，管理员拒绝时填写原因，前端展示给医生）
     */
    private String auditRemark;

    /**
     * 注册时间（对应user_base.create_time）
     */
    private Date createTime;

    /**
     * 信息更新时间（对应user_base.update_time）
     */
    private Date updateTime;

    // ------------------------------ 关联扩展字段（非数据库表字段）------------------------------
    /**
     * 关联的订单数量（用于个人中心、管理员统计，非表字段）
     */
    private Integer orderCount;

    /**
     * 发布的笔记数量（用于社区个人主页展示，非表字段）
     */
    private Integer noteCount;
}