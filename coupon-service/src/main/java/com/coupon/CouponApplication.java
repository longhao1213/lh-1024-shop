package com.coupon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: CouponApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:00
 */
@SpringBootApplication
public class CouponApplication {
    private final static Logger logger = LoggerFactory.getLogger(CouponApplication.class);

    public static void main(String[] args){
            SpringApplication.run(CouponApplication.class, args);
        }
}