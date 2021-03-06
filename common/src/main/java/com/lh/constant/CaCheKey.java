package com.lh.constant;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: CaCheKey.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/20 15:32
 */
public class CaCheKey {
    // 注册验证码 第一个参数是类型，第二个是接收号码
    public static final String CHECK_CODE_KEY = "code:%s:%s";

    /**
     * 购物车 hash 结构，key是用户唯一标识
     */
    public static final String CART_KEY = "cart:%s";

    /**
     * 提交表单的token key
     */
    public static final String SUBMIT_ORDER_TOKEN_KEY = "order:submit:%s";
}