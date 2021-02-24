package com.lh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: OSSConfig.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/23 16:20
 */
@ConfigurationProperties(prefix = "aliyun.oss")
@Configuration
@Data
public class OSSConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

}
