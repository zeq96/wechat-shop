package com.andrew.wechetshop.dao;

import com.andrew.wechetshop.generate.User;
import com.andrew.wechetshop.generate.UserExample;
import com.andrew.wechetshop.generate.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private final UserMapper userMapper;

    @Autowired
    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void insertUser(User user) {
        userMapper.insert(user);
    }

    public User selectUserByTel(String tel) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTelEqualTo(tel);
        return userMapper.selectByExample(userExample).get(0);
    }
}
