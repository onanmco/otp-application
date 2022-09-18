package com.cemonan.otp.service.user_service;

import com.cemonan.otp.domain.user.User;

public interface UserService {

    User getByEmail(String email);
    User getByPhoneNumber(String phoneNumber);
    User insert(User user);
}
