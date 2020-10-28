package com.bubble.house.repository;

import com.bubble.house.entity.user.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Role：角色数据Dao
 *
 * @author wugang
 * date: 2019-11-06 10:51
 **/
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    /**
     * 根据userId获取Role信息
     *
     * @param userId 用户ID
     * @return List<RoleEntity>
     */
    List<RoleEntity> findRolesByUserId(Long userId);
}
