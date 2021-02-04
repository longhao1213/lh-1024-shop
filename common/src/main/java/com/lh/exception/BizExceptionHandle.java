package com.lh.exception;

import com.lh.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: BizExceptionHandle.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/04 15:45
 */
@ControllerAdvice
@Slf4j
public class BizExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonData Handle(Exception e) {
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            log.info("[业务异常]{}", e);
            return JsonData.buildError(bizException.getMsg(), bizException.getCode());
        } else {
            log.info("[系统异常]{}", e);
            return JsonData.buildError("系统异常，未知错误");
        }
    }
}