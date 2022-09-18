package com.cemonan.otp.dao;

import com.cemonan.otp.domain.otp.Otp;

import java.util.UUID;

public interface OtpDao {

    Otp insert(Otp otp);
    Otp getById(UUID id);
    Otp update(Otp otp);
}
