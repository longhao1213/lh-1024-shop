package com.lh.service.impl;

import com.lh.constant.CaCheKey;
import com.lh.enums.BizCodeEnum;
import com.lh.enums.SendCodeEnum;
import com.lh.component.MailService;
import com.lh.service.NotifyService;
import com.lh.utils.CheckUtil;
import com.lh.utils.CommonUtil;
import com.lh.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: NotifyServiceImpl.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/20 14:01
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String SUBJECT = "Lh商城验证码";

    public static final String CONTENT = "您的验证码是%s，有效期60秒，打死都不要告诉别人";

    private static final int CACHE_VALUE_EXPIRE = 60 * 1000 * 10;

    /**
     * 前置：判断是否重复发送
     * 1：存储验证码到缓存
     * 2：发送验证码到邮箱
     * 后置：存储发送记录
     * @param sendCodeEnum
     * @param to
     * @return
     */
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {

        // 前置判断
        String cacheKey = String.format(CaCheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = (String) redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheValue)) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            if (CommonUtil.getCurrentTimestamp() - ttl < 60 * 1000) {
                log.info("重复发送验证码，间隔时间{}秒", (CommonUtil.getCurrentTimestamp() - ttl) / 1000);
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }
        String randomCode = CommonUtil.getRandomCode(4);
        cacheValue = randomCode + "_" + CommonUtil.getCurrentTimestamp();
        // 存储验证码到缓存
        redisTemplate.opsForValue().set(cacheKey, cacheValue, CACHE_VALUE_EXPIRE, TimeUnit.MILLISECONDS);

        if (CheckUtil.isEmail(to)) {
            // 邮箱验证码
            // 发送验证码
            mailService.sendSimpleMail(to, SUBJECT, String.format(CONTENT, randomCode));
            return JsonData.buildSuccess();
        } else if (CheckUtil.isPhone(to)){
            // 手机验证码
        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

    /**
     * 校验邮箱验证码
     * @param sendCodeEnum
     * @param to
     * @param code
     * @return
     */
    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String cacheKey = String.format(CaCheKey.CHECK_CODE_KEY,sendCodeEnum.name(),to);

        String cacheValue = (String) redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isNotBlank(cacheValue)){
            String cacheCode = cacheValue.split("_")[0];
            if(cacheCode.equals(code)){
                //删除验证码
                redisTemplate.delete(cacheKey);
                return true;
            }

        }
        return false;
    }
}