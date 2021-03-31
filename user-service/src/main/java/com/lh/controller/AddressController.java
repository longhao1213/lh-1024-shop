package com.lh.controller;


import com.lh.enums.BizCodeEnum;
import com.lh.exception.BizException;
import com.lh.model.AddressDO;
import com.lh.request.AddressAddRequest;
import com.lh.service.AddressService;
import com.lh.utils.JsonData;
import com.lh.vo.AddressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        AddressVO addressVO = addressService.detail(addressId);
        return JsonData.buildSuccess(addressVO);
    }

    @ApiOperation("添加收货地址")
    @PostMapping("/add")
    public Object add(@RequestBody AddressAddRequest addressAddRequest) {
        int row = addressService.add(addressAddRequest);
        return JsonData.buildSuccess(row);
    }

    /**
     * 删除指定收货地址
     * @param addressId
     * @return
     */
    @ApiOperation("删除指定收货地址")
    @DeleteMapping("/del/{address_id}")
    public JsonData del(
            @ApiParam(value = "地址id",required = true)
            @PathVariable("address_id")int addressId){

        int rows = addressService.del(addressId);

        return rows == 1 ? JsonData.buildSuccess(): JsonData.buildResult(BizCodeEnum.ADDRESS_DEL_FAIL);
    }


    /**
     * 查询用户的全部收货地址
     * @return
     */
    @ApiOperation("查询用户的全部收货地址")
    @GetMapping("/list")
    public JsonData findUserAllAddress(){

        List<AddressVO> list = addressService.listUserAllAddress();

        return JsonData.buildSuccess(list);
    }

}

