package com.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.product.model.BannerDO;
import com.product.mapper.BannerMapper;
import com.product.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.product.vo.BannerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 龙三
 * @since 2021-04-09
 */
@Service
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<BannerVO> list() {
        List<BannerDO> bannerDOList = bannerMapper.selectList(new QueryWrapper<BannerDO>().orderByAsc("weight"));
        List<BannerVO> bannerVOList = bannerDOList.stream().map(obj -> {
            BannerVO bannerVO = new BannerVO();
            BeanUtils.copyProperties(obj, bannerVO);
            return bannerVO;
        }).collect(Collectors.toList());

        return bannerVOList;
    }
}
