package com.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: OrderApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:02
 */
@SpringBootApplication
public class OrderApplication {
    private final static Logger logger = LoggerFactory.getLogger(OrderApplication.class);

    public static void main(String[] args){
            SpringApplication.run(OrderApplication.class, args);
        }
}