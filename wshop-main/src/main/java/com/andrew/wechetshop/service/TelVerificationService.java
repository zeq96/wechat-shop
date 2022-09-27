package com.andrew.wechetshop.service;

import com.andrew.wechetshop.controller.AuthController;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class TelVerificationService {
    private static final Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");

    /**
     * 验证输入的参数是否合法:
     * tel必须存在且为合法的中国大陆手机号(数字1开头, 11位)
     * @param param 输入的参数
     * @return 验证后的布尔值
     */
    public boolean isValidTelNumber(AuthController.TelAndCode param) {
        if (checkParam(param)) {
            return TEL_PATTERN.matcher(param.getTel()).find();
        } else {
            return false;
        }
    }

    private boolean checkParam(AuthController.TelAndCode telAndCode) {
        return telAndCode != null && telAndCode.getTel() != null;
    }
}
