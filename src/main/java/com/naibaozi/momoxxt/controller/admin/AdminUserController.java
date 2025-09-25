/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 17:33:33
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 08:11:17
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/admin/AdminUserController.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-23 17:33:33
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 08:11:17
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/admin/AdminUserController.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
import com.naibaozi.momoxxt.service.VerificationCodeService;
import com.naibaozi.momoxxt.util.CryptoUtil;
import com.naibaozi.momoxxt.util.JwtUtil;
import com.naibaozi.momoxxt.util.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.coyote.http11.Constants.a;


/**
 * 管理员用户管理接口（扩展自现有UserController风格）
 */
@WebServlet("/admin/user/*")
public class AdminUserController extends HttpServlet {

    @Resource
    private UserDao userDao;

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    VerificationCodeService verificationCodeService;
    @Resource
    CryptoUtil cryptoUtil;



    // 管理员角色标识（假设1为管理员）
    private static final int ADMIN_ROLE = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String op = request.getParameter("op");

        try {
            // 统一管理员权限校验（所有接口必须先验证）
            if (!validateAdminPermission(request)) {
                out.write(ResultUtil.error(403, "无管理员权限"));
                return;
            }

            if (op != null) {
                switch (op) {
                    case "listAll":
                        listAllUsers(request, response, out);
                        break;
                    case "getDetail":
                        getAdminUserDetail(request, response, out);
                        break;
                    case "search":
                        searchUsers(request, response, out);
                        break;
                    default:
                        out.write(ResultUtil.error(400, "无效的操作类型"));
                }
            } else {
                out.write(ResultUtil.error(400, "请指定操作类型(op参数)"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "服务器内部错误: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String op = request.getParameter("op");

        try {
            // 统一管理员权限校验
            if (!validateAdminPermission(request)) {
                out.write(ResultUtil.error(403, "无管理员权限"));
                return;
            }

            if (op != null) {
                switch (op) {
                    case "updateStatus":
                        updateUserStatus(request, response, out);
                        break;
                    case "resetPassword":
                        resetUserPassword(request, response, out);
                        break;
                    case "grantAdmin":
                        grantAdminRole(request, response, out);
                        break;
                    default:
                        out.write(ResultUtil.error(400, "无效的操作类型"));
                }
            } else {
                out.write(ResultUtil.error(400, "请指定操作类型(op参数)"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 管理员权限校验核心方法
     * 从Token中解析当前用户ID，查询角色是否为管理员
     */
    private boolean validateAdminPermission(HttpServletRequest request) {
        // 1. 从请求属性获取当前登录用户ID（拦截器已解析Token）
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        if (currentUserId == null) {
            return false;
        }

        // 2. 查询用户角色
        User adminUser = userDao.getUserById(currentUserId);
        return adminUser != null && adminUser.getRole() == ADMIN_ROLE;
    }

    /**
     * 管理员查询所有用户（带分页和状态筛选）
     */
    private void listAllUsers(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 获取分页参数（复用现有分页逻辑）
        String pageNumStr = request.getParameter("pageNum");
        String pageSizeStr = request.getParameter("pageSize");
        String statusStr = request.getParameter("status");

        Integer pageNum = (pageNumStr != null) ? Integer.parseInt(pageNumStr) : 1;
        Integer pageSize = (pageSizeStr != null) ? Integer.parseInt(pageSizeStr) : 10;
        Integer status = (statusStr != null) ? Integer.parseInt(statusStr) : null;

        // 调用DAO分页查询（管理员接口无用户名权限限制）
        List<User> userList = userDao.getUserByPage(pageNum, pageSize, null, status);
        Integer total = userDao.countUser(null, status);

        Map<String, Object> data = new HashMap<>();
        data.put("list", userList);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("total", total);
        data.put("pages", (total + pageSize - 1) / pageSize);
        out.write(ResultUtil.success(data));
    }

    /**
     * 管理员查看用户详情（包含敏感信息）
     */
    private void getAdminUserDetail(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            User user = userDao.getUserById(userId);
            if (user == null) {
                out.write(ResultUtil.error(404, "用户不存在"));
                return;
            }

            // 管理员接口返回完整信息（普通接口需脱敏）
            out.write(ResultUtil.success(user));
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
        }
    }

    /**
     * 管理员搜索用户（支持用户名、邮箱、手机号多条件）
     */
    private void searchUsers(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String keyword = request.getParameter("keyword"); // 搜索关键词
        String pageNumStr = request.getParameter("pageNum");
        String pageSizeStr = request.getParameter("pageSize");

        Integer pageNum = (pageNumStr != null) ? Integer.parseInt(pageNumStr) : 1;
        Integer pageSize = (pageSizeStr != null) ? Integer.parseInt(pageSizeStr) : 10;

        // 实际项目中需在UserDao扩展多条件查询方法
        List<User> userList = userDao.searchUsers(pageNum, pageSize, keyword);
        Integer total = userDao.countSearchUsers(keyword);

        Map<String, Object> data = new HashMap<>();
        data.put("list", userList);
        data.put("total", total);
        out.write(ResultUtil.success(data));
    }

    /**
     * 管理员修改用户状态（启用/禁用）
     */
    private void updateUserStatus(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
        JSONObject param = JSON.parseObject(request.getReader().readLine());
        Long userId = param.getLong("userId");
        Integer status = param.getInteger("status");

        if (userId == null || status == null) {
            out.write(ResultUtil.error(400, "用户ID和状态不能为空"));
            return;
        }

        // 校验用户存在性
        User user = userDao.getUserById(userId);
        if (user == null) {
            out.write(ResultUtil.error(404, "用户不存在"));
            return;
        }

        // 执行状态更新
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        int rows = userDao.updateUser(updateUser);

        if (rows > 0) {
            out.write(ResultUtil.success("状态更新成功"));
        } else {
            out.write(ResultUtil.error(500, "状态更新失败"));
        }
    }

    /**
     * 管理员重置用户密码
     */
    private void resetPassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            // 生成默认密码并加密（例如：123456 -> BCrypt加密）
            String defaultPwd = "123456";
            String encryptedPwd = new CryptoUtil().bcryptEncrypt(defaultPwd);

            int rows = userDao.updatePassword(userId, encryptedPwd);
            if (rows > 0) {
                Map<String, String> data = new HashMap<>();
                data.put("defaultPassword", defaultPwd); // 仅管理员接口返回默认密码
                out.write(ResultUtil.success(data));
            } else {
                out.write(ResultUtil.error(404, "用户不存在"));
            }
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
        }
    }

    /**
     * 管理员授予/撤销管理员权限
     */
    private void grantAdminRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
        JSONObject param = JSON.parseObject(request.getReader().readLine());
        Long userId = param.getLong("userId");
        Integer isAdmin = param.getInteger("isAdmin"); // 1:授予 0:撤销

        if (userId == null || isAdmin == null) {
            out.write(ResultUtil.error(400, "用户ID和权限标识不能为空"));
            return;
        }

        User user = userDao.getUserById(userId);
        if (user == null) {
            out.write(ResultUtil.error(404, "用户不存在"));
            return;
        }

        // 更新角色（实际项目中需在User实体添加role字段）
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setRole(isAdmin == 1 ? ADMIN_ROLE : 0); // 0为普通用户
        int rows = userDao.updateUser(updateUser);

        if (rows > 0) {
            out.write(ResultUtil.success(isAdmin == 1 ? "授予管理员权限成功" : "撤销管理员权限成功"));
        } else {
            out.write(ResultUtil.error(500, "权限更新失败"));
        }
    }
    /**
     * 密码重置功能（通过邮箱验证码）
     * 接收参数：email（邮箱）、code（验证码）、newPassword（前端AES加密后的新密码）
     */
    private void resetUserPassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. 解析请求参数（newPassword是前端AES加密后的字符串）
            JSONObject param = JSON.parseObject(request.getReader().readLine());
            String email = param.getString("email");
            String code = param.getString("code");
            String encryptedNewPassword = param.getString("newPassword");

            // 2. 参数校验
            if (email == null || email.trim().isEmpty() ||
                    code == null || code.trim().isEmpty() ||
                    encryptedNewPassword == null || encryptedNewPassword.trim().isEmpty()) {
                out.write(ResultUtil.error(400, "邮箱、验证码和新密码均为必填项"));
                return;
            }

            // 3. 邮箱格式校验
            String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*\\.com(\\.[a-zA-Z0-9_-]+)*$";
            if (!Pattern.matches(EMAIL_REGEX, email)) {
                out.write(ResultUtil.error(400, "邮箱格式不正确"));
                return;
            }

            // 4. 验证邮箱验证码有效性
            // 注意：根据VerificationCodeService实现，验证成功后会自动移除验证码
            boolean codeValid = verificationCodeService.verifyCode(email, code);
            if (!codeValid) {
                out.write(ResultUtil.error(400, "验证码无效或已过期"));
                return;
            }

            // 5. 校验用户是否存在
            User user = userDao.getUserByEmail(email);
            if (user == null) {
                out.write(ResultUtil.error(404, "该邮箱未注册"));
                return;
            }

            try {
                // 6. AES解密：前端加密的密码 → 明文密码（使用注入的cryptoUtil）
                String plainNewPassword = cryptoUtil.aesDecrypt(encryptedNewPassword);

                // 7. 密码复杂度二次校验（与注册逻辑保持一致）
                if (!isValidPassword(plainNewPassword)) {
                    out.write(ResultUtil.error(400, "密码不符合要求：至少8位，包含字母和数字"));
                    return;
                }

                // 8. BCrypt加密：明文密码 → 哈希值（用于存储到数据库）
                String bcryptNewPassword = cryptoUtil.bcryptEncrypt(plainNewPassword);

                // 9. 执行密码更新
                int rows = userDao.updatePassword(user.getId(), bcryptNewPassword);
                if (rows > 0) {
                    // 注意：此处已移除verificationCodeService.invalidateCode(email);
                    // 因为VerificationCodeService的verifyCode方法在验证成功后已自动移除验证码
                    out.write(ResultUtil.success("密码重置成功，请使用新密码登录"));
                } else {
                    out.write(ResultUtil.error(500, "密码重置失败，请重试"));
                }
            } catch (RuntimeException e) {
                // 捕获AES解密失败的异常（如密钥不匹配、密文损坏等）
                out.write(ResultUtil.error(400, "密码解析失败，请重新输入"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "密码重置异常: " + e.getMessage()));
        }
    }



    private boolean isValidPassword(String password) {
        // 密码长度至少8位
        if (password == null || password.length() < 8) {
            return false;
        }

        // 包含至少一个字母和一个数字
        boolean hasLetter = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }

            // 提前退出循环
            if (hasLetter && hasNumber) {
                break;
            }
        }

        return hasLetter && hasNumber;
    }

}