package com.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: CouponApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:00
 */
@SpringBootApplication
@MapperScan("com.coupon.mapper")
@EnableTransactionManagement
@ComponentScan({"com.lh.**","com.coupon.**"})
@EnableFeignClients
@EnableDiscoveryClient
public class CouponApplication {

    public static void main(String[] args){
            SpringApplication.run(CouponApplication.class, args);
        }
}