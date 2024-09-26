package com.team8504.csgo2web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan// 扫描servlet
public class Csgo2webApplication {

    public static void main(String[] args) {
        SpringApplication.run(Csgo2webApplication.class, args);
        //demo
        System.out.println("http://localhost:8080/demo");
        //userinfo启动层
        System.out.println("http://localhost:8080/userinfo");
        //teachin启动层
        System.out.println("http://localhost:8080/teaching");
        //news启动层
        System.out.println("http://localhost:8080/news");
        //maps启动层
        System.out.println("http://localhost:8080/maps");
        //background启动层
        System.out.println("http://localhost:8080/background");

        System.out.println("http://localhost:8080/index.html");
        System.out.println("http://localhost:8080/login.html");


    }

}
