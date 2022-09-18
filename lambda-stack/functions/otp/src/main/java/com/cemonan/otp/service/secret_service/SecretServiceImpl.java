package com.cemonan.otp.service.secret_service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretServiceImpl implements SecretService {

    @Autowired
    private AWSSecretsManager secretsManagerClient;

    @Override
    public String getSecretString(String secretId) {
        GetSecretValueRequest request = new GetSecretValueRequest()
                .withSecretId(secretId);
        return secretsManagerClient.getSecretValue(request)
                .getSecretString();
    }
}
