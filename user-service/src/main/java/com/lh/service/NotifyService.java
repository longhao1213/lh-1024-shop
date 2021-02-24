package com.lh.service;

import com.lh.enums.SendCodeEnum;
import com.lh.utils.JsonData;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: NotifyService.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/20 14:00
 */
public interface NotifyService {

    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);

    boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code);
}