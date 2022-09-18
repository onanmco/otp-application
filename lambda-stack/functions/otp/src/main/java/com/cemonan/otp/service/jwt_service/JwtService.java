package com.cemonan.otp.service.jwt_service;

import java.util.UUID;

public interface JwtService {

    boolean isValid(String token);
    String create(UUID userId);
}
