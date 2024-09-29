package com.team8504.csgo2web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
/**
 * Author:  J.C.ZONG
 * Package: com.team8504.csgo2web.controller
 * Project: CSGO2WEB
 * Date: 2024/09/26/上午9:08
 * Version 0.0
 */
@SpringBootApplication
@ServletComponentScan// 扫描servlet
public class Csgo2webApplication {

    public static void main(String[] args) {
        SpringApplication.run(Csgo2webApplication.class, args);
//        //demo
//        System.out.println("http://localhost:8080/demo");
//        //userinfo启动层
//        System.out.println("http://localhost:8080/userinfo");
//        //teaching启动层
//        System.out.println("http://localhost:8080/teaching");
//        //news启动层
//        System.out.println("http://localhost:8080/news");
//        //maps启动层
//        System.out.println("http://localhost:8080/maps");
//        //background启动层
//        System.out.println("http://localhost:8080/background");
//        System.out.println("http://localhost:8080/login.html");
//        System.out.println("http://localhost:8080/register.html");
        System.out.println("http://localhost:8080/index.html");
        System.out.println("http://localhost:8080/classtab?op=query");
        System.out.println("http://localhost:8080/news?op=query&cId=1");
        System.out.println("http://localhost:8080/maps?op=query&cId=1");
        System.out.println("http://localhost:8080/background?op=query&cId=1");
        System.out.println("http://localhost:8080/teaching?op=query&cId=1");
    }

}
