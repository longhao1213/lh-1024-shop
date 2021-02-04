package com.lh.controller;


import com.lh.exception.BizException;
import com.lh.model.AddressDO;
import com.lh.service.AddressService;
import com.lh.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 电商-公司收发货地址表 前端控制器
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
@RestController
@RequestMapping("/api/address/v1")
@Api(tags = "收货地址模块")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @ApiOperation("根据id查找收获地址详情")
    @GetMapping("/find/{address_id}")
    public Object detail(@ApiParam(value = "地址id", required = true)
                         @PathVariable("address_id") long addressId) {
//        int i = 1/0;
        if (addressId == 1) {
            throw new BizException(-1, "测试自定义异常");
        }
        AddressDO addressDO = addressService.detail(addressId);
        return JsonData.buildSuccess(addressDO);
    }
}

