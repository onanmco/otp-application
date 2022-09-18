package com.cemonan.otp.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretsManagerConfig {

    @Bean
    public AWSSecretsManager getSecretsManagerClient() {
        return AWSSecretsManagerClientBuilder.standard()
                .withRegion(System.getenv("AWS_REGION"))
                .build();
    }
}
