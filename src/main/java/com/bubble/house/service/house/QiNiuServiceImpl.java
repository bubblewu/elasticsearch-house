package com.bubble.house.service.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * 七牛云服务
 *
 * @author wugang
 * date: 2019-11-05 14:36
 **/
@Service
public class QiNiuServiceImpl implements QiNiuService, InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(QiNiuServiceImpl.class);

    /**
     * 图片存储空间名字
     */
    @Value("${qiniu.bucket}")
    private String bucket;
    /**
     * 返回结果（遵循七牛云定义的规范）
     */
    private StringMap putPolicy;

    /**
     * 上传实例
     */
    private final UploadManager uploadManager;
    /**
     * 空间管理实例
     */
    private final BucketManager bucketManager;
    /**
     * 用户认证实例
     */
    private final Auth auth;

    /**
     * afterPropertiesSet方法，初始化bean的时候执行，可以针对某个具体的bean进行配置。
     * afterPropertiesSet 必须实现 InitializingBean接口。实现 InitializingBean接口必须实现afterPropertiesSet方法。
     * <p>
     * 执行顺序：
     * afterPropertiesSet 和init-method之间的执行顺序是afterPropertiesSet 先执行，init-method 后执行。
     * 从BeanPostProcessor的作用，可以看出最先执行的是postProcessBeforeInitialization，
     * 然后是afterPropertiesSet，然后是init-method，
     * 然后是postProcessAfterInitialization
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    public QiNiuServiceImpl(UploadManager uploadManager, BucketManager bucketManager, Auth auth) {
        this.uploadManager = uploadManager;
        this.bucketManager = bucketManager;
        this.auth = auth;
    }

    @Override
    public Response uploadFile(File file) throws QiniuException {
        Response response = this.uploadManager.put(file, null, getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, null, getUploadToken());
            retry++;
        }
        LOGGER.info("七牛云上传图片 [{}]：{}", file.toString(), response.isOK());
        return response;
    }

    @Override
    public Response uploadFile(InputStream inputStream) throws QiniuException {
        Response response = this.uploadManager.put(inputStream, null, getUploadToken(), null, null);
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, null, getUploadToken(), null, null);
            retry++;
        }
        LOGGER.info("七牛云上传图片 [{}]：{}", inputStream.toString(), response.isOK());
        return response;
    }

    @Override
    public Response delete(String key) throws QiniuException {
        Response response = bucketManager.delete(this.bucket, key);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(bucket, key);
        }
        LOGGER.info("七牛云删除图片 [{}], {}", key, response.isOK());
        return response;
    }

    /**
     * 获取上传凭证
     */
    private String getUploadToken() {
        return this.auth.uploadToken(bucket, null, 3600, putPolicy);
    }

}
