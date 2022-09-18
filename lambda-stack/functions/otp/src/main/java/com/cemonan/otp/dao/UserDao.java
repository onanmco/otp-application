package com.cemonan.otp.dao;

import com.cemonan.otp.domain.user.User;

public interface UserDao {

    User getByEmail(String email);
    User getByPhoneNumber(String phoneNumber);
    User insert(User user);
}
