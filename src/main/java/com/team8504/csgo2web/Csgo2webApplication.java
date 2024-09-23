package com.team8504.csgo2web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Csgo2webApplication {

    public static void main(String[] args) {
        SpringApplication.run(Csgo2webApplication.class, args);
        System.out.println("http://localhost:8080/index.html");

    }

}
