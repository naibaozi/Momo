/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 09:10:02
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/UserController.java
 * @Description: 用户核心功能控制器（登录、注册、信息管理等）
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
import com.naibaozi.momoxxt.service.VerificationCodeService;
import com.naibaozi.momoxxt.util.CryptoUtil;
import com.naibaozi.momoxxt.util.EmailUtil;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Pattern;


@WebServlet("/user/*")
public class UserController extends HttpServlet {

    // 注入加密工具
    @Resource
    private CryptoUtil cryptoUtil;

    // 注入UserDao
    @Resource
    private UserDao userDao;

    // 注入邮件工具
    @Resource
    private EmailUtil emailUtil;

    // 注入验证码服务
    @Resource
    private VerificationCodeService verificationCodeService;

    // 邮箱正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型和编码
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取操作参数
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
                    // 新增：发送邮箱验证码
                    case "sendEmailCode":
                        sendEmailCode(request, response, out);
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
                    // 新增：验证邮箱验证码
                    case "verifyEmailCode":
                        verifyEmailCode(request, response, out);
                        break;
                    case "bindWechat": // 微信绑定
                        bindWechat(request, response, out);
                        break;
                    case "wechatLogin": // 微信登录
                        wechatQuickLogin(request, response, out);
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
     * 1. 微信绑定接口（处理 op=bindWechat 请求）
     * 接收参数：code（微信临时code）、userId（当前用户ID）、nickName（可选）、avatarUrl（可选）
     */
    private void bindWechat(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 1. 获取前端传递的参数
        String code = request.getParameter("code");
        String userIdStr = request.getParameter("userId");
        String nickName = request.getParameter("nickName");
        String avatarUrl = request.getParameter("avatarUrl");

        // 2. 基础参数校验
        if (code == null || code.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "微信code不能为空"));
            return;
        }
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            // 3. 调用微信接口兑换openid（替换为你的小程序appid和secret）
            String appId = "wxe69172f88dbfc63a";
            String appSecret = "e843267e290f1483081c58ee5fb36540";
            String wechatApi = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, appSecret, code
            );

            // 4. 发送HTTP请求到微信接口
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(wechatApi)).GET().build();
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // ------------ 新增关键日志 ------------
            String wechatRespStr = httpResponse.body(); // 微信接口的完整返回数据
            System.out.println("=== 微信接口调试信息 ===");
            System.out.println("请求地址: " + wechatApi); // 打印完整请求URL（可直接复制到浏览器测试）
            System.out.println("微信返回完整数据: " + wechatRespStr); // 核心！打印微信的错误信息
            System.out.println("=====================================");
            // -------------------------------------

            // 2. 解析微信响应（优先处理错误）
            JSONObject wechatResp = JSONObject.parseObject(wechatRespStr);
            // 3. 解析openid并校验

            // 关键：先判断微信是否返回错误码
            if (wechatResp.containsKey("errCode")) {
                int errCode = wechatResp.getIntValue("errCode");
                String errMsg = wechatResp.getString("errMsg");
                // 直接返回微信的错误信息，便于定位问题
                out.write(ResultUtil.error(400, "微信接口错误: " + errCode + " - " + errMsg));
                return;
            }

            String openid = wechatResp.getString("openid");
            if (openid == null || openid.trim().isEmpty()) {
                out.write(ResultUtil.error(400, "未获取到openid，微信返回: " + wechatRespStr));
                return;
            }

            // 6. 校验用户存在性并更新openid
            User user = userDao.getUserById(userId);
            if (user == null) {
                out.write(ResultUtil.error(404, "用户不存在"));
                return;
            }
            // 更新用户信息（openid + 可选的昵称/头像）
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setOpenId(openid);
            if (nickName != null && !nickName.trim().isEmpty()) {
                updateUser.setNickName(nickName);
            }
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                updateUser.setAvatar(avatarUrl);
            }
            userDao.updateUser(updateUser);

            // 7. 返回成功结果
            Map<String, Object> data = new HashMap<>();
            data.put("openId", openid);
            data.put("nickName", updateUser.getNickName());
            data.put("avatarUrl", updateUser.getAvatar());
            out.write(ResultUtil.success(data));

        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "绑定失败：" + e.getMessage()));
        }
    }

    /**
     * 2. 微信登录接口（处理 op=wechatLogin 请求）
     * 接收参数：code（微信临时code）
     */
    private void wechatQuickLogin(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 1. 获取前端传递的微信code
        String code = request.getParameter("code");
        if (code == null || code.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "微信code不能为空"));
            return;
        }

        try {
            // 2. 调用微信接口兑换openid（同上，使用你的appid和secret）
            String appId = "wxe69172f88dbfc63a";
            String appSecret = "e843267e290f1483081c58ee5fb36540";
            String wechatApi = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, appSecret, code
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(wechatApi)).GET().build();
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // ------------ 新增关键日志 ------------
            String wechatRespStr = httpResponse.body(); // 微信接口的完整返回数据
            System.out.println("=== 微信接口调试信息 ===");
            System.out.println("请求地址: " + wechatApi); // 打印完整请求URL（可直接复制到浏览器测试）
            System.out.println("微信返回完整数据: " + wechatRespStr); // 核心！打印微信的错误信息
            System.out.println("=====================================");
            // -------------------------------------

            // 2. 解析微信响应（优先处理错误）
            JSONObject wechatResp = JSONObject.parseObject(wechatRespStr);
            // 3. 解析openid并校验
            // 关键：先判断微信是否返回错误码
            if (wechatResp.containsKey("errCode")) {
                int errCode = wechatResp.getIntValue("errCode");
                String errMsg = wechatResp.getString("errMsg");
                // 直接返回微信的错误信息，便于定位问题
                out.write(ResultUtil.error(400, "微信接口错误: " + errCode + " - " + errMsg));
                return;
            }

            String openid = wechatResp.getString("openid");
            if (openid == null || openid.trim().isEmpty()) {
                out.write(ResultUtil.error(400, "未获取到openid，微信返回: " + wechatRespStr));
                return;
            }

            // 4. 根据openid查询绑定的用户
            User user = userDao.getUserByOpenId(openid); // 需在UserDao中新增该方法
            if (user == null) {
                out.write(ResultUtil.error(404, "该微信未绑定账号，请先绑定"));
                return;
            }

            // 5. 生成JWT Token（与账号密码登录逻辑一致）
            String token = JwtUtil.generateToken(user.getId(), user.getUserName());

            // 6. 返回登录成功结果（包含token和用户信息）
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            out.write(ResultUtil.success(data));

        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "登录失败：" + e.getMessage()));
        }
    }

    /**
     * 发送邮箱验证码
     */
    private void sendEmailCode(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String email = request.getParameter("email");

        // 验证邮箱格式
        if (email == null || email.trim().isEmpty() || !Pattern.matches(EMAIL_REGEX, email.trim())) {
            out.write(ResultUtil.error(400, "请输入有效的邮箱地址"));
            return;
        }

        email = email.trim();

        // 检查是否可以发送新的验证码
        if (!verificationCodeService.canSendNewCode(email)) {
            out.write(ResultUtil.error(400, "验证码发送过于频繁，请1分钟后再试"));
            return;
        }

        // 生成验证码
        String code = emailUtil.generateVerificationCode();

        // 发送邮件
        boolean sendSuccess = emailUtil.sendVerificationCode(email, code);

        if (sendSuccess) {
            // 存储验证码
            verificationCodeService.storeCode(email, code);
            out.write(ResultUtil.success("验证码已发送至您的邮箱，有效期5分钟"));
        } else {
            out.write(ResultUtil.error(500, "验证码发送失败，请稍后重试"));
        }
    }

    /**
     * 验证邮箱验证码
     */
    private void verifyEmailCode(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String email = request.getParameter("email");
        String code = request.getParameter("code");

        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "邮箱和验证码不能为空"));
            return;
        }

        boolean isValid = verificationCodeService.verifyCode(email.trim(), code.trim());

        if (isValid) {
            out.write(ResultUtil.success("验证码验证成功"));
        } else {
            out.write(ResultUtil.error(400, "验证码无效或已过期"));
        }
    }

    /**
     * 查询所有用户信息（适配新字段）
     */
    private void listUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        List<User> userList = userDao.getUserinfoList();
        out.write(ResultUtil.success(userList));
    }

    /**
     * 根据ID查询用户（适配新字段）
     */
    private void getUserById(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            User user = userDao.getUserById(id);

            if (user != null) {
                out.write(ResultUtil.success(user));
            } else {
                out.write(ResultUtil.error(404, "用户不存在"));
            }
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
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

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", userList);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("total", total);
        data.put("pages", (total + pageSize - 1) / pageSize);  // 总页数
        out.write(ResultUtil.success(data));
    }

    /**
     * 用户登录（适配 AES 解密 + BCrypt 哈希校验 + JWT Token 生成）
     */
    private void login(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws ServletException, IOException {
        // 1. 获取前端参数（password 是 AES 加密后的字符串）
        String username = request.getParameter("username");
        String encryptedPwd = request.getParameter("password"); // 加密后的密码

        // 2. 基础校验（非空 + 非空白）
        if (username == null || encryptedPwd == null ||
                username.trim().isEmpty() || encryptedPwd.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户名和密码不能为空"));
            return;
        }

        // 3. 根据用户名查询数据库（获取存储的 BCrypt 哈希值）
        User user = userDao.getUserByUsername(username.trim());
        if (user == null) {
            out.write(ResultUtil.error(401, "用户名或密码错误"));
            return;
        }

        try {
            // 4. AES 解密：前端加密字符串 → 明文密码
            String plainPwd = cryptoUtil.aesDecrypt(encryptedPwd);

            // 5. BCrypt 校验：明文密码 vs 数据库存储的哈希值
            boolean isPwdMatch = cryptoUtil.bcryptMatches(plainPwd, user.getPassWord());

            if (isPwdMatch) {
                // 6. 登录成功：生成 JWT Token（传入用户ID和用户名）
                String token = JwtUtil.generateToken(user.getId(), user.getUserName());

                // 7. 构建响应数据
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user);
                out.write(ResultUtil.success(data));
            } else {
                out.write(ResultUtil.error(401, "用户名或密码错误"));
            }
        } catch (RuntimeException e) {
            out.write(ResultUtil.error(401, "密码解析失败，请重新输入"));
        }
    }

    /**
     * 添加用户（适配邮箱验证和密码复杂度校验，仅处理前端提供的字段）
     */
    private void addUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
        // 只获取前端提供的四个核心字段
        JSONObject param = JSON.parseObject(request.getReader().readLine());
        String username = param.getString("username");
        String encryptedPwd = param.getString("password");
        String email = param.getString("email");
        String code = param.getString("code");

        // 1. 验证邮箱和验证码
        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "邮箱和验证码不能为空"));
            return;
        }

        // 2. 验证邮箱验证码是否有效
        boolean isCodeValid = verificationCodeService.verifyCode(email.trim(), code.trim());
        if (!isCodeValid) {
            out.write(ResultUtil.error(400, "验证码无效或已过期"));
            return;
        }

        // 3. 基础校验（仅校验前端提供的必填字段）
        if (username == null || encryptedPwd == null ||
                username.trim().isEmpty() || encryptedPwd.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户名和密码不能为空"));
            return;
        }

        // 4. 用户名唯一性校验
        User existingUser = userDao.getUserByUsername(username.trim());
        if (existingUser != null) {
            out.write(ResultUtil.error(400, "用户名已经存在"));
            return;
        }

        // 5. 邮箱唯一性校验
        User userByEmail = userDao.getUserByEmail(email.trim());
        if (userByEmail != null) {
            out.write(ResultUtil.error(400, "该邮箱已被注册"));
            return;
        }

        // 6. AES 解密：加密字符串→明文密码
        String plainPwd = cryptoUtil.aesDecrypt(encryptedPwd);
        if (plainPwd == null) {
            out.write(ResultUtil.error(400, "密码解密失败"));
            return;
        }

        // 7. 后端密码复杂度校验
        if (!isValidPassword(plainPwd)) {
            out.write(ResultUtil.error(400, "密码不符合要求：至少8位，包含字母和数字"));
            return;
        }

        // 8. BCrypt 加密：明文→哈希值（用于存储）
        String bcryptPwd = cryptoUtil.bcryptEncrypt(plainPwd);

        // 9. 构建用户对象（添加createTime初始化）
        User user = new User();
        user.setUserName(username.trim());
        user.setPassWord(bcryptPwd);
        user.setEmail(email.trim());
        user.setCreateTime(new Date());
        user.setOpenId("");
        user.setAvatar("");
        user.setPhone("");
        user.setNickName(username.trim());
        user.setStatus(1);

        Integer newId = userDao.addUser(user);

        if (newId > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", newId);
            out.write(ResultUtil.success(data));
        } else {
            out.write(ResultUtil.error(500, "注册失败"));
        }
    }

    /**
     * 密码复杂度校验工具方法
     * 要求：至少8位，包含至少一个字母和一个数字
     */
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

    /**
     * 更新用户信息（新增邮箱变更+验证码校验逻辑）
     */
    private void updateUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 1. 获取前端传递的所有参数（新增 emailCode 验证码参数）
        String idStr = request.getParameter("id");
        String nickname = request.getParameter("nickname");
        String openId = request.getParameter("openId");
        String avatar = request.getParameter("avatar");
        String phone = request.getParameter("phone");
        String newEmail = request.getParameter("email"); // 前端传递的新邮箱
        String emailCode = request.getParameter("emailCode"); // 前端传递的邮箱验证码
        String statusStr = request.getParameter("status");


        // 2. 基础校验：用户ID必传且格式正确
        if (idStr == null || idStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            Long updateId = Long.parseLong(idStr);
            Long currentUserId = (Long) request.getAttribute("currentUserId");

            // 3. 权限校验：仅能更新自己的信息
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请重新登录"));
                return;
            }
            if (!updateId.equals(currentUserId)) {
                out.write(ResultUtil.error(403, "权限不足，仅能更新自己的信息"));
                return;
            }


            // 4. 检查用户是否存在（核心：必须先查询旧数据，用于对比邮箱）
            User oldUser = userDao.getUserById(updateId);
            if (oldUser == null) {
                System.out.println("用户不存在 - ID: " + updateId);
                out.write(ResultUtil.error(404, "用户不存在"));
                return;
            }

            // ---------------------- 新增核心逻辑：邮箱变更校验 ----------------------
            boolean isEmailChanged = false; // 标记邮箱是否有修改
            String oldEmail = oldUser.getEmail(); // 数据库中的旧邮箱

            // 4.1 检测邮箱是否变更（新邮箱不为空，且与旧邮箱不同）
            if (newEmail != null && !newEmail.trim().isEmpty()
                    && !newEmail.trim().equals(oldEmail)) {
                isEmailChanged = true;

                // 4.2 邮箱变更时，强制校验验证码
                if (emailCode == null || emailCode.trim().isEmpty()) {
                    out.write(ResultUtil.error(400, "请输入邮箱验证码"));
                    return;
                }

                // 4.3 调用验证码服务验证（复用注册时的验证逻辑）
                boolean isCodeValid = verificationCodeService.verifyCode(newEmail.trim(), emailCode.trim());
                if (!isCodeValid) {
                    out.write(ResultUtil.error(400, "验证码无效或已过期"));
                    return;
                }

                // 4.4 新邮箱唯一性校验（防止被其他用户占用）
                User userByNewEmail = userDao.getUserByEmail(newEmail.trim());
                if (userByNewEmail != null) {
                    out.write(ResultUtil.error(400, "该邮箱已被注册"));
                    return;
                }

            }
            // ---------------------------------------------------------------------

            // 5. 构建更新对象（处理所有字段，为空则保留原值）
            User updateUser = new User();
            updateUser.setId(updateId);

            // 昵称
            updateUser.setNickName(nickname != null && !nickname.trim().isEmpty()
                    ? nickname.trim() : oldUser.getNickName());
            // 头像
            updateUser.setAvatar(avatar != null && !avatar.trim().isEmpty()
                    ? avatar.trim() : oldUser.getAvatar());
            // 手机号
            updateUser.setPhone(phone != null && !phone.trim().isEmpty()
                    ? phone.trim() : oldUser.getPhone());
            // 邮箱（若变更则用新邮箱，否则保留旧邮箱）
            updateUser.setEmail(isEmailChanged ? newEmail.trim() : oldUser.getEmail());
            // 状态
            updateUser.setStatus(statusStr != null && !statusStr.trim().isEmpty()
                    ? Integer.parseInt(statusStr.trim()) : oldUser.getStatus());

            // 6. 执行更新操作
            Integer rows = userDao.updateUser(updateUser);
            System.out.println("更新结果 - 影响行数: " + rows + ", 用户ID: " + updateId);

            // 7. 响应结果
            if (rows > 0) {
                User updatedUser = userDao.getUserById(updateId);
                out.write(ResultUtil.success(updatedUser));
            } else {
                out.write(ResultUtil.error(500, "更新失败，未修改任何内容"));
            }

        } catch (NumberFormatException e) {
            System.err.println("ID格式错误: " + e.getMessage());
            out.write(ResultUtil.error(400, "用户ID必须为数字"));
        } catch (Exception e) {
            System.err.println("更新异常: " + e.getMessage());
            out.write(ResultUtil.error(500, "服务器内部错误: " + e.getMessage()));
        }
    }


    /**
     * 更新密码
     */
    private void updatePassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        // 1. 获取参数（newPassword 是前端 AES 加密后的字符串）
        String idStr = request.getParameter("id");
        String encryptedNewPwd = request.getParameter("newPassword");

        // 2. 基础校验
        if (idStr == null || encryptedNewPwd == null ||
                idStr.trim().isEmpty() || encryptedNewPwd.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID和新密码不能为空"));
            return;
        }

        try {
            // 3. 解析用户ID
            Long id = Long.parseLong(idStr);

            // 4. AES 解密：加密字符串 → 明文新密码
            String plainNewPwd = cryptoUtil.aesDecrypt(encryptedNewPwd);

            // 5. BCrypt 加密：明文 → 哈希值（存储到数据库）
            String bcryptNewPwd = cryptoUtil.bcryptEncrypt(plainNewPwd);

            // 6. 执行密码更新（存储哈希值）
            Integer rows = userDao.updatePassword(id, bcryptNewPwd);

            if (rows > 0) {
                out.write(ResultUtil.success("密码更新成功"));
            } else {
                out.write(ResultUtil.error(404, "用户不存在或密码未变更"));
            }
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
        } catch (RuntimeException e) {
            out.write(ResultUtil.error(500, "密码处理失败：" + e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    private void deleteUserinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            Integer rows = userDao.deleteUser(id);

            if (rows > 0) {
                out.write(ResultUtil.success("删除成功"));
            } else {
                out.write(ResultUtil.error(404, "用户不存在"));
            }
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
        }
    }
}