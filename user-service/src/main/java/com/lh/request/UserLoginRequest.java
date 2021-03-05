package com.lh.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: UserLoginRequest.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/26 10:02
 */
@Data
@ApiModel("用户登录")
public class UserLoginRequest {

    @ApiModelProperty(value = "邮箱",example = "123")
    private String mail;

    @ApiModelProperty(value = "密码",example = "123")
    private String pwd;
}