package com.lh.service;

import com.lh.model.AddressDO;
import com.lh.request.AddressAddRequest;
import com.lh.vo.AddressVO;

import java.util.List;

/**
 * <p>
 * 电商-公司收发货地址表 服务类
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
public interface AddressService {

    AddressVO detail(long id);

    int add(AddressAddRequest addressAddRequest);

    /**
     * 根据id删除地址
     * @param addressId
     * @return
     */
    int del(int addressId);

    /**
     * 查找用户全部收货地址
     * @return
     */
    List<AddressVO> listUserAllAddress();
}
