package com.bubble.house.repository;

import com.bubble.house.entity.user.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Role
 *
 * @author wugang
 * date: 2019-11-06 10:51
 **/
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    List<RoleEntity> findRolesByUserId(Long userId);
}
