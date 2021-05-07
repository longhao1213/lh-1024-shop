package com.lh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: UserApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:04
 */
@SpringBootApplication
@MapperScan("com.lh.mapper")
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
public class UserApplication {

    public static void main(String[] args){
            SpringApplication.run(UserApplication.class, args);
        }
}