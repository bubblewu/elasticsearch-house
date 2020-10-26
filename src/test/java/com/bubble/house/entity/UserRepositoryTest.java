package com.bubble.house.entity;

import com.bubble.house.ApplicationTests;
import com.bubble.house.entity.user.UserEntity;
import com.bubble.house.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户表测试用例
 *
 * @author wugang
 * date: 2019-11-01 18:17
 **/
public class UserRepositoryTest extends ApplicationTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindById() {
        UserEntity user = userRepository.findById(1L).orElse(new UserEntity());
        Assertions.assertEquals("bubble", user.getName());
//        Assertions.assertTrue("bubble".equals(user.getName()), "testFindById测试成功");
    }


}
