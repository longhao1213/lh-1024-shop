package com.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: GatewayApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:01
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args){
            SpringApplication.run(GatewayApplication.class, args);
        }
}