package com.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: ProdcutApplication.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/01/28 16:03
 */
@SpringBootApplication
@MapperScan("com.product.mapper")
@EnableTransactionManagement
@ComponentScan({"com.lh.**","com.product.**"})
public class ProductApplication {

    public static void main(String[] args){
            SpringApplication.run(ProductApplication.class, args);
        }
}