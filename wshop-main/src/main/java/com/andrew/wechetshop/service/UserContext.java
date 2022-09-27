package com.andrew.wechetshop.service;

import com.andrew.wechetshop.generate.User;

public class UserContext {

    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static User getCurrentUser() {
        return threadLocal.get();
    }

    public static void setCurrentUser(User user) {
        threadLocal.set(user);
    }

    public static void clearCurrentUser() {
        threadLocal.remove();
    }
}
