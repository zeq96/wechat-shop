package com.andrew.wechetshop.service;

import com.andrew.wechetshop.entity.LoginResponse;
import com.andrew.wechetshop.generate.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class AbstractIntegrationTest {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @BeforeEach
    public void setUp() {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(dbUrl, username, password);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
    }

    protected UserLoginResponse loginThenGetCookie() throws JsonProcessingException {
        // 1.最开始默认情况下 /api/status 处于未登录状态
        HttpResponse httpResponse = doHttpRequest("/api/v1/status", "GET", null, null);
        LoginResponse response = objectMapper.readValue(httpResponse.body, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());

        // 2.发送验证码
        httpResponse = doHttpRequest("/api/v1/code", "POST", TelVerificationServiceTest.VALID_TEL, null);
        Assertions.assertEquals(HTTP_OK, httpResponse.code);

        // 3.带着验证码进行登录, 得到Cookie
        httpResponse = doHttpRequest("/api/v1/login", "POST", TelVerificationServiceTest.VALID_TEL_CODE, null);
        Map<String, List<String>> headers = httpResponse.headers;
        String jsessionid = headers.get("Set-Cookie").stream()
                .filter(session -> session.contains("JSESSIONID"))
                .findFirst()
                .get();

        httpResponse = doHttpRequest("/api/v1/status", "GET", null, jsessionid);
        response = objectMapper.readValue(httpResponse.body, LoginResponse.class);

        return new UserLoginResponse(jsessionid, response.getUser());
    }

    protected HttpResponse doHttpRequest(String apiName, String httpMethod, Object requestBody, String cookie) throws JsonProcessingException {
        HttpRequest request = createHttpRequest(getUrl(apiName), httpMethod);

        request.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE);

        if (cookie != null) {
            request.header("Cookie", cookie);
        }
        if (requestBody != null) {
            request.send(objectMapper.writeValueAsString(requestBody));
        }
        return new HttpResponse(request.code(), request.body(), request.headers());
    }

    private HttpRequest createHttpRequest(String url, String method) {
        if ("PATCH".equalsIgnoreCase(method)) {
            // workaround for https://bugs.openjdk.java.net/browse/JDK-8207840
            HttpRequest request = new HttpRequest(url, "POST");
            request.header("X-HTTP-Method-Override", "PATCH");
            return request;
        } else {
            return new HttpRequest(url, method);
        }
    }

    protected String getUrl(String path) {
        return "http://localhost:" + environment.getProperty("local.server.port") + path;
    }

    protected String getSessionIdFromSetCookie(String setCookie) {
        // JSESSIONID=ab5ecb3e-55ae-4ca6-bc7e-e7ae828e1a12; Path=/; HttpOnly; SameSite=lax
        int index = setCookie.indexOf(";");
        return setCookie.substring(0, index);
    }

    protected static class UserLoginResponse {
        String cookie;
        User user;

        public UserLoginResponse(String cookie, User user) {
            this.cookie = cookie;
            this.user = user;
        }
    }

    protected static class HttpResponse {
        int code;
        String body;
        Map<String, List<String>> headers;

        HttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }

        HttpResponse assertOkStatusCode() {
            Assertions.assertTrue(code >= 200 && code < 300);
            return this;
        }

        public <T> T asJsonObject(TypeReference<T> typeReference) throws JsonProcessingException {
            return objectMapper.readValue(body, typeReference);
        }
    }
}
