package com.order.service;

import com.lh.enums.ProductOrderPayTypeEnum;
import com.lh.model.OrderMessage;
import com.lh.utils.JsonData;
import com.order.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.order.request.ConfirmOrderRequest;
import com.order.request.RepayOrderRequest;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 龙三
 * @since 2021-04-21
 */
public interface ProductOrderService {

    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

    String queryProductOrderState(String outTradeNo);

    boolean closeProductOrder(OrderMessage orderMessage);

    JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum alipay, Map<String, String> paramsMap);

    Map<String, Object> page(int page, int size, String state);

    JsonData repay(RepayOrderRequest repayOrderRequest);

}
