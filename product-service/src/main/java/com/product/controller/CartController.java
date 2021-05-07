package com.product.controller;

import com.lh.utils.JsonData;
import com.product.request.CartItemRequest;
import com.product.service.CartService;
import com.product.vo.CartItemVO;
import com.product.vo.CartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Api("购物车")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {


    @Autowired
    private CartService cartService;


    @ApiOperation("添加到购物车")
    @PostMapping("add")
    public JsonData addToCart(@ApiParam("购物项") @RequestBody  CartItemRequest cartItemRequest){

        cartService.addToCart(cartItemRequest);

        return JsonData.buildSuccess();
    }



    @ApiOperation("修改购物车数量")
    @PostMapping("change")
    public JsonData changeItemNum( @ApiParam("购物项") @RequestBody CartItemRequest cartItemRequest){

        cartService.changeItemNum(cartItemRequest);

        return JsonData.buildSuccess();
    }




    @ApiOperation("清空购物车")
    @DeleteMapping("/clear")
    public JsonData cleanMyCart(){

        cartService.clear();
        return JsonData.buildSuccess();
    }



    @ApiOperation("查看我的购物车")
    @GetMapping("/mycart")
    public JsonData findMyCart(){

        CartVO cartVO = cartService.getMyCart();

        return JsonData.buildSuccess(cartVO);
    }



    @ApiOperation("删除购物项")
    @DeleteMapping("/delete/{product_id}")
    public JsonData deleteItem( @ApiParam(value = "商品id",required = true)@PathVariable("product_id")long productId ){

        cartService.deleteItem(productId);
        return JsonData.buildSuccess();
    }

    /**
     * 用于订单服务，确认订单，获取对应的商品项详情信息
     *
     * 会清空购物车的商品数据
     * @param productIdList
     * @return
     */
    @ApiOperation("获取对应订单的商品信息")
    @PostMapping("confirm_order_cart_items")
    public JsonData confirmOrderCartItems(@ApiParam("商品id列表") @RequestBody List<Long> productIdList){

        List<CartItemVO> cartItemVOList = cartService.confirmOrderCartItems(productIdList);

        return JsonData.buildSuccess(cartItemVOList);

    }








}
