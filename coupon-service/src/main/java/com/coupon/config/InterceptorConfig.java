package com.coupon.config;

import com.lh.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: InterceptorConfig.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/03/24 11:25
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                //拦截的路径
                .addPathPatterns("/api/coupon/*/**", "/api/coupon_record/*/**")
                //排查不拦截的路径
                .excludePathPatterns("/api/coupon/*/page_coupon","/api/coupon/*/new_user_coupon");
    }

}