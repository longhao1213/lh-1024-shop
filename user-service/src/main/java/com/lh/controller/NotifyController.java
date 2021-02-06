package com.lh.controller;

import com.google.code.kaptcha.Producer;
import com.lh.utils.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: NotifyController.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/05 16:23
 */
@RestController
@Api(tags = "验证码模块")
@RequestMapping("/api/notify/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    // redis中验证码过期时间
    private static final long CAPTCHA_CODE_EXPIRED = 60 * 1000 * 10;

    @ApiOperation("获取图形验证码")
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String text = captchaProducer.createText();
        log.info("图形验证码:{}", text);
        String cacheKey = getCaptchaKey(request);
        redisTemplate.opsForValue().set(cacheKey, text, CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);
        BufferedImage image = captchaProducer.createImage(text);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
        } catch (Exception e) {
            log.error("图形验证码生成异常");
        }
    }

    /**
     * 获取验证码key
     * @param request
     * @return
     */
    public String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String agent = request.getHeader("User-Agent");
        String key = "user-service:captcha:" + CommonUtil.MD5(ip + agent);
        log.info("ip:{}", ip);
        log.info("agent:{}", agent);
        log.info("key:{}", key);
        return key;
    }
}