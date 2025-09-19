package com.naibaozi.momoxxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan// 扫描servlet
public class MomoXxtApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomoXxtApplication.class, args);
        System.out.println("Csgo2webApplication started");

    }

}
