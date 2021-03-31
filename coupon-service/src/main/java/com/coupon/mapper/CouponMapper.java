package com.coupon.mapper;

import com.coupon.model.CouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 龙三
 * @since 2021-03-26
 */
public interface CouponMapper extends BaseMapper<CouponDO> {

    int reduceStock(@Param("couponId") long couponId);
}
