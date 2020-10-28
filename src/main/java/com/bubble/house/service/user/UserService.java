package com.bubble.house.service.user;

import com.bubble.house.entity.dto.UserDTO;
import com.bubble.house.entity.result.ServiceResultEntity;
import com.bubble.house.entity.user.UserEntity;

/**
 * 用户服务
 *
 * @author wugang
 * date: 2019-11-06 10:46
 **/
public interface UserService {

    /**
     * 根据用户名查询并获取用户权限
     *
     * @param userName 用户名
     * @return 用户实体
     */
    UserEntity findUserByName(String userName);

    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return ResultEntity<UserDTO>
     */
    ServiceResultEntity<UserDTO> findById(Long userId);

    /**
     * 根据电话号码寻找用户
     *
     * @param telephone 联系方式
     * @return UserEntity
     */
    UserEntity findUserByTelephone(String telephone);

    /**
     * 通过手机号注册用户
     *
     * @param telephone 手机号
     * @return UserEntity
     */
    UserEntity addUserByPhone(String telephone);

    /**
     * 修改指定属性值
     *
     * @param profile 要修改的属性
     * @param value   值
     * @return ResultEntity
     */
    ServiceResultEntity modifyUserProfile(String profile, String value);

}
