package com.andrew.wechetshop.controller;

import com.andrew.wechetshop.entity.LoginResponse;
import com.andrew.wechetshop.generate.User;
import com.andrew.wechetshop.service.AuthService;
import com.andrew.wechetshop.service.TelVerificationService;
import com.andrew.wechetshop.service.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private final TelVerificationService telVerificationService;

    public AuthController(AuthService authService, TelVerificationService telVerificationService) {
        this.authService = authService;
        this.telVerificationService = telVerificationService;
    }

    @GetMapping("/status")
    public LoginResponse status() {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return LoginResponse.notLogin();
        } else {
            return LoginResponse.login(currentUser);
        }
    }

    @PostMapping("/code")
    public void code(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
        if (telVerificationService.isValidTelNumber(telAndCode)) {
            authService.sendVerificationCode(telAndCode.getTel());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode, HttpServletResponse httpServletResponse) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                telAndCode.getTel(),
                telAndCode.getCode());
        token.setRememberMe(true);
        try {
            SecurityUtils.getSubject().login(token);
        } catch (IncorrectCredentialsException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

    }

    @PostMapping("/logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    public static class TelAndCode {
        private String tel;
        private String code;

        public TelAndCode(String tel, String code) {
            this.tel = tel;
            this.code = code;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

}
