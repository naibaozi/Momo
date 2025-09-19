/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 17:39:51
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-19 19:07:25
 * @FilePath: src/main/java/com/naibaozi/momoxxt/util/CryptoUtil.java
 * @Description: 
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

@Component
public class CryptoUtil {
    // 静态代码块：注册BouncyCastle加密提供者（解决PKCS7Padding不支持问题）
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 必须与前端一致（密钥16位，偏移量16位）
    private static final String AES_KEY = "nbz_1234567890ab";
    private static final String AES_IV = "mxx_0987654321cd";
    // 明确指定使用BouncyCastle提供者
    private static final String AES_ALGORITHM = "AES/CBC/PKCS7Padding";
    private static final String PROVIDER = "BC"; // BouncyCastle的提供者名称

    // BCrypt 加密器（单例）
    private static final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

    /**
     * AES 解密（前端加密的密码→明文）
     */
    public String aesDecrypt(String ciphertext) {
        try {
            // 1. Base64字符串→字节数组
            byte[] encryptedData = Base64.getDecoder().decode(ciphertext);
            // 2. 密钥和偏移量→字节数组
            byte[] keyBytes = AES_KEY.getBytes(StandardCharsets.UTF_8);
            byte[] ivBytes = AES_IV.getBytes(StandardCharsets.UTF_8);
            // 3. 初始化加密算法（指定BouncyCastle提供者）
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            // 关键修复：指定使用BC提供者
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM, PROVIDER);
            // 4. 解密模式初始化
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            // 5. 解密→明文
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("密码解密失败");
        }
    }

    /**
     * BCrypt 加密（明文→不可逆哈希值，用于存储）
     */
    public String bcryptEncrypt(String plaintext) {
        return bCryptEncoder.encode(plaintext);
    }

    /**
     * BCrypt 密码校验（明文→与数据库哈希值比对）
     */
    public boolean bcryptMatches(String plaintext, String encodedPassword) {
        return bCryptEncoder.matches(plaintext, encodedPassword);
    }
}
    