package com.cemonan.otp.service.user_service;

import com.cemonan.otp.dao.UserDao;
import com.cemonan.otp.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        return userDao.getByPhoneNumber(phoneNumber);
    }

    @Override
    public User insert(User user) {
        return userDao.insert(user);
    }
}
