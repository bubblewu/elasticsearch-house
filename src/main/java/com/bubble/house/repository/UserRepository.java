package com.bubble.house.repository;

import com.bubble.house.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * JPA操作类：用户接口
 *
 * @author wugang
 * date: 2019-11-01 18:12
 **/
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
