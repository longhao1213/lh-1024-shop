package com.product.controller;


import com.lh.utils.JsonData;
import com.product.service.ProductService;
import com.product.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 龙三
 * @since 2021-04-09
 */
@RestController
@Api("商品")
@RequestMapping("/api/product/v1")
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("分页查询商品列表")
    @GetMapping("page")
    public JsonData pageProductList(
            @ApiParam(value = "当前页")  @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size
    ){
        Map<String,Object> pageResult = productService.page(page,size);
        return JsonData.buildSuccess(pageResult);
    }

    @ApiOperation("商品详情")
    @GetMapping("/detail/{product_id}")
    public JsonData detail(@ApiParam(value = "商品id",required = true) @PathVariable("product_id") long productId){
        ProductVO productVO = productService.findDetailById(productId);
        return JsonData.buildSuccess(productVO);
    }

}

