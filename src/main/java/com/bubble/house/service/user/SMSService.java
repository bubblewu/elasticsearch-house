package com.bubble.house.service.user;

import com.bubble.house.entity.result.ResultEntity;

/**
 * SMS短信服务：验证码服务
 *
 * @author wugang
 * date: 2019-11-06 11:27
 **/
public interface SMSService {

    /**
     * 发送验证码到指定手机 并 缓存验证码 10分钟 及 请求间隔时间1分钟
     */
    ResultEntity<String> sendSms(String telephone);

    /**
     * 获取缓存中的验证码
     */
    String getSmsCode(String telephone);

    /**
     * 移除指定手机号的验证码缓存
     */
    void remove(String telephone);

}
