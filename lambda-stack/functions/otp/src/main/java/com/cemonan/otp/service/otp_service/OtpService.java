package com.cemonan.otp.service.otp_service;

import com.cemonan.otp.domain.otp.Otp;
import com.cemonan.otp.domain.user.User;

import java.util.UUID;

public interface OtpService {

    Otp createOtpAndSendSMS(User user);
    Otp getById(UUID id);
    Otp update(Otp otp);
}
