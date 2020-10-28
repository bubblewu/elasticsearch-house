package com.bubble.house.config.security;

import com.bubble.house.entity.user.UserEntity;
import com.bubble.house.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证实现
 *
 * @author wugang
 * date: 2019-11-06 14:25
 **/
public class AuthProvider implements AuthenticationProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthProvider.class);

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();

        UserEntity user = userService.findUserByName(userName);
        if (user == null) {
            LOGGER.error("用户[{}]无权限", userName);
            throw new AuthenticationCredentialsNotFoundException("authError");
        }
        if (user.getPassword().equals(inputPassword)) {
            // 参数：认证用户、密码、拥有的认证权限
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }

        LOGGER.error("用户[{}]密码错误", userName);
        throw new BadCredentialsException("用户密码错误");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
