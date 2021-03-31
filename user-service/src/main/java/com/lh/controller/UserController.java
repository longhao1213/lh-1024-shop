package com.lh.controller;


import com.lh.enums.BizCodeEnum;
import com.lh.request.UserLoginRequest;
import com.lh.service.FileService;
import com.lh.service.UserService;
import com.lh.utils.JsonData;
import com.lh.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import com.lh.request.UserRegisterRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
@RestController
@RequestMapping("/api/user/v1")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @ApiOperation("用户头像上传")
    @PostMapping("/upload")
    public JsonData uploadHeaderImg(
            @ApiParam(value = "文件上传", readOnly = true) @RequestPart("file") MultipartFile file) {
        String result = fileService.uploadUserHeadImg(file);
        return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

    @ApiOperation("用户注册")
    @PostMapping("/userRegister")
    public JsonData userRegister(@ApiParam(value = "用户注册对象",required = true) @RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

    @ApiOperation("用户登录")
    @PostMapping("/userLogin")
    public JsonData userLogin(@ApiParam("用户登录对象") @RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(userLoginRequest);
    }

    @ApiOperation("查看用户详情")
    @PostMapping("/detail")
    public JsonData detail() {
        UserVo userVo = userService.findUserDetail();
        return JsonData.buildSuccess(userVo);
    }

}

