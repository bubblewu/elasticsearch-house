package com.bubble.house.service.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * 七牛云服务
 *
 * @author wugang
 * date: 2019-11-05 14:35
 **/
public interface QiNiuService {

    /**
     * 上传图片
     *
     * @param file 文件
     * @return Response
     * @throws QiniuException 异常
     */
    Response uploadFile(File file) throws QiniuException;

    /**
     * 文件流上传
     *
     * @param inputStream 文件流
     * @return Response
     * @throws QiniuException 异常
     */
    Response uploadFile(InputStream inputStream) throws QiniuException;

    /**
     * 删除
     *
     * @param key 关键字
     * @return Response
     * @throws QiniuException 异常
     */
    Response delete(String key) throws QiniuException;

}
