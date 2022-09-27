package com.andrew.wechetshop.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsCodeService implements SmsCodeService{
    @Override
    public String sendSmsCode(String tel) {
        return "000000";
    }
}
