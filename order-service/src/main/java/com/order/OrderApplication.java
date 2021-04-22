package com.order;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: OrderApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:02
 */
@SpringBootApplication
@MapperScan("com.order.mapper")
@EnableTransactionManagement
@ComponentScan({"com.lh.**","com.order.**"})
@EnableFeignClients
@EnableDiscoveryClient
public class OrderApplication {
    private final static Logger logger = LoggerFactory.getLogger(OrderApplication.class);

    public static void main(String[] args){
            SpringApplication.run(OrderApplication.class, args);
        }
}