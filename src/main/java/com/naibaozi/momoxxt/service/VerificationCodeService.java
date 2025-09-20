/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-20 15:33:22
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 21:08:49
 * @FilePath: src/main/java/com/naibaozi/momoxxt/service/VerificationCodeService.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-20 15:33:22
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-20 21:08:49
 * @FilePath: src/main/java/com/naibaozi/momoxxt/service/VerificationCodeService.java
 * @Description: Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved.
 */
package com.naibaozi.momoxxt.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    // 存储验证码：key=邮箱，value=验证码
    private final Map<String, CodeInfo> codeStorage = new HashMap<>();

    // 验证码有效期5分钟
    private static final long EXPIRATION_MINUTES = 5;

    // 内部类存储验证码和过期时间
    private static class CodeInfo {
        String code;
        long expireTime;

        CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }

    /**
     * 存储验证码
     */
    public void storeCode(String email, String code) {
        long expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION_MINUTES);
        codeStorage.put(email, new CodeInfo(code, expireTime));
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String code) {
        CodeInfo codeInfo = codeStorage.get(email);

        // 验证码不存在
        if (codeInfo == null) {
            return false;
        }

        // 验证码已过期
        if (System.currentTimeMillis() > codeInfo.expireTime) {
            codeStorage.remove(email); // 移除过期验证码
            return false;
        }

        // 验证码匹配
        boolean isMatch = codeInfo.code.equals(code);
        if (isMatch) {
            codeStorage.remove(email); // 验证成功后移除验证码，防止重复使用
        }
        return isMatch;
    }

    /**
     * 检查是否可以发送新的验证码（防止频繁发送）
     */
    public boolean canSendNewCode(String email) {
        CodeInfo codeInfo = codeStorage.get(email);
        // 如果不存在或已过期，则可以发送新的
        if (codeInfo == null) {
            return true;
        }

        // 1分钟内不允许重复发送
        long timeSinceLastSend = System.currentTimeMillis() - (codeInfo.expireTime - TimeUnit.MINUTES.toMillis(EXPIRATION_MINUTES));
        return timeSinceLastSend > TimeUnit.MINUTES.toMillis(1);
    }
}
