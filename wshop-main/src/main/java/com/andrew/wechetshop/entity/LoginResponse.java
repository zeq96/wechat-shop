package com.andrew.wechetshop.entity;

import com.andrew.wechetshop.generate.User;

public class LoginResponse {
    private boolean login;
    private User user;

    public LoginResponse() {}

    private LoginResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public static LoginResponse login(User user) {
        return new LoginResponse(true, user);
    }

    public static LoginResponse notLogin() {
        return new LoginResponse(false, null);
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
