package com.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lh.constant.CaCheKey;
import com.lh.enums.BizCodeEnum;
import com.lh.exception.BizException;
import com.lh.interceptor.LoginInterceptor;
import com.lh.model.LoginUser;
import com.product.request.CartItemRequest;
import com.product.service.CartService;
import com.product.service.ProductService;
import com.product.vo.CartItemVO;
import com.product.vo.CartVO;
import com.product.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: CartServiceImpl.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/04/20 15:32
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductService productService;

    @Override
    public void addToCart(CartItemRequest cartItemRequest) {
        long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();

        // 获取购物车
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();

        Object cacheObj = myCartOps.get(productId);
        String result = "";
        if (cacheObj != null) {
            result = (String) cacheObj;
        }

        if (StringUtils.isBlank(result)) {
            // 不存在，则创建一个新的商品
            CartItemVO cartItemVO = new CartItemVO();

            ProductVO productVO = productService.findDetailById(productId);
            if (productVO == null) {
                throw new BizException(BizCodeEnum.CART_FAIL);
            }

            cartItemVO.setAmount(productVO.getAmount());
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setProductImg(productVO.getCoverImg());
            cartItemVO.setProductTitle(productVO.getTitle());
            cartItemVO.setProductId(productVO.getId());
            myCartOps.put(productId, JSONObject.toJSONString(cartItemVO));
        } else {
            // 存在商品，修改数量
            CartItemVO cartItemVO = JSONObject.parseObject(result, CartItemVO.class);
            cartItemVO.setBuyNum(cartItemVO.getBuyNum() + buyNum);
            myCartOps.put(productId, JSONObject.toJSONString(cartItemVO));
        }
    }

    @Override
    public void clear() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);
    }

    @Override
    public CartVO getMyCart() {
        // 获取全部购物项
        List<CartItemVO> cartItemVOList = buildCartItem(false);

        // 封装成cartVO
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);
        return cartVO;
    }

    /**
     * 获取最新的购物项
     * @param latestPrise 是否需要获取最新价格
     * @return
     */
    private List<CartItemVO> buildCartItem(boolean latestPrise) {
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        List<Object> itemList = myCart.values();

        List<CartItemVO> cartItemVOList = new ArrayList<>();

        // 拼接id列表查询最新价格
        List<Long> productIdList = new ArrayList<>();

        for (Object item : itemList) {
            CartItemVO cartItemVO = JSONObject.parseObject((String) item, CartItemVO.class);
            cartItemVOList.add(cartItemVO);
            productIdList.add(cartItemVO.getProductId());
        }

        // 查询最新价格
        if (latestPrise) {
            setProductLatestPrice(cartItemVOList, productIdList);
        }
        return cartItemVOList;

    }

    /**
     * 设置商品最新价格
     * @param cartItemVOList
     * @param productIdList
     */
    private void setProductLatestPrice(List<CartItemVO> cartItemVOList, List<Long> productIdList) {
        // 批量查询
        List<ProductVO> productVOList = productService.findProductsByIdBatch(productIdList);

        // 根据商品id进行分组
        Map<Long, ProductVO> maps = productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        cartItemVOList.stream().forEach(item->{
            ProductVO productVO = maps.get(item.getProductId());
            item.setProductTitle(productVO.getTitle());
            item.setProductImg(productVO.getCoverImg());
            item.setAmount(productVO.getAmount());
        });
    }

    /**
     * 删除购物项
     * @param productId
     */
    @Override
    public void deleteItem(long productId) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        myCartOps.delete(productId);
    }

    /**
     * 修改购物项
     * @param cartItemRequest
     */
    @Override
    public void changeItemNum(CartItemRequest cartItemRequest) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        Object cacheObj = myCartOps.get(cartItemRequest.getProductId());
        if (cacheObj == null) {
            throw new BizException(BizCodeEnum.CART_FAIL);
        }
        CartItemVO cartItemVO = JSONObject.parseObject((String) cacheObj, CartItemVO.class);
        cartItemVO.setBuyNum(cartItemRequest.getBuyNum());
        myCartOps.put(cartItemRequest.getProductId(), JSONObject.toJSONString(cartItemVO));
    }

    /**
     * 购物车 key
     * @return
     */
    private String getCartKey(){
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String cartKey = String.format(CaCheKey.CART_KEY, loginUser.getId());
        return cartKey;
    }

    /**
     * 抽取我的购物车，通用方法
     * @return
     */
    private BoundHashOperations<String, Object, Object> getMyCartOps() {
        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
    }
}