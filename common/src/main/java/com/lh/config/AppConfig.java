package com.lh.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: AppConfig.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/04/06 16:05
 */
@Configuration
@Data
public class AppConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPwd;

    @Bean
    RedissonClient redisson(){
        Config config = new Config();
        config.useSingleServer()
                .setPassword(redisPwd)
                .setAddress("http://" + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }

}