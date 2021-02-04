package com.lh.exception;


import com.lh.enums.BizCodeEnum;
import lombok.Data;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: BizException.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/04 15:41
 */
@Data
public class BizException extends RuntimeException{
    private Integer code;
    private String msg;

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }
}