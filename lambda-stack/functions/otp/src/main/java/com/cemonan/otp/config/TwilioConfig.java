package com.cemonan.otp.config;

import com.cemonan.otp.service.secret_service.SecretService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.twilio.http.TwilioRestClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Autowired
    private SecretService secretService;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Bean
    public TwilioRestClient getTwilioClient() {
        String secretString = secretService.getSecretString(System.getenv("TWILIO_SECRET_KEY"));
        ObjectNode credentials = objectMapper.readValue(secretString, ObjectNode.class);
        return new TwilioRestClient.Builder(
                credentials.get("account_sid").asText(),
                credentials.get("auth_token").asText()
        )
                .build();
    }
}
