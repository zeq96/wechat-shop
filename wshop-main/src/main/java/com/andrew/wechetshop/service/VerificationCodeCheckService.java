package com.andrew.wechetshop.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeCheckService {
    private Map<String, String> telNumber2VerificationCode = new ConcurrentHashMap<>();

    public void addCode(String tel, String verificationCode) {
        telNumber2VerificationCode.put(tel, verificationCode);
    }

    public String getVerificationCode(String tel) {
        return telNumber2VerificationCode.get(tel);
    }
}
