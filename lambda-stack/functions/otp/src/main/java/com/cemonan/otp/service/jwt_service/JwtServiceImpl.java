package com.cemonan.otp.service.jwt_service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.cemonan.otp.constant.domain.DomainConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private Algorithm algorithm;
    @Autowired
    private JWTVerifier verifier;

    @Override
    public boolean isValid(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTCreationException e) {
            return false;
        }
    }

    public String create(UUID userId) {
        return JWT.create()
                .withIssuer(DomainConstants.ISSUER)
                .withClaim("userId", userId.toString())
                .sign(algorithm);
    }
}
