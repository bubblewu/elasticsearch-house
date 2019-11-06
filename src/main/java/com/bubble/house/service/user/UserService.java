package com.bubble.house.service.user;

import com.bubble.house.entity.dto.UserDTO;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.entity.user.UserEntity;

/**
 * 用户服务
 *
 * @author wugang
 * date: 2019-11-06 10:46
 **/
public interface UserService {

    UserEntity findUserByName(String userName);

    ResultEntity<UserDTO> findById(Long userId);

    /**
     * 根据电话号码寻找用户
     */
    UserEntity findUserByTelephone(String telephone);

    /**
     * 通过手机号注册用户
     */
    UserEntity addUserByPhone(String telephone);

    /**
     * 修改指定属性值
     */
    ResultEntity modifyUserProfile(String profile, String value);

}
