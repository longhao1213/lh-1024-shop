package com.lh.interceptor;

import com.lh.model.LoginUser;
import com.lh.utils.CommonUtil;
import com.lh.utils.JsonData;
import com.lh.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: LoginInterceptor.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/03/08 15:32
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = request.getHeader("token");
            if (token == null) {
                token = request.getParameter("token");
            }
            if (StringUtils.isNotBlank(token)) {
                Claims claims = JwtUtils.checkJWT(token);
                if (claims == null) {
                    CommonUtil.sendJsonMessage(response, JsonData.buildError("登录过期，重新登录"));
                    return false;
                }
                Long id = Long.valueOf(claims.get("id").toString());
                String headImg = claims.get("head_img").toString();
                String mail = claims.get("mail").toString();
                String name = claims.get("name").toString();

                LoginUser loginUser = LoginUser
                        .builder()
                        .id(id)
                        .headImg(headImg)
                        .name(name)
                        .mail(mail).build();
//                loginUser.setId(id);
//                loginUser.setHeadImg(headImg);
//                loginUser.setMail(mail);
//                loginUser.setName(name);

//                // 通过attribute传递
//                request.setAttribute("loginUser", loginUser);

                // 通过threadLocal传递
                threadLocal.set(loginUser);

                return true;
            }
        } catch (Exception e) {
            log.error("拦截器错误：{}", e);
        }
        CommonUtil.sendJsonMessage(response, JsonData.buildError("token不存在，重新登录"));
        return false;
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}