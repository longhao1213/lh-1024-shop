package com.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.enums.*;
import com.lh.exception.BizException;
import com.lh.interceptor.LoginInterceptor;
import com.lh.model.LoginUser;
import com.lh.model.OrderMessage;
import com.lh.utils.CommonUtil;
import com.lh.utils.JsonData;
import com.order.config.RabbitMQConfig;
import com.order.feign.CouponFeignSerivce;
import com.order.feign.ProductFeignService;
import com.order.feign.UserFeignService;
import com.order.mapper.ProductOrderItemMapper;
import com.order.model.ProductOrderDO;
import com.order.mapper.ProductOrderMapper;
import com.order.model.ProductOrderItemDO;
import com.order.request.ConfirmOrderRequest;
import com.order.request.LockCouponRecordRequest;
import com.order.request.LockProductRequest;
import com.order.request.OrderItemRequest;
import com.order.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.vo.CouponRecordVO;
import com.order.vo.OrderItemVO;
import com.order.vo.ProductOrderAddressVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 龙三
 * @since 2021-04-21
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private CouponFeignSerivce couponFeignSerivce;


    @Autowired
    private ProductOrderItemMapper orderItemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    /**
     * * 防重提交
     * * 用户微服务-确认收货地址
     * * 商品微服务-获取最新购物项和价格
     * * 订单验价
     *   * 优惠券微服务-获取优惠券
     *   * 验证价格
     * * 锁定优惠券
     * * 锁定商品库存
     * * 创建订单对象
     * * 创建子订单对象
     * * 发送延迟消息-用于自动关单
     * * 创建支付信息-对接三方支付
     *
     * @param orderRequest
     * @return
     */
    @Override
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);


        //获取收货地址详情
        ProductOrderAddressVO addressVO = this.getUserAddress(orderRequest.getAddressId());

        log.info("收货地址信息:{}",addressVO);

        //获取用户加入购物车的商品
        List<Long> productIdList = orderRequest.getProductIdList();

        JsonData cartItemDate = productFeignService.confirmOrderCartItem(productIdList);
        List<OrderItemVO> orderItemList  = cartItemDate.getData(new TypeReference<>(){});
        log.info("获取的商品:{}",orderItemList);
        if(orderItemList == null){
            //购物车商品不存在
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }

        //验证价格，减去商品优惠券
        this.checkPrice(orderItemList,orderRequest);

        //锁定优惠券
        this.lockCouponRecords(orderRequest ,orderOutTradeNo );

        //锁定库存
        this.lockProductStocks(orderItemList,orderOutTradeNo);


        //创建订单
        ProductOrderDO productOrderDO = this.saveProductOrder(orderRequest,loginUser,orderOutTradeNo,addressVO);

        //创建订单项
        this.saveProductOrderItems(orderOutTradeNo,productOrderDO.getId(),orderItemList);

        //发送延迟消息，用于自动关单
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOutTradeNo(orderOutTradeNo);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getOrderCloseDelayRoutingKey(),orderMessage);


        //创建支付  TODO


        return null;
    }

    /**
     * 新增订单项
     * @param orderOutTradeNo
     * @param id
     * @param orderItemList
     */
    private void saveProductOrderItems(String orderOutTradeNo, Long orderId, List<OrderItemVO> orderItemList) {


        List<ProductOrderItemDO> list = orderItemList.stream().map(
                obj->{
                    ProductOrderItemDO itemDO = new ProductOrderItemDO();
                    itemDO.setBuyNum(obj.getBuyNum());
                    itemDO.setProductId(obj.getProductId());
                    itemDO.setProductImg(obj.getProductImg());
                    itemDO.setProductName(obj.getProductTitle());

                    itemDO.setOutTradeNo(orderOutTradeNo);
                    itemDO.setCreateTime(new Date());

                    //单价
                    itemDO.setAmount(obj.getAmount());
                    //总价
                    itemDO.setTotalAmount(obj.getTotalAmount());
                    itemDO.setProductOrderId(orderId);
                    return itemDO;
                }
        ).collect(Collectors.toList());


        orderItemMapper.insertBatch(list);


    }

    /**
     * 创建订单
     * @param orderRequest
     * @param loginUser
     * @param orderOutTradeNo
     * @param addressVO
     */
    private ProductOrderDO saveProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductOrderAddressVO addressVO) {

        ProductOrderDO productOrderDO = new ProductOrderDO();
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setHeadImg(loginUser.getHeadImg());
        productOrderDO.setNickname(loginUser.getName());

        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);
        productOrderDO.setOrderType(ProductOrderTypeEnum.DAILY.name());

        //实际支付的价格
        productOrderDO.setPayAmount(orderRequest.getRealPayAmount());

        //总价，未使用优惠券的价格
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        ProductOrderTypeEnum.valueOf(orderRequest.getPayType()).name();
        productOrderDO.setPayType(ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType()).name());

        productOrderDO.setReceiverAddress(JSON.toJSONString(addressVO));

        productOrderMapper.insert(productOrderDO);

        return productOrderDO;

    }

    /**
     * 锁定商品库存
     * @param orderItemList
     * @param orderOutTradeNo
     */
    private void lockProductStocks(List<OrderItemVO> orderItemList, String orderOutTradeNo) {

        List<OrderItemRequest> itemRequestList = orderItemList.stream().map(obj->{

            OrderItemRequest request = new OrderItemRequest();
            request.setBuyNum(obj.getBuyNum());
            request.setProductId(obj.getProductId());
            return request;
        }).collect(Collectors.toList());


        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(orderOutTradeNo);
        lockProductRequest.setOrderItemList(itemRequestList);

        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if(jsonData.getCode()!=0){
            log.error("锁定商品库存失败：{}",lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }
    }

    /**
     * 锁定优惠券
     * @param orderRequest
     * @param orderOutTradeNo
     */
    private void lockCouponRecords(ConfirmOrderRequest orderRequest, String orderOutTradeNo) {
        List<Long> lockCouponRecordIds = new ArrayList<>();
        if(orderRequest.getCouponRecordId()>0){
            lockCouponRecordIds.add(orderRequest.getCouponRecordId());

            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setOrderOutTradeNo(orderOutTradeNo);
            lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);

            //发起锁定优惠券请求
            JsonData jsonData = couponFeignSerivce.lockCouponRecords(lockCouponRecordRequest);
            if(jsonData.getCode()!=0){
                throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
            }
        }

    }

    /**
     * 验证价格
     * 1）统计全部商品的价格
     * 2) 获取优惠券(判断是否满足优惠券的条件)，总价再减去优惠券的价格 就是 最终的价格
     *
     * @param orderItemList
     * @param orderRequest
     */
    private void checkPrice(List<OrderItemVO> orderItemList, ConfirmOrderRequest orderRequest) {

        //统计商品总价格
        BigDecimal realPayAmount = new BigDecimal("0");
        if (orderItemList != null) {
            for(OrderItemVO orderItemVO : orderItemList){
                BigDecimal itemRealPayAmount = orderItemVO.getTotalAmount();
                realPayAmount = realPayAmount.add(itemRealPayAmount);
            }
        }

        //获取优惠券，判断是否可以使用
        CouponRecordVO couponRecordVO = getCartCouponRecord(orderRequest.getCouponRecordId());

        //计算购物车价格，是否满足优惠券满减条件
        if(couponRecordVO!=null){

            //计算是否满足满减
            if(realPayAmount.compareTo(couponRecordVO.getConditionPrice()) < 0){
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
            }
            if(couponRecordVO.getPrice().compareTo(realPayAmount)>0){
                realPayAmount = BigDecimal.ZERO;

            }else {
                realPayAmount = realPayAmount.subtract(couponRecordVO.getPrice());
            }

        }

        if(realPayAmount.compareTo(orderRequest.getRealPayAmount()) !=0 ){
            log.error("订单验价失败：{}",orderRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }
    }

    /**
     * 获取优惠券
     * @param couponRecordId
     * @return
     */
    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {

        if(couponRecordId ==null || couponRecordId < 0){
            return null;
        }

        JsonData couponData = couponFeignSerivce.findUserCouponRecordById(couponRecordId);

        if(couponData.getCode()!=0){
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }

        if(couponData.getCode()==0){

            CouponRecordVO couponRecordVO = couponData.getData(new TypeReference<>(){});

            if(!couponAvailable(couponRecordVO)){
                log.error("优惠券使用失败");
                throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
            }
            return couponRecordVO;
        }

        return null;
    }

    /**
     * 判断优惠券是否可用
     * @param couponRecordVO
     * @return
     */
    private boolean couponAvailable(CouponRecordVO couponRecordVO) {

        if(couponRecordVO.getUseState().equalsIgnoreCase(CouponStateEnum.NEW.name())){
            long currentTimestamp = CommonUtil.getCurrentTimestamp();
            long end = couponRecordVO.getEndTime().getTime();
            long start = couponRecordVO.getStartTime().getTime();
            if(currentTimestamp>= start && currentTimestamp<=end){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取收货地址详情
     * @param addressId
     * @return
     */
    private ProductOrderAddressVO getUserAddress(long addressId) {

        JsonData addressData = userFeignService.detail(addressId);

        if(addressData.getCode() !=0){
            log.error("获取收获地址失败,msg:{}",addressData);
            throw new BizException(BizCodeEnum.ADDRESS_NO_EXITS);
        }

        ProductOrderAddressVO addressVO = addressData.getData(new TypeReference<>(){});

        return addressVO;
    }

    /**
     * 查询订单状态
     * @param outTradeNo
     * @return
     */
    @Override
    public String queryProductOrderState(String outTradeNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no",outTradeNo));

        if(productOrderDO == null){
            return "";
        }else {
            return productOrderDO.getState();
        }

    }

    /**
     * 定时关单
     * @param orderMessage
     * @return
     */
    @Override
    public boolean closeProductOrder(OrderMessage orderMessage) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no",orderMessage.getOutTradeNo()));

        if(productOrderDO == null){
            //订单不存在
            log.warn("直接确认消息，订单不存在:{}",orderMessage);
            return true;
        }

        if(productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())){
            //已经支付
            log.info("直接确认消息,订单已经支付:{}",orderMessage);
            return true;
        }

        //向第三方支付查询订单是否真的未支付  TODO




        String payResult = "";

        //结果为空，则未支付成功，本地取消订单
        if(StringUtils.isBlank(payResult)){
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(),ProductOrderStateEnum.CANCEL.name(),ProductOrderStateEnum.NEW.name());
            log.info("结果为空，则未支付成功，本地取消订单:{}",orderMessage);
            return true;
        }else {
            //支付成功，主动的把订单状态改成UI就支付，造成该原因的情况可能是支付通道回调有问题
            log.warn("支付成功，主动的把订单状态改成UI就支付，造成该原因的情况可能是支付通道回调有问题:{}",orderMessage);
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(),ProductOrderStateEnum.PAY.name(),ProductOrderStateEnum.NEW.name());
            return true;
        }


    }
}
