package com.bubble.house.repository;

import com.bubble.house.entity.user.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * JPA操作类：用户接口
 * 继承CrudRepository，传入类和主键的类型
 *
 * @author wugang
 * date: 2019-11-01 18:12
 **/
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    UserEntity findByName(String userName);

    /**
     * 根据手机号获取用户信息
     *
     * @param telephone 手机号
     * @return 用户信息
     */
    UserEntity findUserByPhoneNumber(String telephone);

    /**
     * 根据id更新用户名
     *
     * @param id   用户ID
     * @param name 用户名
     */
    @Modifying
    @Query("update UserEntity as user set user.name = :name where id = :id")
    void updateUsername(@Param(value = "id") Long id, @Param(value = "name") String name);

    /**
     * 根据id更新邮箱
     *
     * @param id    用户ID
     * @param email 邮箱
     */
    @Modifying
    @Query("update UserEntity as user set user.email = :email where id = :id")
    void updateEmail(@Param(value = "id") Long id, @Param(value = "email") String email);

    /**
     * 根据id更新密码
     *
     * @param id       用户ID
     * @param password 密码
     */
    @Modifying
    @Query("update UserEntity as user set user.password = :password where id = :id")
    void updatePassword(@Param(value = "id") Long id, @Param(value = "password") String password);

}
