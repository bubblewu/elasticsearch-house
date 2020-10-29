package com.bubble.house.service.user;

import com.bubble.house.base.util.LoginUserUtils;
import com.bubble.house.entity.user.RoleEntity;
import com.bubble.house.entity.user.UserEntity;
import com.bubble.house.repository.RoleRepository;
import com.bubble.house.repository.UserRepository;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.user.UserDTO;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * User
 *
 * @author wugang
 * date: 2019-11-06 10:50
 **/
@Service
public class UserServiceImpl implements UserService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        modelMapper = new ModelMapper();
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public UserEntity findUserByName(String userName) {
        UserEntity user = userRepository.findByName(userName);
        if (user != null) {
            List<RoleEntity> roles = roleRepository.findRolesByUserId(user.getId());
            if (roles == null || roles.isEmpty()) {
                LOGGER.error("用户[{}]无权限", userName);
                throw new DisabledException("用户[" + userName + "]无权限");
            }
            // 设置用户权限
            List<GrantedAuthority> authorities = new ArrayList<>();
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
            user.setAuthorityList(authorities);
            return user;
        }
        return null;
    }

    @Override
    public ServiceResultEntity<UserDTO> findById(Long userId) {
        Optional<UserEntity> userOp = userRepository.findById(userId);
        if (!userOp.isPresent()) {
            return ServiceResultEntity.notFound();
        }
        UserDTO userDTO = modelMapper.map(userOp.get(), UserDTO.class);
        return ServiceResultEntity.of(userDTO);
    }

    @Override
    public UserEntity findUserByTelephone(String telephone) {
        UserEntity user = userRepository.findUserByPhoneNumber(telephone);
        if (null == user) {
            return null;
        }
        List<RoleEntity> roles = roleRepository.findRolesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            LOGGER.error("用户[{} - {}]无权限", user.getId(), telephone);
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorityList(authorities);
        return user;
    }

    @Override
    public UserEntity addUserByPhone(String telephone) {
        UserEntity user = new UserEntity();
        user.setPhoneNumber(telephone);
        user.setName(telephone.substring(0, 3) + "****" + telephone.substring(7, telephone.length()));
        Date now = new Date();
        user.setCreateTime(now);
        user.setLastLoginTime(now);
        user.setLastUpdateTime(now);
        user = userRepository.save(user);

        RoleEntity role = new RoleEntity();
        role.setName("USER");
        role.setUserId(user.getId());
        roleRepository.save(role);
        user.setAuthorityList(Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));
        return user;
    }

    @Override
    @Transactional
    public ServiceResultEntity modifyUserProfile(String profile, String value) {
        Long userId = LoginUserUtils.getLoginUserId();
        if (profile == null || profile.isEmpty()) {
            return new ServiceResultEntity(false, "属性不可以为空");
        }
        switch (profile) {
            case "name":
                userRepository.updateUsername(userId, value);
                break;
            case "email":
                userRepository.updateEmail(userId, value);
                break;
            case "password":
//                userRepository.updatePassword(userId, this.passwordEncoder.encode(value + userId));
                userRepository.updatePassword(userId, value);
                break;
            default:
                return new ServiceResultEntity(false, "不支持的属性");
        }
        return ServiceResultEntity.success();
    }
}
