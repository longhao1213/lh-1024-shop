package com.order.component;

import com.lh.enums.ProductOrderPayTypeEnum;
import com.order.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Component
@Slf4j
public class PayFactory {


    @Autowired
    private AlipayStrategy alipayStrategy;

    @Autowired
    private WechatPayStrategy wechatPayStrategy;


    /**
     * 创建支付，简单工程模式
     * @param payInfoVO
     * @return
     */
    public String pay(PayInfoVO payInfoVO){

        String payType = payInfoVO.getPayType();

        if(ProductOrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)){
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);

            return payStrategyContext.executeUnifiedorder(payInfoVO);

        } else if(ProductOrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)){
            //微信支付 暂未实现
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeUnifiedorder(payInfoVO);
        }


        return "";
    }


    /**
     * 查询订单支付状态
     *
     * 支付成功返回非空，其他返回空
     *
     * @param payInfoVO
     * @return
     */
    public String queryPaySuccess(PayInfoVO payInfoVO){
        String payType = payInfoVO.getPayType();

        if(ProductOrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)){
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);

            return payStrategyContext.executeQueryPaySuccess(payInfoVO);

        } else if(ProductOrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)){
            //微信支付 暂未实现
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeQueryPaySuccess(payInfoVO);
        }


        return "";


    }




}
