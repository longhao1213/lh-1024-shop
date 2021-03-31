package com.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coupon.mapper.CouponRecordMapper;
import com.coupon.model.CouponDO;
import com.coupon.mapper.CouponMapper;
import com.coupon.model.CouponRecordDO;
import com.coupon.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coupon.vo.CouponVO;
import com.lh.enums.BizCodeEnum;
import com.lh.enums.CouponCategoryEnum;
import com.lh.enums.CouponPublishEnum;
import com.lh.enums.CouponStateEnum;
import com.lh.exception.BizException;
import com.lh.interceptor.LoginInterceptor;
import com.lh.model.LoginUser;
import com.lh.utils.CommonUtil;
import com.lh.utils.JsonData;
import com.lh.vo.PageDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 龙三
 * @since 2021-03-26
 */
@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Override
    public PageDataVo pageCouponActivity(int page, int size) {
        Page<CouponDO> pageInfo = new Page<>(page,size);
        IPage<CouponDO> couponDOIPage =  couponMapper.selectPage(pageInfo,new QueryWrapper<CouponDO>()
                .eq("publish", CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));

        PageDataVo pageDataVo = new PageDataVo();
        pageDataVo.setTotalPage(pageInfo.getPages());
        pageDataVo.setTotalRecord(pageInfo.getCurrent());
        pageDataVo.setCurrentData(couponDOIPage.getRecords().stream().map(obj -> beanProcess(obj)).collect(Collectors.toList()));
        return pageDataVo;
    }

    /**
     * 领券接口
     * 1 获取优惠券是否存在
     * 2 校验优惠券是否可以领取：时间、库存、超过限制
     * 3 扣减库存
     * 4 保存领券几率
     * @param couponId
     * @param category
     * @return
     */
    @Override
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
                .eq("id", couponId)
                .eq("category", category.name()));

        // 优惠券是否可以领取
        this.checkCoupon(couponDO, loginUser.getId());

        // 构建领券几率
        CouponRecordDO couponRecordDO = new CouponRecordDO();
        BeanUtils.copyProperties(couponDO, couponRecordDO);
        couponRecordDO.setCreateTime(new Date());
        couponRecordDO.setUseState(CouponStateEnum.NEW.name());
        couponRecordDO.setUserId(loginUser.getId());
        couponRecordDO.setUserName(loginUser.getName());
        couponRecordDO.setCouponId(couponId);
        couponRecordDO.setId(null);

        // 扣减库存 TODO
        int rows = couponMapper.reduceStock(couponId);

        if (rows == 1) {
            couponRecordMapper.insert(couponRecordDO);
        } else {
            log.warn("发放优惠券失败：{},用户：{}", couponDO, loginUser);
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }
        return JsonData.buildSuccess();
    }

    /**
     * 校验优惠券是否可以领取
     * @param couponDO
     * @param id
     */
    private void checkCoupon(CouponDO couponDO, Long id) {
        if(couponDO==null){
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }

        //库存是否足够
        if(couponDO.getStock()<=0){
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        //判断是否是否发布状态
        if(!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())){
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //是否在领取时间范围
        long time = CommonUtil.getCurrentTimestamp();
        long start = couponDO.getStartTime().getTime();
        long end = couponDO.getEndTime().getTime();
        if(time<start || time>end){
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        //用户是否超过限制
        int recordNum =  couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id",couponDO.getId())
                .eq("user_id",id));

        if(recordNum >= couponDO.getUserLimit()){
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }
    }

    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO,couponVO);
        return couponVO;
    }
}
