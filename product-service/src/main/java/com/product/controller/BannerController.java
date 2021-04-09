package com.product.controller;


import com.lh.utils.JsonData;
import com.product.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 龙三
 * @since 2021-04-09
 */
@RestController
@Api("轮播图模块")
@RequestMapping("/api/banner/v1")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    @ApiOperation("轮播图列表接口")
    @GetMapping("list")
    public JsonData list() {
        return JsonData.buildSuccess(bannerService.list());
    }
}

