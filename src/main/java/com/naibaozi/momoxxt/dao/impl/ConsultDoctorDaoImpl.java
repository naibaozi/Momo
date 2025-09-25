package com.naibaozi.momoxxt.dao.impl;

import com.naibaozi.momoxxt.dao.ConsultDoctorDao;
import com.naibaozi.momoxxt.entity.ConsultDoctor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ConsultDoctorDaoImpl implements ConsultDoctorDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // ====================== 原有方法（保持不变，优化日志与空处理） ======================
    @Override
    public ConsultDoctor getDoctorByUserId(Integer userId) {
        String sql = "SELECT " +
                "id, user_id AS userId, real_name AS realName, title, " +
                "good_at_type AS goodAtType, qualification, " +
                "consultation_fee AS consultationFee, receive_status AS receiveStatus, " +
                "auditor_id AS auditorId, audit_time AS auditTime, " +  // 新增字段
                "doctor_intro AS doctorIntro, consult_count AS consultCount, " +  // 新增字段
                "avg_score AS avgScore, create_time AS createTime, update_time AS updateTime " +  // 新增字段
                "FROM consult_doctor WHERE user_id = ?";

        try {
            System.out.println("执行医生查询（按用户ID）SQL: " + sql);
            System.out.println("查询用户ID: " + userId);

            List<ConsultDoctor> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultDoctor.class), userId);
            return list.isEmpty() ? null : list.get(0);

        } catch (Exception e) {
            System.err.println("按用户ID查询医生失败，用户ID: " + userId);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ConsultDoctor getDoctorById(Integer id) {
        String sql = "SELECT " +
                "id, user_id AS userId, real_name AS realName, title, " +
                "good_at_type AS goodAtType, qualification, " +
                "consultation_fee AS consultationFee, receive_status AS receiveStatus, " +
                "auditor_id AS auditorId, audit_time AS auditTime, " +  // 新增字段
                "doctor_intro AS doctorIntro, consult_count AS consultCount, " +  // 新增字段
                "avg_score AS avgScore, create_time AS createTime, update_time AS updateTime " +  // 新增字段
                "FROM consult_doctor WHERE id = ?";

        try {
            System.out.println("执行医生查询（按医生ID）SQL: " + sql);
            System.out.println("查询医生ID: " + id);

            List<ConsultDoctor> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ConsultDoctor.class), id);
            return list.isEmpty() ? null : list.get(0);

        } catch (Exception e) {
            System.err.println("按医生ID查询医生失败，医生ID: " + id);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Long> getRelatedUserIdList() {
        String sql = "SELECT user_id FROM consult_doctor";

        try {
            System.out.println("执行已审核医生user_id查询SQL: " + sql);

            List<Long> relatedUserIdList = jdbcTemplate.queryForList(sql, Long.class);
            System.out.println("已审核医生user_id查询完成，共" + (relatedUserIdList == null ? 0 : relatedUserIdList.size()) + "条记录");
            return relatedUserIdList == null ? new ArrayList<>() : relatedUserIdList;

        } catch (Exception e) {
            System.err.println("已审核医生user_id查询失败");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ====================== 新增方法1：插入医生信息（审核通过时调用） ======================
    @Override
    public int insertDoctor(ConsultDoctor doctor) {
        // 适配新增字段：auditorId、auditTime、doctorIntro、consultCount、avgScore
        String sql = "INSERT INTO consult_doctor (" +
                "user_id, real_name, title, good_at_type, qualification, " +
                "consultation_fee, receive_status, auditor_id, audit_time, " +  // 新增字段
                "doctor_intro, consult_count, avg_score, create_time, update_time " +  // 新增字段
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, 0, 0.00, NOW(), NOW())";  // consult_count默认0，avg_score默认0.00

        try {
            System.out.println("执行医生信息插入SQL: " + sql);
            System.out.println("插入参数：用户ID=" + doctor.getUserId() + ", 姓名=" + doctor.getRealName() + ", 审核员ID=" + doctor.getAuditorId());

            // 执行插入，返回受影响行数（1=成功，0=失败）
            int affectedRows = jdbcTemplate.update(
                    sql,
                    doctor.getUserId(),          // user_id
                    doctor.getRealName(),        // real_name
                    doctor.getTitle(),           // title
                    doctor.getGoodAtType(),      // good_at_type
                    doctor.getQualification(),   // qualification
                    doctor.getConsultationFee(), // consultation_fee
                    doctor.getReceiveStatus() == null ? 1 : doctor.getReceiveStatus(),  // receive_status默认1（可接诊）
                    doctor.getAuditorId(),       // auditor_id（新增字段）
                    doctor.getDoctorIntro() == null ? "" : doctor.getDoctorIntro()     // doctor_intro（新增字段，默认空）
            );

            System.out.println("医生信息插入完成，受影响行数: " + affectedRows);
            return affectedRows;

        } catch (Exception e) {
            System.err.println("插入医生信息失败，用户ID: " + doctor.getUserId());
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法2：更新医生信息（动态更新非空字段） ======================
    @Override
    public int updateDoctor(ConsultDoctor doctor) {
        // 1. 动态拼接SQL，仅更新非空字段
        StringBuilder sql = new StringBuilder("UPDATE consult_doctor SET update_time = NOW()");
        List<Object> params = new ArrayList<>();

        // 适配新增字段：支持更新 doctorIntro、consultationFee、receiveStatus 等
        if (doctor.getTitle() != null && !doctor.getTitle().trim().isEmpty()) {
            sql.append(", title = ?");
            params.add(doctor.getTitle());
        }
        if (doctor.getGoodAtType() != null && !doctor.getGoodAtType().trim().isEmpty()) {
            sql.append(", good_at_type = ?");
            params.add(doctor.getGoodAtType());
        }
        if (doctor.getQualification() != null && !doctor.getQualification().trim().isEmpty()) {
            sql.append(", qualification = ?");
            params.add(doctor.getQualification());
        }
        if (doctor.getConsultationFee() != null) {
            sql.append(", consultation_fee = ?");
            params.add(doctor.getConsultationFee());
        }
        if (doctor.getReceiveStatus() != null) {
            sql.append(", receive_status = ?");
            params.add(doctor.getReceiveStatus());
        }
        if (doctor.getDoctorIntro() != null && !doctor.getDoctorIntro().trim().isEmpty()) {
            sql.append(", doctor_intro = ?");
            params.add(doctor.getDoctorIntro());
        }

        // 必须包含主键ID条件
        sql.append(" WHERE id = ?");
        params.add(doctor.getId());

        try {
            System.out.println("执行医生信息更新SQL: " + sql.toString());
            System.out.println("更新参数: " + params);

            int affectedRows = jdbcTemplate.update(sql.toString(), params.toArray());
            System.out.println("医生信息更新完成，受影响行数: " + affectedRows);
            return affectedRows;

        } catch (Exception e) {
            System.err.println("更新医生信息失败，医生ID: " + doctor.getId());
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法3：更新医生接诊状态（单独提取，便于调用） ======================
    @Override
    public int updateReceiveStatus(Integer doctorId, Integer receiveStatus) {
        String sql = "UPDATE consult_doctor " +
                "SET receive_status = ?, update_time = NOW() " +
                "WHERE id = ?";

        try {
            System.out.println("执行医生接诊状态更新SQL: " + sql);
            System.out.println("医生ID: " + doctorId + ", 目标状态: " + (receiveStatus == 1 ? "可接诊" : "休息中"));

            int affectedRows = jdbcTemplate.update(sql, receiveStatus, doctorId);
            System.out.println("接诊状态更新完成，受影响行数: " + affectedRows);
            return affectedRows;

        } catch (Exception e) {
            System.err.println("更新医生接诊状态失败，医生ID: " + doctorId);
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法4：接诊次数+1（原子操作，避免并发问题） ======================
    @Override
    public int incrementConsultCount(Integer doctorId) {
        // 使用UPDATE ... SET count = count + 1实现原子操作，防止并发时数据不一致
        String sql = "UPDATE consult_doctor " +
                "SET consult_count = consult_count + 1, update_time = NOW() " +
                "WHERE id = ?";

        try {
            System.out.println("执行医生接诊次数自增SQL: " + sql);
            System.out.println("医生ID: " + doctorId);

            int affectedRows = jdbcTemplate.update(sql, doctorId);
            System.out.println("接诊次数自增完成，受影响行数: " + affectedRows);
            return affectedRows;

        } catch (Exception e) {
            System.err.println("医生接诊次数自增失败，医生ID: " + doctorId);
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法5：更新医生平均评分 ======================
    @Override
    public int updateAvgScore(Integer doctorId, BigDecimal newAvgScore) {
        String sql = "UPDATE consult_doctor " +
                "SET avg_score = ?, update_time = NOW() " +
                "WHERE id = ?";

        try {
            System.out.println("执行医生平均评分更新SQL: " + sql);
            System.out.println("医生ID: " + doctorId + ", 新评分: " + newAvgScore);

            int affectedRows = jdbcTemplate.update(sql, newAvgScore, doctorId);
            System.out.println("平均评分更新完成，受影响行数: " + affectedRows);
            return affectedRows;

        } catch (Exception e) {
            System.err.println("更新医生平均评分失败，医生ID: " + doctorId);
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法6：根据擅长品类查询医生列表 ======================
    @Override
    public List<ConsultDoctor> getDoctorsByType(Integer typeId) {
        // 逻辑：good_at_type 是逗号分隔的类型ID字符串，使用 FIND_IN_SET 匹配
        String sql = "SELECT " +
                "id, user_id AS userId, real_name AS realName, title, " +
                "good_at_type AS goodAtType, qualification, " +
                "consultation_fee AS consultationFee, receive_status AS receiveStatus, " +
                "doctor_intro AS doctorIntro, consult_count AS consultCount, " +
                "avg_score AS avgScore, create_time AS createTime " +
                "FROM consult_doctor " +
                "WHERE FIND_IN_SET(?, good_at_type) " +  // 匹配擅长品类
                "AND receive_status = 1 " +  // 仅查询可接诊的医生
                "ORDER BY avg_score DESC, consult_count DESC";  // 按评分和接诊量排序

        try {
            System.out.println("执行按品类查询医生列表SQL: " + sql);
            System.out.println("宠物品类ID: " + typeId);

            List<ConsultDoctor> doctorList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(ConsultDoctor.class),
                    typeId
            );

            System.out.println("按品类查询医生完成，共" + (doctorList == null ? 0 : doctorList.size()) + "条记录");
            return doctorList == null ? new ArrayList<>() : doctorList;

        } catch (Exception e) {
            System.err.println("按品类查询医生失败，品类ID: " + typeId);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ====================== 新增方法7：分页查询医生列表（支持排序） ======================
    @Override
    public List<ConsultDoctor> getDoctorListByPage(Integer pageNum, Integer pageSize, String sortBy) {
        // 处理分页参数默认值
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;

        // 处理排序参数（默认按评分降序）
        String orderBy = "avg_score DESC";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            // 白名单校验：仅允许指定字段排序，防止SQL注入
            if (sortBy.matches("^(avg_score|consult_count|create_time) (ASC|DESC)$")) {
                orderBy = sortBy;
            } else {
                System.out.println("无效的排序参数，使用默认排序: " + sortBy);
            }
        }

        String sql = "SELECT " +
                "id, user_id AS userId, real_name AS realName, title, " +
                "good_at_type AS goodAtType, consultation_fee AS consultationFee, " +
                "receive_status AS receiveStatus, doctor_intro AS doctorIntro, " +
                "consult_count AS consultCount, avg_score AS avgScore, " +
                "create_time AS createTime " +
                "FROM consult_doctor " +
                "ORDER BY " + orderBy + " " +
                "LIMIT ?, ?";

        try {
            System.out.println("执行医生分页查询SQL: " + sql);
            System.out.println("分页参数：pageNum=" + pageNum + ", pageSize=" + pageSize + ", 排序=" + orderBy);

            List<ConsultDoctor> doctorList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(ConsultDoctor.class),
                    offset,
                    pageSize
            );

            System.out.println("医生分页查询完成，共" + (doctorList == null ? 0 : doctorList.size()) + "条记录");
            return doctorList == null ? new ArrayList<>() : doctorList;

        } catch (Exception e) {
            System.err.println("医生分页查询失败");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ====================== 新增方法8：查询所有医生总数 ======================
    @Override
    public int countAllDoctors() {
        String sql = "SELECT COUNT(*) FROM consult_doctor";

        try {
            System.out.println("执行医生总数统计SQL: " + sql);

            Integer totalCount = jdbcTemplate.queryForObject(sql, Integer.class);
            int count = totalCount == null ? 0 : totalCount;
            System.out.println("医生总数统计完成，共" + count + "名医生");
            return count;

        } catch (Exception e) {
            System.err.println("医生总数统计失败");
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 新增方法9：查询医生详情（整合用户基础信息） ======================
    @Override
    public Map<String, Object> getDoctorDetailWithUserInfo(Integer doctorId) {
        // 逻辑：JOIN consult_doctor 和 user_base 表，获取医生完整信息（不含敏感字段）
        String sql = "SELECT " +
                "cd.id AS doctorId, " +
                "cd.user_id AS userId, " +
                "cd.real_name AS realName, " +
                "cd.title, " +
                "cd.good_at_type AS goodAtType, " +
                "cd.qualification, " +
                "cd.consultation_fee AS consultationFee, " +
                "cd.receive_status AS receiveStatus, " +
                "cd.doctor_intro AS doctorIntro, " +
                "cd.consult_count AS consultCount, " +
                "cd.avg_score AS avgScore, " +
                "ub.avatar AS doctorAvatar, " +  // 从user_base获取头像
                "ub.phone AS contactPhone, " +   // 从user_base获取联系电话
                "cd.create_time AS joinTime " +  // 医生入驻时间
                "FROM consult_doctor cd " +
                "LEFT JOIN user_base ub ON cd.user_id = ub.id " +  // 关联用户表
                "WHERE cd.id = ?";

        try {
            System.out.println("执行医生详情查询（含用户信息）SQL: " + sql);
            System.out.println("医生ID: " + doctorId);

            Map<String, Object> doctorDetail = jdbcTemplate.queryForMap(sql, doctorId);
            System.out.println("医生详情查询完成，医生姓名: " + doctorDetail.get("realName"));
            return doctorDetail;

        } catch (Exception e) {
            System.err.println("查询医生详情（含用户信息）失败，医生ID: " + doctorId);
            e.printStackTrace();
            return null;
        }
    }
}