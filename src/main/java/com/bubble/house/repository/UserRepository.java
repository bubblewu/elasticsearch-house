package com.bubble.house.repository;

import com.bubble.house.entity.user.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * JPA操作类：用户接口
 *
 * @author wugang
 * date: 2019-11-01 18:12
 **/
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByName(String userName);

    UserEntity findUserByPhoneNumber(String telephone);

    @Modifying
    @Query("update UserEntity as user set user.name = :name where id = :id")
    void updateUsername(@Param(value = "id") Long id, @Param(value = "name") String name);

    @Modifying
    @Query("update UserEntity as user set user.email = :email where id = :id")
    void updateEmail(@Param(value = "id") Long id, @Param(value = "email") String email);

    @Modifying
    @Query("update UserEntity as user set user.password = :password where id = :id")
    void updatePassword(@Param(value = "id") Long id, @Param(value = "password") String password);

}
