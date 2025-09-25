package com.naibaozi.momoxxt.controller.admin;

import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.dao.ConsultDoctorDao;
import com.naibaozi.momoxxt.entity.ConsultDoctor;
import com.naibaozi.momoxxt.entity.User;
import com.naibaozi.momoxxt.util.JwtUtil;
import com.naibaozi.momoxxt.util.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员医生资质审核控制器（适配最新表结构与DAO方法）
 */
@WebServlet("/admin/doctor/*") // 接口路径：/admin/doctor?op=xxx
public class AdminDoctorAuditController extends HttpServlet {

    // 角色常量
    private static final int ADMIN_ROLE = 1;    // 管理员角色
    private static final int DOCTOR_ROLE = 2;   // 医生角色
    // 审核状态常量
    private static final int AUDIT_PENDING = 0; // 待审核
    private static final int AUDIT_PASS = 1;    // 审核通过
    private static final int AUDIT_REJECT = 2;  // 审核拒绝

    // 注入依赖
    @Resource
    private UserDao userDao;
    @Resource
    private ConsultDoctorDao consultDoctorDao;
    @Resource
    private JwtUtil jwtUtil;

    /**
     * 处理GET请求（查询类操作：待审核数量、列表、审核详情）
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String op = request.getParameter("op"); // 操作类型：detail/getPendingDoctorCount等

        try {
            // 1. 管理员权限校验（必须先通过）
            if (!validateAdminPermission(request)) {
                out.write(ResultUtil.error(403, "无管理员权限，无法操作医生审核"));
                return;
            }

            // 2. 操作类型为空校验
            if (op == null || op.trim().isEmpty()) {
                out.write(ResultUtil.error(400, "请指定操作类型（op参数），支持：detail、getPendingDoctorCount、getPendingDoctorList"));
                return;
            }

            // 3. 按操作类型分发处理
            switch (op) {
                case "detail":
                    // 适配：查询医生审核详情（整合用户基础信息+医生资质信息）
                    getDoctorAuditDetail(request, out);
                    break;
                case "getPendingDoctorCount":
                    // 原有：获取待审核医生数量（逻辑优化）
                    getPendingDoctorCount(out);
                    break;
                case "getPendingDoctorList":
                    // 修复：获取待审核医生列表（带分页，适配DAO新方法）
                    getPendingDoctorList(request, out);
                    break;
                default:
                    out.write(ResultUtil.error(400, "无效的操作类型：" + op));
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "服务器内部错误：" + e.getMessage()));
        } finally {
            out.close();
        }
    }

    /**
     * 处理POST请求（操作类：审核通过/拒绝）
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String op = request.getParameter("op"); // 操作类型：action（前端统一调用）

        try {
            // 1. 管理员权限校验
            if (!validateAdminPermission(request)) {
                out.write(ResultUtil.error(403, "无管理员权限，无法操作医生审核"));
                return;
            }

            // 2. 操作类型校验（仅支持action）
            if (!"action".equals(op)) {
                out.write(ResultUtil.error(400, "仅支持op=action操作，用于审核通过/拒绝"));
                return;
            }

            // 3. 读取POST请求体中的参数（auditId、status、rejectRemark（可选））
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            // 解析JSON参数（使用FastJSON）
            Map<String, Object> paramMap = JSON.parseObject(
                    sb.toString(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // 4. 参数校验（auditId和status必传，拒绝时可传rejectRemark）
            Long auditId = paramMap.get("auditId") == null ? null : Long.parseLong(paramMap.get("auditId").toString());
            Integer status = paramMap.get("status") == null ? null : Integer.parseInt(paramMap.get("status").toString());
            String rejectRemark = paramMap.get("rejectRemark") == null ? "" : paramMap.get("rejectRemark").toString(); // 拒绝原因（可选）

            if (auditId == null || status == null) {
                out.write(ResultUtil.error(400, "缺少必要参数：auditId（审核ID）或 status（审核状态，1-通过，2-拒绝）"));
                return;
            }
            if (status != AUDIT_PASS && status != AUDIT_REJECT) {
                out.write(ResultUtil.error(400, "status参数无效，仅支持1（通过）或2（拒绝）"));
                return;
            }
            // 拒绝时必须传拒绝原因
            if (status == AUDIT_REJECT && (rejectRemark == null || rejectRemark.trim().isEmpty())) {
                out.write(ResultUtil.error(400, "拒绝审核时必须填写拒绝原因（rejectRemark）"));
                return;
            }

            // 5. 执行审核操作（通过/拒绝）
            handleDoctorAudit(auditId, status, rejectRemark, request, out);

        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "服务器内部错误：" + e.getMessage()));
        } finally {
            out.close();
        }
    }

    // ------------------------------ 核心业务方法（适配更新） ------------------------------

    /**
     * 1. 管理员权限校验（基于Token解析的currentUserId）
     */
    private boolean validateAdminPermission(HttpServletRequest request) {
        // 前提：Token拦截器已将解析后的userId放入request属性（key=currentUserId）
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        if (currentUserId == null) {
            return false; // 未登录或Token无效
        }

        // 查询用户角色是否为管理员（适配User实体新增字段）
        User adminUser = userDao.getUserById(currentUserId);
        return adminUser != null && ADMIN_ROLE == adminUser.getRole() && adminUser.getStatus() == 1; // 新增：仅启用状态的管理员可操作
    }

