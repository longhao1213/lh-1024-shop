package com.lh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.enums.BizCodeEnum;
import com.lh.enums.SendCodeEnum;
import com.lh.interceptor.LoginInterceptor;
import com.lh.model.LoginUser;
import com.lh.model.UserDO;
import com.lh.mapper.UserMapper;
import com.lh.request.UserLoginRequest;
import com.lh.service.NotifyService;
import com.lh.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lh.utils.CommonUtil;
import com.lh.utils.JsonData;
import com.lh.utils.JwtUtils;
import com.lh.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lh.request.UserRegisterRequest;

import java.util.Date;
import java.util.List;

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

        //设置密码
        //生成秘钥
        userDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));
        //密码 + 加盐处理
        String cryptPwd = Md5Crypt.md5Crypt(userRegisterRequest.getPwd().getBytes(), userDO.getSecret());
        userDO.setPwd(cryptPwd);
        //账号唯一性检查
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
     * 1、根据Mail去找有没这记录
     * 2、有的话，则用秘钥+用户传递的明文密码，进行加密，再和数据库的密文进行匹配
     *
     * @param userLoginRequest
     * @return
     */
    @Override
    public JsonData login(UserLoginRequest userLoginRequest) {

        List<UserDO> userDOList =  userMapper.selectList(new QueryWrapper<UserDO>().eq("mail",userLoginRequest.getMail()));

        if(userDOList!=null && userDOList.size()==1){
            //已经注册
            UserDO userDO = userDOList.get(0);
            String cryptPwd = Md5Crypt.md5Crypt(userLoginRequest.getPwd().getBytes(),userDO.getSecret());
            if(cryptPwd.equals(userDO.getPwd())){
                //登录成功,生成token
                LoginUser userDTO = LoginUser.builder().build();
                BeanUtils.copyProperties(userDO, userDTO);
                String token = JwtUtils.geneJsonWebToken(userDTO);
                log.info("用户登录token，{}", token);
                return JsonData.buildSuccess(token);
            }else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }
        }else {
            //未注册
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }


    }

    /**
     * 查看用户详情
     * @return
     */
    @Override
    public UserVo findUserDetail() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("id", loginUser.getId()));
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userDO, userVo);
        return userVo;
    }


    /**
     * 校验用户账号唯一
     * @param mail
     * @return
     */
    private boolean checkUnique(String mail) {
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<UserDO>().eq("mail", mail);
        Integer integer = userMapper.selectCount(queryWrapper);
        return integer > 0 ? false : true;
    }


    /**
     * 用户注册，初始化福利信息 TODO
     * @param userDO
     */
    private void userRegisterInitTask(UserDO userDO){

    }
}
