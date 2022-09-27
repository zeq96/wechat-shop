package com.andrew.wechetshop.service;

import com.andrew.wechetshop.dao.UserDao;
import com.andrew.wechetshop.generate.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            userDao.insertUser(user);
        } catch (DuplicateKeyException e) {
            return userDao.selectUserByTel(tel);
        }
        return user;
    }

    /**
     * 根据电话返回用户, 如果用户不存在, 返回null
     * @param tel 发验证码的用户手机号
     * @return 匹配的用户信息
     */
    public Optional<User> selectUserByTel(String tel) {
        return Optional.ofNullable(userDao.selectUserByTel(tel));
    }
}
