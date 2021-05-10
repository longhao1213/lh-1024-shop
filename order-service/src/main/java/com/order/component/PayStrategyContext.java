package com.order.component;


import com.order.vo.PayInfoVO;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

public class PayStrategyContext {

    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy){

        this.payStrategy = payStrategy;
    }


    /**
     * 根据支付策略，调用不同的支付
     * @param payInfoVO
     * @return
     */
    public String executeUnifiedorder(PayInfoVO payInfoVO){
        return this.payStrategy.unifiedorder(payInfoVO);
    }


    /**
     * 根据支付的策略，调用不同的查询订单支持状态
     * @param payInfoVO
     * @return
     */
    public String executeQueryPaySuccess(PayInfoVO payInfoVO){
        return this.payStrategy.queryPaySuccess(payInfoVO);

    }


}
