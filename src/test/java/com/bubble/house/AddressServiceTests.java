package com.bubble.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.service.house.AddressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wugang
 * date: 2019-11-21 15:17
 **/
public class AddressServiceTests extends ApplicationTests {

    @Autowired
    private AddressService addressService;

    /**
     * 地理编码测试
     */
    @Test
    public void testGetMapLocation() {
        String city = "北京";
        String address = "望京SOHO";
        ServiceResultEntity<BaiDuMapEntity> result = addressService.getBaiDuMapLocation(city, address);
        System.out.println(result.getResult().getLongitude());
        System.out.println(result.getResult().getLatitude());

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertTrue(result.getResult().getLongitude() > 0);
        Assertions.assertTrue(result.getResult().getLatitude() > 0);
    }

}
