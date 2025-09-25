package com.naibaozi.momoxxt.dao;

import com.naibaozi.momoxxt.entity.ConsultDoctor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 医生DAO接口（适配 consult_doctor 表新增字段与业务扩展）
 */
public interface ConsultDoctorDao {

    // ====================== 原有方法（保持不变） ======================

    /**
     * 根据用户ID查询医生信息（医生账号关联user_base）
     * @param userId 用户ID
     * @return 医生实体
     */
    ConsultDoctor getDoctorByUserId(Integer userId);

    /**
     * 根据医生ID查询
     * @param id 医生ID
     * @return 医生实体
     */
    ConsultDoctor getDoctorById(Integer id);

    /**
     * 获取consult_doctor表中已关联的用户ID列表（已审核通过的医生）
     * @return 已关联的用户ID列表
     */
    List<Long> getRelatedUserIdList();

    // ====================== 新增方法（支撑新增字段与业务） ======================

    /**
     * 插入新医生信息（审核通过时调用）
     * 适配：补充 auditor_id, audit_time, doctor_intro 等字段
     * @param doctor 医生实体，需包含 userId, realName, qualification 等必要信息
     * @return 插入成功返回1，失败返回0
     */
    int insertDoctor(ConsultDoctor doctor);

    /**
     * 更新医生信息（支持动态更新）
     * 适配：支持更新 doctor_intro, consultation_fee, receive_status 等字段
     * @param doctor 包含待更新字段和主键id的医生实体
     * @return 更新成功返回影响的行数
     */
    int updateDoctor(ConsultDoctor doctor);

    /**
     * 更新医生接诊状态
     * @param doctorId 医生ID
     * @param receiveStatus 接诊状态（1=可接诊，0=休息中）
     * @return 更新成功返回1，失败返回0
     */
    int updateReceiveStatus(Integer doctorId, Integer receiveStatus);

    /**
     * 接诊次数+1（原子操作，避免并发问题）
     * 适配：`consult_count` 字段
     * @param doctorId 医生ID
     * @return 更新成功返回1，失败返回0
     */
    int incrementConsultCount(Integer doctorId);

    /**
     * 更新医生平均评分
     * 适配：`avg_score` 字段
     * @param doctorId 医生ID
     * @param newAvgScore 新的平均评分
     * @return 更新成功返回1，失败返回0
     */
    int updateAvgScore(Integer doctorId, BigDecimal newAvgScore);

    /**
     * 根据擅长品类查询医生列表
     * @param typeId 宠物品类ID (dict_pet_type.id)
     * @return 医生实体列表
     */
    List<ConsultDoctor> getDoctorsByType(Integer typeId);

    /**
     * 分页查询医生列表（可按评分、接诊量排序）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param sortBy 排序字段 (例如: "avg_score DESC", "consult_count DESC")
     * @return 医生实体列表
     */
    List<ConsultDoctor> getDoctorListByPage(Integer pageNum, Integer pageSize, String sortBy);

    /**
     * 查询所有医生的总数
     * @return 医生总数
     */
    int countAllDoctors();

    /**
     * 根据医生ID获取医生详情（包含用户基础信息）
     * 用于医生个人主页，整合 user_base 和 consult_doctor 表信息
     * @param doctorId 医生ID
     * @return 包含医生和用户信息的Map
     */
    Map<String, Object> getDoctorDetailWithUserInfo(Integer doctorId);
}