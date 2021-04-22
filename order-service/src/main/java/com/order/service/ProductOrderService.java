package com.order.service;

import com.lh.utils.JsonData;
import com.order.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.order.request.ConfirmOrderRequest;

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
}
