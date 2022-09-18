package com.cemonan.otp.service.random_service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomServiceImpl implements RandomService {

    @Override
    public int getRandomOTP(int randomLength) {
        Random random = new Random(System.currentTimeMillis());
        int multiplier = (int) Math.pow(10, randomLength - 1);
        return multiplier + random.nextInt(9 * multiplier);
    }
}
