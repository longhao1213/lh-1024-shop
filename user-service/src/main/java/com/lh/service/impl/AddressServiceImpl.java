package com.lh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.interceptor.LoginInterceptor;
import com.lh.mapper.AddressMapper;
import com.lh.model.AddressDO;
import com.lh.model.LoginUser;
import com.lh.request.AddressAddRequest;
import com.lh.service.AddressService;
import com.lh.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressVO detail(long id) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id",id).eq("user_id",loginUser.getId()));

        if(addressDO == null){
            return null;
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(addressDO,addressVO);

        return addressVO;
    }

    @Override
    public int add(AddressAddRequest addressAddRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        AddressDO addressDO = new AddressDO();
        addressDO.setCreateTime(new Date());
        BeanUtils.copyProperties(addressAddRequest, addressDO);
        addressDO.setUserId(loginUser.getId());
        //是否有默认收货地址
        if (addressDO.getDefaultStatus() == 1) {
            AddressDO defaultAddressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("user_id", loginUser.getId()).eq("default_status", 1).eq("user_id",loginUser.getId()));
            if (defaultAddressDO != null) {
                //修改为非默认地址
                defaultAddressDO.setDefaultStatus(0);
                addressMapper.update(defaultAddressDO, new QueryWrapper<AddressDO>().eq("id", defaultAddressDO.getId()));
            }
        }
        int rows = addressMapper.insert(addressDO);
        return rows;
    }



    /**
     * 根据id删除地址
     * @param addressId
     * @return
     */
    @Override
    public int del(int addressId) {
        int rows = addressMapper.delete(new QueryWrapper<AddressDO>().eq("id",addressId));
        return rows;
    }

    /**
     * 查找用全部收货地址
     * @return
     */
    @Override
    public List<AddressVO> listUserAllAddress() {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        List<AddressDO> list = addressMapper.selectList(new QueryWrapper<AddressDO>().eq("user_id",loginUser.getId()));

        List<AddressVO> addressVOList =  list.stream().map(obj->{
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(obj,addressVO);
            return addressVO;
        }).collect(Collectors.toList());

        return addressVOList;

    }

}
