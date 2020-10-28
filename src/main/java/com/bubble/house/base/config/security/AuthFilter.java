package com.bubble.house.base.config.security;

import com.bubble.house.base.util.LoginUserUtils;
import com.bubble.house.entity.user.UserEntity;
import com.bubble.house.service.user.SMSService;
import com.bubble.house.service.user.UserService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * User访问权限控制
 *
 * @author wugang
 * date: 2019-11-06 10:43
 **/
public class AuthFilter extends UsernamePasswordAuthenticationFilter {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SMSService smsService;

    /**
     * 用户鉴权
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String name = obtainUsername(request);
        if (!Strings.isNullOrEmpty(name)) {
            request.setAttribute("username", name);
            return super.attemptAuthentication(request, response);
        }
        String telephone = request.getParameter("telephone");
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtils.checkTelephone(telephone)) {
            LOGGER.error("联系方式输入有误或非法");
            throw new BadCredentialsException("Wrong telephone number");
        }

        UserEntity user = this.userService.findUserByTelephone(telephone);
        String inputCode = request.getParameter("smsCode");
        String sessionCode = this.smsService.getSmsCode(telephone);
        if (Objects.equals(inputCode, sessionCode)) {
            // 如果用户第一次用手机登录 则自动注册该用户
            if (user == null) {
                user = this.userService.addUserByPhone(telephone);
            }
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } else {
            LOGGER.error("smsCodeError");
            throw new BadCredentialsException("smsCodeError");
        }
    }

}
