/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-20 17:01:39
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 21:08:50
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/UploadController.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-20 17:01:39
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 21:08:50
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/UploadController.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.controller;

import com.alibaba.fastjson.JSON;
import com.naibaozi.momoxxt.dao.UserDao;
import com.naibaozi.momoxxt.entity.User;
import com.naibaozi.momoxxt.util.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 处理文件上传的控制器，特别是头像上传
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    // 从配置文件读取基础上传路径和访问前缀
    @Value("${upload.avatar.base-path}")
    private String avatarBasePath; // 基础路径：D:/project/naibaozi/momo/upload

    @Value("${upload.avatar.url-prefix}")
    private String avatarUrlPrefix;

    // 直接注入UserDao，跳过Servlet层（核心修复）
    @Resource
    private UserDao userDao;

    /**
     * 头像上传接口（按用户ID+用户名+创建时间生成专属目录）
     * 前端需通过 formData 传递 userId 参数，name 参数为 "file"
     */
    @PostMapping("/avatar")
    public String uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId, // 接收前端传递的用户ID
            HttpServletRequest request) {

        // 1. 验证文件是否为空
        if (file.isEmpty()) {
            return ResultUtil.error(400, "上传的头像文件不能为空");
        }

        // 2. 验证用户ID有效性并查询用户信息（直接用DAO查询，修复报错）
        if (userId == null) {
            return ResultUtil.error(400, "用户ID不能为空");
        }
        // 直接调用DAO查询用户，无需通过Servlet
        User user = userDao.getUserById(userId);
        if (user == null) {
            return ResultUtil.error(404, "用户不存在");
        }

        try {
            // 3. 验证文件类型和大小
            String originalFilename = file.getOriginalFilename();
            if (!isValidImageFile(originalFilename)) {
                return ResultUtil.error(400, "只支持 JPG、PNG、WEBP 格式的图片");
            }
            long fileSize = file.getSize();
            if (fileSize > 5 * 1024 * 1024) { // 限制 5MB 以内
                return ResultUtil.error(400, "图片大小不能超过 5MB");
            }

            // 4. 构建用户专属目录（核心：ID+用户名+创建时间）
            // 4.1 处理用户名（过滤特殊字符，避免路径错误）
            String username = filterSpecialChars(user.getUserName());
            // 4.2 格式化用户创建时间（yyyyMMddHHmmss，确保唯一性）
            // 注意：需确保User实体类的createTime字段不为null（注册时要初始化）
            if (user.getCreateTime() == null) {
                return ResultUtil.error(500, "用户创建时间不存在，无法生成目录");
            }
            String createTimeStr = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(user.getCreateTime());
            // 4.3 拼接用户专属目录（格式：ID_用户名_创建时间）
            String userDir = userId + "_" + username + "_" + createTimeStr;
            // 4.4 完整存储路径（基础路径/用户专属目录/avatar/）
            String fullDirPath = avatarBasePath + File.separator + userDir + File.separator + "avatar";
            File uploadDir = new File(fullDirPath);

            // 5. 确保目录存在（递归创建：基础路径→用户目录→avatar目录）
            if (!uploadDir.exists()) {
                boolean isCreated = uploadDir.mkdirs();
                if (!isCreated) {
                    return ResultUtil.error(500, "用户专属目录创建失败");
                }
            }

            // 6. 生成唯一文件名（避免重名覆盖）
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExt;
            File destFile = new File(uploadDir, fileName);

            // 7. 保存文件到服务器
            file.transferTo(destFile);

            // 8. 构建访问 URL（格式：前缀/用户目录/avatar/文件名）
            String avatarUrl = avatarUrlPrefix + "/" + userDir + "/avatar/" + fileName;

            // 9. 返回成功结果
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            System.out.println("头像上传成功 - 存储路径：" + destFile.getAbsolutePath() + "，访问URL：" + avatarUrl);
            return ResultUtil.success(data);

        } catch (IOException e) {
            // 处理文件保存异常
            e.printStackTrace();
            return ResultUtil.error(500, "文件上传失败，请重试");
        } catch (Exception e) {
            // 处理其他异常（如时间格式化异常）
            e.printStackTrace();
            return ResultUtil.error(500, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 过滤用户名中的特殊字符（避免路径包含 / \ : * ? " < > | 等非法字符）
     */
    private String filterSpecialChars(String username) {
        if (username == null || username.isEmpty()) {
            return "unknown"; // 用户名为空时默认值
        }
        // 保留字母、数字、下划线，其他字符替换为下划线
        return username.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * 验证是否为合法的图片文件
     */
    private boolean isValidImageFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg")
                || lowerFilename.endsWith(".jpeg")
                || lowerFilename.endsWith(".png")
                || lowerFilename.endsWith(".webp");
    }
}