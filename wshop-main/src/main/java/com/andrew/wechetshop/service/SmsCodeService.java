package com.andrew.wechetshop.service;

public interface SmsCodeService {
    /**
     * 向目标手机号发送验证码 返回正确答案
     * @param tel 目标手机号
     * @return 正确答案
     */
    String sendSmsCode(String tel);
}
