package com.bubble.house.service;

import com.bubble.house.ApplicationTests;
import com.bubble.house.service.house.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * 七牛云服务测试
 *
 * @author wugang
 * date: 2020-10-28 14:49
 **/
public class QiNiuServiceTests extends ApplicationTests {

    @Autowired
    private QiNiuService qiNiuService;

    @Test
    public void testUploadFile() {
        String fileName = "/Users/wugang/code/java/elasticsearch-house/tmp/images/testImg.png";
        File file = new File(fileName);
        Assertions.assertTrue(file.exists());
        try {
            Response response = qiNiuService.uploadFile(file);
            Assertions.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        String key = "FvyNceBAaZF6TBh6OZpcEKlhuACG";
        try {
            Response response = qiNiuService.delete(key);
            Assertions.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

}
