package com.user.service.impl;

import com.user.model.AddressDO;
import com.user.mapper.AddressMapper;
import com.user.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressDO> implements AddressService {

}
