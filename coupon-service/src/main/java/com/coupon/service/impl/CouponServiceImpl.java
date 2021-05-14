package com.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coupon.mapper.CouponRecordMapper;
import com.coupon.model.CouponDO;
import com.coupon.mapper.CouponMapper;
import com.coupon.model.CouponRecordDO;
import com.coupon.request.NewUserCouponRequest;
import com.coupon.service.CouponService;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
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

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageDataVo pageCouponActivity(int page, int size) {
        Page<CouponDO> pageInfo = new Page<>(page, size);
        IPage<CouponDO> couponDOIPage = couponMapper.selectPage(pageInfo, new QueryWrapper<CouponDO>()
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
     *
     * @param couponId
     * @param category
     * @return
     */
    @Transactional(rollbackFor=Exception.class,propagation= Propagation.REQUIRED)
    @Override
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        // 使用框架加锁方式
        String lockKey = "lock:coupon:" + couponId + ":" + loginUser.getId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        log.info("加锁成功：{}", Thread.currentThread().getId());
        try {
            // 执行业务逻辑
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

            // 扣减库存
            int rows = couponMapper.reduceStock(couponId);

            if (rows == 1) {
                couponRecordMapper.insert(couponRecordDO);
            } else {
                log.warn("发放优惠券失败：{},用户：{}", couponDO, loginUser);
                throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
            }
        } finally {
            // 解锁
            lock.unlock();
            log.info("解锁：{}", Thread.currentThread().getId());
        }



    // 传统加锁方式
//        String uuid = CommonUtil.generateUUID();
//        String lockKey = "lock:coupon:" + couponId;
//        Boolean lockFlag = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, Duration.ofSeconds(30));
//        if (lockFlag) {
//            log.info("加锁成功：{}", couponId);
//            try {
//                // 执行业务逻辑
//                CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
//                        .eq("id", couponId)
//                        .eq("category", category.name()));
//
//                // 优惠券是否可以领取
//                this.checkCoupon(couponDO, loginUser.getId());
//
//                // 构建领券几率
//                CouponRecordDO couponRecordDO = new CouponRecordDO();
//                BeanUtils.copyProperties(couponDO, couponRecordDO);
//                couponRecordDO.setCreateTime(new Date());
//                couponRecordDO.setUseState(CouponStateEnum.NEW.name());
//                couponRecordDO.setUserId(loginUser.getId());
//                couponRecordDO.setUserName(loginUser.getName());
//                couponRecordDO.setCouponId(couponId);
//                couponRecordDO.setId(null);
//
//                // 扣减库存
//                int rows = couponMapper.reduceStock(couponId);
//
//                if (rows == 1) {
//                    couponRecordMapper.insert(couponRecordDO);
//                } else {
//                    log.warn("发放优惠券失败：{},用户：{}", couponDO, loginUser);
//                    throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
//                }
//            }finally {
//                // 解锁
//                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
//                Integer result = (Integer) redisTemplate.execute(new DefaultRedisScript<>(script, Integer.class), Arrays.asList(lockKey), uuid);
//                log.info("解锁：{}",result);
//            }
//        } else {
//            // 加锁失败
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                log.error("自旋失败");
//            }
//            addCoupon(couponId, category);
//
//        }




        return JsonData.buildSuccess();
}


    /**
     * 用户微服务调用的时候，没传递token
     *
     * 本地直接调用发放优惠券的方法，需要构造一个登录用户存储在threadlocal
     *
     * @param newUserCouponRequest
     * @return
     */
    @Transactional(rollbackFor=Exception.class,propagation=Propagation.REQUIRED)
    @Override
    public JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest) {
        LoginUser loginUser = LoginUser.builder().build();
        loginUser.setId(newUserCouponRequest.getUserId());
        loginUser.setName(newUserCouponRequest.getName());
        LoginInterceptor.threadLocal.set(loginUser);

        //查询新用户有哪些优惠券
        List<CouponDO> couponDOList = couponMapper.selectList(new QueryWrapper<CouponDO>()
                .eq("category",CouponCategoryEnum.NEW_USER.name()));

        for(CouponDO couponDO : couponDOList){
            //幂等操作，调用需要加锁
            this.addCoupon(couponDO.getId(),CouponCategoryEnum.NEW_USER);

        }

        return JsonData.buildSuccess();
    }

    /**
     * 校验优惠券是否可以领取
     *
     * @param couponDO
     * @param id
     */
    private void checkCoupon(CouponDO couponDO, Long id) {
        if (couponDO == null) {
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }

        //库存是否足够
        if (couponDO.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        //判断是否是否发布状态
        if (!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //是否在领取时间范围
        long time = CommonUtil.getCurrentTimestamp();
        long start = couponDO.getStartTime().getTime();
        long end = couponDO.getEndTime().getTime();
        if (time < start || time > end) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        //用户是否超过限制
        int recordNum = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id", couponDO.getId())
                .eq("user_id", id));

        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }
    }

    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO, couponVO);
        return couponVO;
    }
}
