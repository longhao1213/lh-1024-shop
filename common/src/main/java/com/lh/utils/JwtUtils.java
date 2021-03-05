package com.lh.utils;

import com.lh.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: JwtUtils.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/03/05 15:14
 */
@Slf4j
public class JwtUtils {
    /**
     * token 过期时间，正常是7天，方便测试我们改为70
     */
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7 * 10;

    /**
     * 加密的秘钥
     */
    private static final String SECRET = "longsan.net666";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "ls1024shop";

    /**
     * subject
     */
    private static final String SUBJECT = "longsan";

    /**
     * 获取token的方法
     *
     * @param user
     * @return
     */
    public static String geneJsonWebToken(LoginUser loginUser) {
        if (loginUser == null) {
            throw new NullPointerException("loginUser对象为空");
        }
        Long userId = loginUser.getId();
        String token =
                Jwts.builder().setSubject(SUBJECT)
                        .claim("head_img", loginUser.getHeadImg())
                .claim("id", loginUser.getId())
                .claim("name", loginUser.getName())
                .claim("mail", loginUser.getMail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        return TOKEN_PREFIX + token;
    }

    /**
     * 校验token的方法
     *
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            return claims;
        } catch (Exception e) {
            log.info("token解密失败，失败原因：{}", e.getMessage());
            return null;
        }
    }


}