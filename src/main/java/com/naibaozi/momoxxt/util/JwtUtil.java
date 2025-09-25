/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 20:31:58
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-25 08:18:37
 * @FilePath: src/main/java/com/naibaozi/momoxxt/util/JwtUtil.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; // 1. 添加 @Component 注解

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类：生成、验证、解析 Token
 */
@Component // 告诉 Spring 这是一个需要管理的组件
public class JwtUtil {

    // 2. 通过 @Value 从配置文件注入，而不是硬编码
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // 生成签名密钥
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) { // 3. 移除 static 关键字
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .claim("userId", userId)
                .claim("username", username)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证 Token 有效性
     */
    public boolean validateToken(String token) { // 移除 static 关键字
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中解析用户ID
     */
    public Long getUserIdFromToken(String token) { // 移除 static 关键字
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中解析用户名
     */
    public String getUsernameFromToken(String token) { // 移除 static 关键字
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }
}