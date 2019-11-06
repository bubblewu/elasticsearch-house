package com.bubble.house.config.security;

import com.bubble.house.entity.user.UserEntity;
import com.bubble.house.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义认证实现
 *
 * @author wugang
 * date: 2019-11-06 14:25
 **/
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();

        UserEntity user = userService.findUserByName(userName);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }
//        if (this.passwordEncoder.matches(user.getPassword(), inputPassword + user.getId())) {
//            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//        }
        if (user.getPassword().equals(inputPassword)) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
