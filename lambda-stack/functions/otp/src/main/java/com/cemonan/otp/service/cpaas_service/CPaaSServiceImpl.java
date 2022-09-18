package com.cemonan.otp.service.cpaas_service;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CPaaSServiceImpl implements CpaaSService {

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Override
    public void sendSMS(String to, String message) {
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(System.getenv("FROM_PHONE_NUMBER")),
                message
        ).create(twilioRestClient);
    }
}
