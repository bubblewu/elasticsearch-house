package com.bubble.house.base.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 转换工具：Map和JavaBean互转
 *
 * @author wugang
 * date: 2019-11-22 11:43
 **/
public class ConvertUtils {

    /**
     * 将一个JavaBean转化为一个Map
     *
     * @param bean 要转化的JavaBean
     * @return Map对象
     */
    public static Map<String, Object> convertBean2Map(Object bean) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Class type = bean.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if ("_id".equals(propertyName)) {
                    continue;
                }
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } else {
                        returnMap.put(propertyName, "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

}