    /**
     * 2. 适配：查询医生审核详情（整合用户基础信息+医生资质信息）
     * 修正：移除不存在的getDoctorQualificationByUserId，改用getDoctorByUserId
     */
    private void getDoctorAuditDetail(HttpServletRequest request, PrintWriter out) {
        // 获取前端传递的auditId（即医生的userId）
        String auditIdStr = request.getParameter("auditId");
        if (auditIdStr == null || auditIdStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "缺少auditId参数（医生审核ID）"));
            return;
        }
        Long auditId = Long.parseLong(auditIdStr);

        // 步骤1：查询医生基础信息（姓名、性别、年龄、身份证照片等）
        User doctorBaseInfo = userDao.getDoctorAuditDetail(auditId);
        if (doctorBaseInfo == null) {
            out.write(ResultUtil.error(404, "未找到该审核ID对应的医生信息"));
            return;
        }

        // 步骤2：查询医生资质信息（职称、擅长品类、执业证书）- 适配DAO新方法
        ConsultDoctor doctorQualification = consultDoctorDao.getDoctorByUserId(Math.toIntExact(auditId));

        // 步骤3：组装详情数据（合并基础信息和资质信息，适配字段名）
        Map<String, Object> detailData = new HashMap<>();
        detailData.put("name", doctorBaseInfo.getRealName());          // 医生姓名
        detailData.put("gender", doctorBaseInfo.getGender());         // 性别（1-男，2-女）
        detailData.put("age", doctorBaseInfo.getAge());               // 年龄
        detailData.put("phone", doctorBaseInfo.getPhone());           // 联系电话
        // 适配：consult_doctor表无department字段，用goodAtType（擅长品类）替代
        detailData.put("department", doctorQualification != null ? doctorQualification.getGoodAtType() : "");
        detailData.put("title", doctorQualification != null ? doctorQualification.getTitle() : "");         // 职称
        detailData.put("idCardFrontUrl", doctorBaseInfo.getIdCardFrontUrl()); // 身份证正面
        detailData.put("idCardBackUrl", doctorBaseInfo.getIdCardBackUrl());   // 身份证背面
        // 适配：执业证书URL对应consult_doctor的qualification字段
        detailData.put("licenseUrl", doctorQualification != null ? doctorQualification.getQualification() : "");
        // 新增：审核状态和拒绝原因（前端展示）
        detailData.put("auditStatus", doctorBaseInfo.getAuditStatus());
        detailData.put("auditRemark", doctorBaseInfo.getAuditRemark());

        // 返回详情数据（前端直接接收并渲染）
        out.write(ResultUtil.success(detailData));
    }

    /**
     * 3. 优化：获取待审核医生数量（逻辑简化，基于audit_status字段）
     */
    private void getPendingDoctorCount(PrintWriter out) {
        // 优化：直接查询user_base表中role=2且audit_status=0的用户数，无需二次过滤
        // 替代原逻辑：allDoctorUserIds.removeAll(auditedDoctorUserIds)
        int pendingCount = userDao.getPendingDoctorTotalCount(null); // 传入null，由DAO处理查询条件

        // 组装结果（便于前端扩展）
        Map<String, Object> result = new HashMap<>();
        result.put("pendingDoctorCount", pendingCount);
        result.put("message", "待审核医生数量查询成功");
        out.write(ResultUtil.success(result));
    }

    /**
     * 4. 适配：获取待审核医生列表（带分页，基于audit_status字段）
     */
    private void getPendingDoctorList(HttpServletRequest request, PrintWriter out) {
        // 分页参数（默认第1页，每页10条）
        int pageNum = request.getParameter("pageNum") == null ? 1 : Integer.parseInt(request.getParameter("pageNum"));
        int pageSize = request.getParameter("pageSize") == null ? 10 : Integer.parseInt(request.getParameter("pageSize"));
        int offset = (pageNum - 1) * pageSize; // MySQL分页偏移量

        // 优化：直接查询待审核医生（role=2且audit_status=0），无需先查所有医生再过滤
        // 步骤1：查询待审核医生总数（适配DAO新方法，传入null表示查询所有待审核）
        int totalCount = userDao.getPendingDoctorTotalCount(null);
        // 步骤2：分页查询待审核医生列表（传入null，由DAO处理查询条件）
        List<User> pendingDoctorList = userDao.getPendingDoctorListByPage(null, offset, pageSize);

        // 步骤3：组装分页结果（与前端分页参数对齐）
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("pendingDoctorList", pendingDoctorList); // 待审核列表
        pageResult.put("pageNum", pageNum);                     // 当前页码
        pageResult.put("pageSize", pageSize);                   // 每页条数
        pageResult.put("totalCount", totalCount);               // 总条数
        pageResult.put("totalPages", totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / pageSize)); // 总页数

        out.write(ResultUtil.success(pageResult));
    }

    /**
     * 5. 适配：处理医生审核操作（通过/拒绝）
     * 修正：调用DAO的insertDoctor方法，移除不存在的getDoctorQualificationByUserId和insertConsultDoctor
     */
    private void handleDoctorAudit(Long auditId, Integer status, String rejectRemark, HttpServletRequest request, PrintWriter out) {
        // 步骤1：查询医生是否存在（且为待审核状态）
        User doctor = userDao.getDoctorAuditDetail(auditId);
        if (doctor == null || doctor.getRole() != DOCTOR_ROLE) {
            out.write(ResultUtil.error(404, "该审核ID对应的医生不存在或非医生角色"));
            return;
        }
        if (doctor.getAuditStatus() != AUDIT_PENDING) {
            out.write(ResultUtil.error(400, "该医生已审核，无需重复操作（当前状态：" + getAuditStatusDesc(doctor.getAuditStatus()) + "）"));
            return;
        }

        // 步骤2：更新医生审核状态（user_base表，拒绝时需更新auditRemark）
        User updateUser = new User();
        updateUser.setId(auditId);
        updateUser.setAuditStatus(status);
        if (status == AUDIT_REJECT) {
            updateUser.setAuditRemark(rejectRemark); // 拒绝时设置拒绝原因
        }
        int updateStatusRows = userDao.updateUser(updateUser); // 调用updateUser动态更新
        if (updateStatusRows <= 0) {
            out.write(ResultUtil.error(500, "审核状态更新失败，请重试"));
            return;
        }

        // 步骤3：若审核通过，新增consult_doctor关联（建立医生资质关联）
        if (status == AUDIT_PASS) {
            // 获取审核管理员ID（从Token解析的currentUserId）
            Long auditorId = (Long) request.getAttribute("currentUserId");

            // 构建ConsultDoctor对象（适配实体新增字段）
            ConsultDoctor consultDoctor = new ConsultDoctor();
            consultDoctor.setUserId(Math.toIntExact(auditId));          // 关联医生用户ID
            consultDoctor.setRealName(doctor.getRealName());            // 医生真实姓名（与user_base一致）
            consultDoctor.setAuditorId(Math.toIntExact(auditorId));     // 审核管理员ID（适配int类型）
            // 基础默认值：初始可接诊、问诊费默认59.90、接诊次数0、评分0.00
            consultDoctor.setReceiveStatus(1);                          // 1=可接诊
            consultDoctor.setConsultationFee(new BigDecimal("59.90"));  // 单次问诊费默认59.90
            consultDoctor.setConsultCount(0);                           // 初始接诊次数0
            consultDoctor.setAvgScore(new BigDecimal("0.00"));          // 初始评分0.00

            // 从医生提交的资质信息中补充字段（假设前端已提前保存到consult_doctor表）
            ConsultDoctor tempQualification = consultDoctorDao.getDoctorByUserId(Math.toIntExact(auditId));
            if (tempQualification != null) {
                consultDoctor.setTitle(tempQualification.getTitle());                  // 职称
                consultDoctor.setGoodAtType(tempQualification.getGoodAtType());        // 擅长品类
                consultDoctor.setQualification(tempQualification.getQualification());  // 执业证书URL
                consultDoctor.setDoctorIntro(tempQualification.getDoctorIntro());      // 医生简介
            }

            // 插入关联记录（调用DAO的insertDoctor方法）
            int insertRows = consultDoctorDao.insertDoctor(consultDoctor);
            if (insertRows <= 0) {
                // 回滚审核状态（避免状态不一致）
                updateUser.setAuditStatus(AUDIT_PENDING);
                updateUser.setAuditRemark("");
                userDao.updateUser(updateUser);
                out.write(ResultUtil.error(500, "审核通过，但医生资质关联失败，请重试"));
                return;
            }
        }

        // 步骤4：返回成功结果
        String message = status == AUDIT_PASS ? "审核通过" : "审核拒绝（原因：" + rejectRemark + "）";
        out.write(ResultUtil.success(message));
    }

    // ------------------------------ 工具方法 ------------------------------
    /**
     * 工具方法：将审核状态码转换为描述（如1→"已通过"）
     */
    private String getAuditStatusDesc(Integer auditStatus) {
        switch (auditStatus) {
            case AUDIT_PENDING:
                return "待审核";
            case AUDIT_PASS:
                return "已通过";
            case AUDIT_REJECT:
                return "已拒绝";
            default:
                return "未知状态";
        }
    }
}