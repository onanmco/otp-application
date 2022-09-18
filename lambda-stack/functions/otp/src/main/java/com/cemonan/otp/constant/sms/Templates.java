package com.cemonan.otp.constant.sms;

public class Templates {

    public static String getSmsTemplate(int otp) {
        return "Hello,\n" +
        "Your OTP code: " + otp + "\n\n" +
        "Thanks.";
    }
}
