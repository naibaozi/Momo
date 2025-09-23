/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 14:50:36
 * @FilePath: src/main/java/com/naibaozi/momoxxt/MomoXxtApplication.java
 * @Description: 入口函数
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan// 扫描servlet
public class MomoXxtApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomoXxtApplication.class, args);
        System.out.println("MomoXxtApplication started");
        System.out.println("http://localhost:80/momoadmin/index.vue");

    }
    // 打包 spring boot项目
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }


}
