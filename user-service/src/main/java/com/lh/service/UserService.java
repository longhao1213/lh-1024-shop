package com.lh.service;

import com.lh.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lh.utils.JsonData;
import request.UserRegisterRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
public interface UserService extends IService<UserDO> {

    JsonData register(UserRegisterRequest userRegisterRequest);
}
