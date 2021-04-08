package com.coupon.service;

import com.coupon.model.CouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coupon.vo.CouponRecordVO;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 龙三
 * @since 2021-03-26
 */
public interface CouponRecordService {

    /**
     * 分页查询领劵记录
     * @param page
     * @param size
     * @return
     */
    Map<String,Object> page(int page, int size);


    /**
     * 根据id查询详情
     * @param recordId
     * @return
     */
    CouponRecordVO findById(long recordId);

}
