package com.lh.component;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: MailService.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/10 18:21
 */
public interface MailService {

    void sendSimpleMail(String to, String subject, String content);
}