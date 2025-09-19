/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 20:31:58
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-19 20:32:03
 * @FilePath: src/main/java/com/naibaozi/momoxxt/util/JwtUtil.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类：生成、验证、解析 Token
 */
public class JwtUtil {

    // 1. 核心配置（生产环境建议放到配置文件中）
    // 密钥：至少 256 位（32 个字符），确保前后端/服务端一致
    private static final String SECRET_KEY = "naibaozi-momoxxt-2025-secret-key-1234";
    // Token 过期时间：2 小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000;

    // 生成签名密钥（基于 HMAC-SHA256 算法）
    private static Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token（登录成功后调用）
     * @param userId 用户ID（Token 中存储的核心标识）
     * @param username 用户名（可选，用于快速识别）
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username) {
        // 当前时间 + 过期时间 = Token 失效时间
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                // 1. 头部：指定算法
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                // 2. 载荷：存储自定义数据（避免敏感信息，如密码）
                .claim("userId", userId)       // 用户ID（必须，用于后续查询用户）
                .claim("username", username)   // 用户名（可选）
                // 3. 过期时间
                .setExpiration(expirationDate)
                // 4. 签名：使用密钥和算法生成签名，防止 Token 被篡改
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                // 生成 Token 字符串
                .compact();
    }

    /**
     * 验证 Token 有效性（拦截器中调用）
     * @param token 前端传递的 Token
     * @return 有效返回 true；无效（过期、篡改、格式错误）返回 false
     */
    public static boolean validateToken(String token) {
        try {
            // 解析 Token 并验证签名和过期时间
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 捕获所有 Token 相关异常，均视为无效
            return false;
        }
    }

    /**
     * 从 Token 中解析用户ID（验证通过后调用）
     * @param token 有效 Token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        // 解析 Token 中的载荷（Claims）
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        // 返回载荷中的 userId（注意类型转换）
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中解析用户名（可选）
     * @param token 有效 Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }
}