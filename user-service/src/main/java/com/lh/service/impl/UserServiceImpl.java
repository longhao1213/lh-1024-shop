package com.lh.service.impl;

import com.lh.enums.BizCodeEnum;
import com.lh.enums.SendCodeEnum;
import com.lh.model.UserDO;
import com.lh.mapper.UserMapper;
import com.lh.service.NotifyService;
import com.lh.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lh.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import request.UserRegisterRequest;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 龙三
 * @since 2021-02-02
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotifyService notifyService;

    @Override
    public JsonData register(UserRegisterRequest userRegisterRequest) {
        boolean checkCode = false;
        //校验验证码
        if(StringUtils.isNotBlank(userRegisterRequest.getMail())){
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER,userRegisterRequest.getMail(),userRegisterRequest.getCode());
        }

        if(!checkCode){
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userRegisterRequest,userDO);

        userDO.setCreateTime(new Date());
        userDO.setSlogan("人生需要动态规划，学习需要贪心算法");

        //设置密码 TODO
        //userDO.setPwd(userRegisterRequest.getPwd());

        //账号唯一性检查  TODO

        if(checkUnique(userDO.getMail())){
            int rows = userMapper.insert(userDO);
            log.info("rows:{},注册成功:{}",rows,userDO.toString());

            //新用户注册成功，初始化信息，发放福利等 TODO
            userRegisterInitTask(userDO);
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }

    }

    /**
     * 校验用户账号唯一
     * @param mail
     * @return
     */
    private boolean checkUnique(String mail) {

        return false;
    }


    /**
     * 用户注册，初始化福利信息 TODO
     * @param userDO
     */
    private void userRegisterInitTask(UserDO userDO){

    }
}
