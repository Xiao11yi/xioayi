package com.olivia.xioayi;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.olivia.xioayi.mapper")
public class XioayiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XioayiApplication.class, args);
    }

}
