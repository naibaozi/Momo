package com.naibaozi.momoxxt.controller;

import com.alibaba.fastjson.JSON;
import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
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

/**
 * 用户信息控制器
 * 处理用户相关的HTTP请求，实现CRUD操作（适配新 User 实体类）
 * Author: J.C.ZONG
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/09/26/上午9:08
 * Version 1.0
 */
@WebServlet("/userinfo/*")
public class UserinfoServlet extends HttpServlet {
    // 注入UserDao
    @Resource
    private UserDao userDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型和编码
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取请求路径和操作参数
        String pathInfo = request.getPathInfo();
        String op = request.getParameter("op");

        try {
            // 根据操作类型处理请求
            if (op != null) {
                switch (op) {
                    case "list":
                        listUserinfo(request, response, out);
                        break;
                    case "get":
                        getUserById(request, response, out);
                        break;
                    case "page":
                        getUserByPage(request, response, out);
                        break;
                    case "login":
                        login(request, response, out);
                        break;
                    default:
                        sendErrorResponse(out, 400, "无效的操作类型");
                }
            } else {
                sendErrorResponse(out, 400, "请指定操作类型(op参数)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, 500, "服务器内部错误: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String op = request.getParameter("op");

        try {
            if (op != null) {
                switch (op) {
                    case "add":
                        addUserinfo(request, response, out);
                        break;
                    case "update":
                        updateUserinfo(request, response, out);
                        break;
                    case "delete":
                        deleteUserinfo(request, response, out);
                        break;
                    case "updatePassword":
                        updatePassword(request, response, out);
                        break;
                    default:
                        sendErrorResponse(out, 400, "无效的操作类型");
                }
            } else {
                sendErrorResponse(out, 400, "请指定操作类型(op参数)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, 500, "服务器内部错误: " + e.getMessage());
        }
    }

    /**
     * 查询所有用户信息（适配新字段）
     */
    private void listUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        List<User> userList = userDao.getUserinfoList();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", userList);
        out.write(JSON.toJSONString(result));
    }

    /**
     * 根据ID查询用户（适配新字段）
     */
    private void getUserById(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            sendErrorResponse(out, 400, "用户ID不能为空");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            User user = userDao.getUserById(id);

            if (user != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 200);
                result.put("message", "查询成功");
                result.put("data", user);
                out.write(JSON.toJSONString(result));
            } else {
                sendErrorResponse(out, 404, "用户不存在");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "用户ID格式错误");
        }
    }

    /**
     * 分页查询用户（适配新字段）
     */
    private void getUserByPage(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 获取分页参数
        String pageNumStr = request.getParameter("pageNum");
        String pageSizeStr = request.getParameter("pageSize");
        String username = request.getParameter("username");
        String statusStr = request.getParameter("status");

        // 处理默认值
        Integer pageNum = (pageNumStr != null) ? Integer.parseInt(pageNumStr) : 1;
        Integer pageSize = (pageSizeStr != null) ? Integer.parseInt(pageSizeStr) : 10;
        Integer status = (statusStr != null && !statusStr.isEmpty()) ? Integer.parseInt(statusStr) : null;

        // 执行分页查询
        List<User> userList = userDao.getUserByPage(pageNum, pageSize, username, status);
        Integer total = userDao.countUser(username, status);

        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
                "list", userList,
                "pageNum", pageNum,
                "pageSize", pageSize,
                "total", total,
                "pages", (total + pageSize - 1) / pageSize  // 总页数
        ));
        out.write(JSON.toJSONString(result));
    }

    /**
     * 用户登录（适配userName和passWord字段）
     */
    private void login(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            sendErrorResponse(out, 400, "用户名和密码不能为空");
            return;
        }

        // 适配实体类的userName字段（数据库username）
        User user = userDao.getUserByUsername(username);

        if (user != null && user.getPassWord().equals(password)) {  // 实际项目中应使用加密验证
            // 登录成功，可将用户信息存入session
            request.getSession().setAttribute("loginUser", user);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", user);
            out.write(JSON.toJSONString(result));
        } else {
            sendErrorResponse(out, 401, "用户名或密码错误");
        }
    }

    /**
     * 添加用户（适配realName、role等新字段）
     */
    private void addUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 获取请求参数（新增realName、role、phone等字段）
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String realName = request.getParameter("realName");
        String avatar = request.getParameter("avatar");
        String roleStr = request.getParameter("role");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String statusStr = request.getParameter("status");

        // 参数校验
        if (username == null || password == null) {
            sendErrorResponse(out, 400, "用户名和密码不能为空");
            return;
        }

        // 构建用户对象（适配新实体类字段）
        User user = new User();
        user.setUserName(username);  // 对应实体类userName
        user.setPassWord(password);  // 对应实体类passWord（实际项目中应加密）
        user.setRealName(realName);  // 新增真实姓名字段
        user.setAvatar(avatar);      // 新增头像字段
        user.setPhone(phone);        // 新增手机号字段
        user.setEmail(email);
        // 角色默认1（学生）
        user.setRole(roleStr != null ? Integer.parseInt(roleStr) : 1);
        // 状态默认1（正常）
        user.setStatus(statusStr != null ? Integer.parseInt(statusStr) : 1);

        // 执行添加操作
        Integer userId = userDao.addUser(user);

        if (userId > 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", Map.of("userId", userId));
            out.write(JSON.toJSONString(result));
        } else {
            sendErrorResponse(out, 500, "添加失败");
        }
    }

    /**
     * 更新用户信息（适配realName、role等新字段）
     */
    private void updateUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            sendErrorResponse(out, 400, "用户ID不能为空");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            // 检查用户是否存在
            User user = userDao.getUserById(id);
            if (user == null) {
                sendErrorResponse(out, 404, "用户不存在");
                return;
            }

            // 设置更新字段（新增realName、role、phone等）
            String realName = request.getParameter("realName");
            String avatar = request.getParameter("avatar");
            String roleStr = request.getParameter("role");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String statusStr = request.getParameter("status");

            if (realName != null) user.setRealName(realName);
            if (avatar != null) user.setAvatar(avatar);
            if (roleStr != null) user.setRole(Integer.parseInt(roleStr));
            if (phone != null) user.setPhone(phone);
            if (email != null) user.setEmail(email);
            if (statusStr != null) user.setStatus(Integer.parseInt(statusStr));
            user.setId(id);

            // 执行更新
            Integer rows = userDao.updateUser(user);

            if (rows > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 200);
                result.put("message", "更新成功");
                out.write(JSON.toJSONString(result));
            } else {
                sendErrorResponse(out, 500, "更新失败");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "参数格式错误");
        }
    }

    /**
     * 更新密码（适配passWord字段）
     */
    private void updatePassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idStr = request.getParameter("id");
        String newPassword = request.getParameter("newPassword");

        if (idStr == null || newPassword == null) {
            sendErrorResponse(out, 400, "用户ID和新密码不能为空");
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);
            // 适配实体类passWord字段
            Integer rows = userDao.updatePassword(id, newPassword);  // 实际项目中应加密

            if (rows > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 200);
                result.put("message", "密码更新成功");
                out.write(JSON.toJSONString(result));
            } else {
                sendErrorResponse(out, 404, "用户不存在或密码未变更");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "用户ID格式错误");
        }
    }

    /**
     * 删除用户
     */
    private void deleteUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            sendErrorResponse(out, 400, "用户ID不能为空");
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);
            Integer rows = userDao.deleteUser(id);

            if (rows > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 200);
                result.put("message", "删除成功");
                out.write(JSON.toJSONString(result));
            } else {
                sendErrorResponse(out, 404, "用户不存在");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "用户ID格式错误");
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(PrintWriter out, int code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        out.write(JSON.toJSONString(error));
    }
}
