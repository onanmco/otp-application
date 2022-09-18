package com.cemonan.otp.service.otp_service;

import com.cemonan.otp.constant.sms.Templates;
import com.cemonan.otp.dao.OtpDao;
import com.cemonan.otp.domain.otp.Otp;
import com.cemonan.otp.domain.user.User;
import com.cemonan.otp.service.cpaas_service.CpaaSService;
import com.cemonan.otp.service.random_service.RandomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private RandomService randomService;
    @Autowired
    private OtpDao otpDao;
    @Autowired
    private CpaaSService cpaaSService;

    @Override
    public Otp createOtpAndSendSMS(User user) {
        int otp = randomService.getRandomOTP(Integer.parseInt(System.getenv("OTP_LENGTH")));
        OffsetDateTime now = Instant.now()
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime expiresAt = now.plusSeconds(Long.parseLong(System.getenv("OTP_EXPIRES_IN_SECONDS")));
        Otp savedOtp = otpDao.insert(
                Otp.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .otp(otp)
                        .isUsed(false)
                        .createdAt(now)
                        .expiresAt(expiresAt)
                        .build()
        );
        cpaaSService.sendSMS(user.getPhoneNumber(), Templates.getSmsTemplate(otp));
        return savedOtp;
    }

    @Override
    public Otp getById(UUID id) {
        return otpDao.getById(id);
    }

    @Override
    public Otp update(Otp otp) {
        return otpDao.update(otp);
    }
}
