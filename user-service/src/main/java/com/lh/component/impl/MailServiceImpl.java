package com.lh.component.impl;

import com.lh.component.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: MailServiceImpl.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/10 18:22
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        // 创建simpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮件发送人
        message.setFrom(from);
        // 邮件接收人
        message.setTo(to);
        // 邮件主题
        message.setSubject(subject);
        // 邮件内容
        message.setText(content);
        // 发送邮件
        try {
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            log.info("到达邮件发送步骤，因为没有拿到正确的配置，所以这里发送失败");
        }
        log.info("邮件发送成功：{}", message.toString());
    }
}