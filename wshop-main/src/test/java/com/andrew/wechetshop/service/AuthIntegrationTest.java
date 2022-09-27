package com.andrew.wechetshop.service;

import com.andrew.wechetshop.WechetShopApplication;
import com.andrew.wechetshop.entity.LoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.kevinsawicki.http.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.net.HttpURLConnection.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechetShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void returnUnauthorizedIfNotLogin() throws JsonProcessingException {
        HttpResponse httpResponse = doHttpRequest("/api/any", "POST", null, null);
        Assertions.assertEquals(HTTP_UNAUTHORIZED, httpResponse.code);
    }

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();
        String sessionId = getSessionIdFromSetCookie(userLoginResponse.cookie);

        // 带着Cookie访问 /api/status 处于登录状态
        HttpResponse httpResponse = doHttpRequest("/api/v1/status", "GET", null, sessionId);
        LoginResponse response = objectMapper.readValue(httpResponse.body, LoginResponse.class);
        Assertions.assertTrue(response.isLogin());
        Assertions.assertEquals(TelVerificationServiceTest.VALID_TEL_CODE.getTel(), response.getUser().getTel());

        // 调用 /api/logout 登出
        httpResponse = doHttpRequest("/api/v1/logout", "POST", null, sessionId);
        Assertions.assertEquals(HTTP_OK, httpResponse.code);

        // 再次带着Cookie访问 /api/status 恢复未登录状态
        httpResponse = doHttpRequest("/api/v1/status", "GET", null, null);
        response = objectMapper.readValue(httpResponse.body, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());
    }

    @Test
    public void returnOkWhenValidParam() throws JsonProcessingException {
        int code = HttpRequest.post(getUrl("/api/v1/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.VALID_TEL))
                .code();
        Assertions.assertEquals(HTTP_OK, code);
    }

    @Test
    public void returnBadRequestWhenInvalidParam() throws JsonProcessingException {
        int code = HttpRequest.post(getUrl("/api/v1/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.EMPTY_TEL))
                .code();
        Assertions.assertEquals(HTTP_BAD_REQUEST, code);
    }

}
