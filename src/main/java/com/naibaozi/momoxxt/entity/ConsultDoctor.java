package com.naibaozi.momoxxt.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 问诊医生信息表（对应consult_doctor）
 */
@Data
public class ConsultDoctor {
    /** 医生ID（主键） */
    private Integer id;
    /** 关联user_base.id（医生的用户账号） */
    private Integer userId;
    /** 真实姓名（认证用） */
    private String realName;
    /** 职称（如“异宠执业医师”） */
    private String title;
    /** 擅长品类（dict_pet_type.id逗号分隔） */
    private String goodAtType;
    /** 资质证书URL（审核用） */
    private String qualification;
    /** 单次问诊费（如59.90） */
    private BigDecimal consultationFee;
    /** 接诊状态（1=可接诊，0=休息中） */
    private Integer receiveStatus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

    // ------------------------------ 新增字段（根据数据库补充）------------------------------

    /**
     * 审核管理员ID（关联 user_base.id）
     * 用于记录是哪个管理员审核通过了该医生，便于责任追溯和管理。
     */
    private Integer auditorId;

    /**
     * 审核通过时间
     * 记录医生正式入驻平台的时间，可用于“最新入驻医生”等排序和展示。
     */
    private Date auditTime;

    /**
     * 医生简介
     * 用于在医生列表和详情页展示，向用户介绍医生的专业背景、经验等，提升用户信任度。
     */
    private String doctorIntro;

    /**
     * 累计接诊次数
     * 统计医生的工作量和活跃度，是衡量医生受欢迎程度的重要指标。
     */
    private Integer consultCount;

    /**
     * 平均评分（例如：4.95）
     * 基于用户接诊后的评价计算得出，用于医生排序和优质医生推荐，提升平台服务质量。
     */
    private BigDecimal avgScore;
}