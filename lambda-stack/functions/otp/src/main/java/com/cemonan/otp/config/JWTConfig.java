package com.cemonan.otp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.cemonan.otp.constant.domain.DomainConstants;
import com.cemonan.otp.service.secret_service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.cemonan.otp.service.secret_service"})
public class JWTConfig {

    @Autowired
    private SecretService secretService;

    @Bean
    public Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretService.getSecretString(System.getenv("SECRET_ID")));
    }

    @Bean
    public JWTVerifier getVerifier() {
        return JWT.require(this.getAlgorithm())
                .withIssuer(DomainConstants.ISSUER)
                .build();
    }
}
