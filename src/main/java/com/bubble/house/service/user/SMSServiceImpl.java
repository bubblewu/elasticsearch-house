package com.bubble.house.service.user;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.bubble.house.service.ServiceResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 *
 * @author wugang
 * date: 2019-11-06 11:28
 **/
@Service
public class SMSServiceImpl implements SMSService, InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(SMSServiceImpl.class);

    @Value("${aliyun.sms.access-key}")
    private String accessKey;
    @Value("${aliyun.sms.secret-key}")
    private String secertKey;
    @Value("${aliyun.sms.template.code}")
    private String templateCode;

    private IAcsClient acsClient;

    private final static String SMS_CODE_CONTENT_PREFIX = "SMS::CODE::CONTENT";

    private static final String[] NUMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final Random random = new Random();

    private final RedisTemplate<String, String> redisTemplate;

    public SMSServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public ServiceResultEntity<String> sendSms(String telephone) {
        String gapKey = "SMS::CODE::INTERVAL::" + telephone;
        String result = redisTemplate.opsForValue().get(gapKey);
        if (result != null) {
            LOGGER.error("send sms [{}] 请求次数太频繁", telephone);
            return new ServiceResultEntity<>(false, "请求次数太频繁");
        }
        String code = generateRandomSmsCode();
        String templateParam = String.format("{\"code\": \"%s\"}", code);
        // 组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        // 使用post提交
        request.setSysMethod(MethodType.POST);
        request.setPhoneNumbers(telephone);
        request.setTemplateParam(templateParam);
        request.setTemplateCode(templateCode);
        request.setSignName("房搜搜");

        boolean success = false;
        try {
            SendSmsResponse response = acsClient.getAcsResponse(request);
            if ("OK".equals(response.getCode())) {
                success = true;
            } else {
                LOGGER.error("send sms [{}] 验证码发送失败", telephone);
            }
        } catch (ClientException e) {
            LOGGER.error("send sms [{}] 异常", telephone);
        }
        if (success) {
            redisTemplate.opsForValue().set(gapKey, code, 60, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(SMS_CODE_CONTENT_PREFIX + telephone, code, 10, TimeUnit.MINUTES);
            return ServiceResultEntity.of(code);
        } else {
            LOGGER.error("send sms [{}] 服务忙，请稍后重试", telephone);
            return new ServiceResultEntity<>(false, "服务忙，请稍后重试");
        }
    }

    /**
     * 6位验证码生成器
     */
    private String generateRandomSmsCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            sb.append(NUMS[index]);
        }
        return sb.toString();
    }

    @Override
    public String getSmsCode(String telephone) {
        return this.redisTemplate.opsForValue().get(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    @Override
    public void remove(String telephone) {
        this.redisTemplate.delete(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, secertKey);

        String product = "Dysmsapi";
        String domain = "dysmsapi.aliyuncs.com";

        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        this.acsClient = new DefaultAcsClient(profile);
    }

}
