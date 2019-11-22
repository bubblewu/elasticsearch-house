package com.bubble.house.modelmapper;

import com.bubble.house.ApplicationTests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

/**
 * 在开发过程中经常会有一个需求，就是类型转换 (把一个类转成另一个类)
 * modelmapper就是一个提高生产力的工具
 *
 * @author wugang
 * date: 2019-11-12 11:27
 **/
public class ModelMapperTest extends ApplicationTests {

    private static ModelMapper modelMapper;

    private static ObjectMapper objectMapper;

    private static List<UserBean> userList;
    private static UserBean userBean;

    @BeforeAll
    private static void before() {
        modelMapper = new ModelMapper();

        objectMapper = new ObjectMapper();

        userBean = new UserBean("000", "test", "120");
        userList = Lists.newArrayList();
        userList.add(new UserBean("001", "bubble", "110"));
        userList.add(new UserBean("002", "hello", "175"));
        userList.add(new UserBean("003", "java", "138"));
    }

    @Test
    public void testVO() {
        // 属性名保持一致，采用默认规则
        UserDTO userDTO = modelMapper.map(userBean, UserDTO.class);
        System.out.println(userDTO.toString());
    }

    @Test
    public void testObject() throws JsonProcessingException {
        // 将Bean转为JSON
        System.out.println(Arrays.toString(objectMapper.writeValueAsBytes(userBean)));
        System.out.println(objectMapper.writeValueAsString(userBean));

    }

}
