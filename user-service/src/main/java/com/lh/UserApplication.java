package com.lh;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
public class UserApplication {
    private final static Logger logger = LoggerFactory.getLogger(UserApplication.class);

    public static void main(String[] args){
            SpringApplication.run(UserApplication.class, args);
        }
}