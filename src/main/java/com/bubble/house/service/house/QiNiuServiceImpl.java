package com.bubble.house.service.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;

/**
 * 七牛云服务
 *
 * @author wugang
 * date: 2019-11-05 14:36
 **/
@Service
public class QiNiuServiceImpl implements QiNiuService {
    private final static Logger LOGGER = LoggerFactory.getLogger(QiNiuServiceImpl.class);

    @Value("${qiniu.bucket}")
    private String bucket;
    private StringMap putPolicy;

    private final UploadManager uploadManager;
    private final BucketManager bucketManager;
    private final Auth auth;

    @PostConstruct
    public void init() {
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
