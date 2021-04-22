package com.coupon.service;

import com.coupon.model.CouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coupon.request.NewUserCouponRequest;
import com.lh.enums.CouponCategoryEnum;
import com.lh.utils.JsonData;
import com.lh.vo.PageDataVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 龙三
 * @since 2021-03-26
 */
public interface CouponService {

    PageDataVo pageCouponActivity(int page, int size);

    JsonData addCoupon(long couponId, CouponCategoryEnum promotion);

    JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest);
}
